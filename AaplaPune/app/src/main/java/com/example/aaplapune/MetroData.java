package com.example.aaplapune;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Pune Metro static data + timetable logic.
 * Source: pune_metro_detailed_info.txt
 *
 * NOTE: There is no public live-GPS feed for Pune Metro, so "live" arrival
 * times are calculated from the official timetable rules (service window,
 * headway, dwell time) against the current clock time.
 */
public class MetroData {

    // =====================================================
    // LINE MODEL
    // =====================================================

    public static class Line {

        public final String name;
        public final int color;
        public final String from;
        public final String to;
        public final boolean operational;
        public final String statusText;
        public final List<String> stations;

        Line(
                String name,
                int color,
                boolean operational,
                String statusText,
                String... stations
        ) {
            this.name = name;
            this.color = color;
            this.operational = operational;
            this.statusText = statusText;
            this.stations = Arrays.asList(stations);
            this.from = stations[0];
            this.to = stations[stations.length - 1];
        }
    }

    // =====================================================
    // LINES & COLOUR CODES
    // =====================================================

    public static final int PURPLE = 0xFF7B2D8E;
    public static final int AQUA = 0xFF00A9C0;
    public static final int PINK = 0xFFE91E8C;

    public static final List<Line> LINES = new ArrayList<>();

    static {

        LINES.add(new Line(
                "Purple Line",
                PURPLE,
                true,
                "Fully Operational",
                "PCMC",
                "Sant Tukaram Nagar",
                "Bhosari (Nashik Phata)",
                "Kasarwadi",
                "Phugewadi",
                "Dapodi",
                "Bopodi",
                "Khadki",
                "Range Hill",
                "Shivajinagar",
                "Civil Court",
                "Budhwar Peth",
                "Mandai",
                "Swargate"
        ));

        LINES.add(new Line(
                "Aqua Line",
                AQUA,
                true,
                "Fully Operational",
                "Vanaz",
                "Anand Nagar",
                "Ideal Colony",
                "Nal Stop",
                "Garware College",
                "Deccan Gymkhana",
                "Chhatrapati Sambhaji Udyan",
                "PMC",
                "Civil Court",
                "Mangalwar Peth",
                "Pune Railway Station",
                "Ruby Hall Clinic",
                "Bund Garden",
                "Yerawada",
                "Kalyani Nagar",
                "Ramwadi"
        ));

        LINES.add(new Line(
                "Pink Line",
                PINK,
                false,
                "Under Construction / Phased Launch",
                "Megapolis Circle",
                "Quadron",
                "Wipro Technologies",
                "Shivaji Chowk",
                "Wakad Chowk",
                "Balewadi Stadium",
                "Baner",
                "SPPU (Pune University)",
                "Civil Court"
        ));
    }

    // =====================================================
    // TIMINGS
    // =====================================================

    /** 06:00 AM */
    public static final int SERVICE_START = 6 * 3600;

    /** 11:00 PM */
    public static final int SERVICE_END = 23 * 3600;

    public static final int PEAK_HEADWAY = 5 * 60;
    public static final int OFFPEAK_HEADWAY = 10 * 60;

    /** Trains stop exactly 30 seconds at each platform. */
    public static final int DWELL_SECONDS = 30;

    /**
     * Approx. running time between two stations (assumption - not in the
     * source file). Adjust here if you get official inter-station timings.
     */
    public static final int RUN_SECONDS = 120;

    /** Time from one station to the next, including dwell. */
    public static final int HOP_SECONDS = RUN_SECONDS + DWELL_SECONDS;

    // =====================================================
    // FARES / BOOKING
    // =====================================================

    public static final String FARE_INFO =
            "Fares range from ₹10 to ₹35.\n"
                    + "10% discount on digital bookings on weekdays.";

    public static final String BOOKING_INFO =
            "• Official app: \"Pune Metro (Official App)\" on Play Store & App Store\n"
                    + "• WhatsApp e-ticket: send \"Hi\" to +91 94201 01990 and pay via UPI";

    // =====================================================
    // HELPERS
    // =====================================================

    /** Peak: 08:00-11:00 and 16:00-20:00. */
    public static boolean isPeak(int secOfDay) {

        return (secOfDay >= 8 * 3600 && secOfDay < 11 * 3600)
                || (secOfDay >= 16 * 3600 && secOfDay < 20 * 3600);
    }

    public static int headway(int secOfDay) {

        return isPeak(secOfDay) ? PEAK_HEADWAY : OFFPEAK_HEADWAY;
    }

    public static boolean isServiceOpen(int secOfDay) {

        return secOfDay >= SERVICE_START && secOfDay <= SERVICE_END;
    }

    /**
     * Number of lines (any status) that contain this station name.
     * Used to flag interchange stations such as Civil Court.
     */
    public static boolean isInterchange(String station) {

        int count = 0;

        for (Line line : LINES) {

            if (line.stations.contains(station)) {
                count++;
            }
        }

        return count > 1;
    }

    /**
     * Upcoming train arrival times (seconds since midnight, today).
     * Values >= 86400 belong to tomorrow's first trains.
     *
     * @param stationIndex index of the station on the line
     * @param stationCount number of stations on the line
     * @param forward      true = towards last station, false = towards first
     * @param nowSec       current time, seconds since midnight
     * @param count        how many arrivals to return
     */
    public static List<Integer> upcomingArrivals(
            int stationIndex,
            int stationCount,
            boolean forward,
            int nowSec,
            int count
    ) {

        List<Integer> result = new ArrayList<>();

        // Hops travelled from the terminus where the train starts
        int hops = forward
                ? stationIndex
                : (stationCount - 1 - stationIndex);

        int offset = hops * HOP_SECONDS;

        for (int day = 0; day < 2; day++) {

            int departure = SERVICE_START;

            while (departure <= SERVICE_END) {

                int arrival = day * 86400 + departure + offset;

                if (arrival >= nowSec) {

                    result.add(arrival);

                    if (result.size() >= count) {
                        return result;
                    }
                }

                departure += headway(departure);
            }
        }

        return result;
    }

    /** "6:05 AM" style. */
    public static String formatClock(int secOfDay) {

        int s = secOfDay % 86400;

        int h = s / 3600;
        int m = (s % 3600) / 60;

        String suffix = h >= 12 ? "PM" : "AM";

        int h12 = h % 12;

        if (h12 == 0) {
            h12 = 12;
        }

        return h12 + ":" + (m < 10 ? "0" + m : String.valueOf(m)) + " " + suffix;
    }

    /** "Arriving now", "45 sec", "3 min 20 sec". */
    public static String formatWait(int seconds) {

        if (seconds <= 10) {
            return "Arriving now";
        }

        int m = seconds / 60;
        int s = seconds % 60;

        if (m == 0) {
            return s + " sec";
        }

        if (m >= 60) {
            return (m / 60) + " hr " + (m % 60) + " min";
        }

        return m + " min " + s + " sec";
    }
}
