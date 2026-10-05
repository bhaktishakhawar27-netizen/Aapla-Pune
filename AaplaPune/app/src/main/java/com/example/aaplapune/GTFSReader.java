package com.example.aaplapune;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GTFSReader {

    private final Context context;

    // =====================================================
    // ROUTES
    // =====================================================

    private final Map<String, String> routeNames =
            new HashMap<>();

    // =====================================================
    // STOPS
    // stop_id -> stop_name
    // =====================================================

    private final Map<String, String> stopNames =
            new HashMap<>();

    // =====================================================
    // TRIPS
    // trip_id -> route_id
    // =====================================================

    private final Map<String, String> tripRoutes =
            new HashMap<>();

    // =====================================================
    // TRIP STOPS
    // trip_id -> ordered StopTime list
    // =====================================================

    private final Map<String, List<StopTime>> tripStops =
            new HashMap<>();


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public GTFSReader(Context context) {
        this.context = context;
    }


    // =====================================================
    // LOAD ALL GTFS DATA
    // =====================================================

    public void loadData() {

        try {

            routeNames.clear();
            stopNames.clear();
            tripRoutes.clear();
            tripStops.clear();

            loadRoutes();
            loadStops();
            loadTrips();
            loadStopTimes();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // =====================================================
    // ROUTES.TXT
    // =====================================================

    private void loadRoutes()
            throws Exception {

        BufferedReader reader =
                openFile("routes.txt");

        String line;
        boolean firstLine = true;

        while ((line = reader.readLine()) != null) {

            if (line.trim().isEmpty()) {
                continue;
            }

            if (firstLine) {
                firstLine = false;
                continue;
            }

            String[] columns =
                    splitCSV(line);

            if (columns.length < 2) {
                continue;
            }

            String routeId =
                    columns[0].trim();

            String routeName =
                    routeId;

            if (columns.length > 2 &&
                    !columns[2].trim().isEmpty()) {

                routeName =
                        columns[2].trim();
            }

            if (columns.length > 3 &&
                    !columns[3].trim().isEmpty()) {

                if (!routeName.equals(routeId)) {

                    routeName =
                            routeName +
                                    " - " +
                                    columns[3].trim();

                } else {

                    routeName =
                            columns[3].trim();
                }
            }

            routeNames.put(
                    routeId,
                    routeName
            );
        }

        reader.close();
    }


    // =====================================================
    // STOPS.TXT
    // =====================================================

    private void loadStops()
            throws Exception {

        BufferedReader reader =
                openFile("stops.txt");

        String line;
        boolean firstLine = true;

        while ((line = reader.readLine()) != null) {

            if (line.trim().isEmpty()) {
                continue;
            }

            if (firstLine) {
                firstLine = false;
                continue;
            }

            String[] columns =
                    splitCSV(line);

            if (columns.length < 2) {
                continue;
            }

            String stopId =
                    columns[0].trim();

            String stopName =
                    columns[1].trim();

            if (!stopId.isEmpty() &&
                    !stopName.isEmpty()) {

                stopNames.put(
                        stopId,
                        stopName
                );
            }
        }

        reader.close();
    }


    // =====================================================
    // TRIPS.TXT
    // =====================================================

    private void loadTrips()
            throws Exception {

        BufferedReader reader =
                openFile("trips.txt");

        String line;
        boolean firstLine = true;

        while ((line = reader.readLine()) != null) {

            if (line.trim().isEmpty()) {
                continue;
            }

            if (firstLine) {
                firstLine = false;
                continue;
            }

            String[] columns =
                    splitCSV(line);

            if (columns.length < 3) {
                continue;
            }

            String routeId =
                    columns[0].trim();

            String tripId =
                    columns[2].trim();

            if (routeId.isEmpty() ||
                    tripId.isEmpty()) {

                continue;
            }

            tripRoutes.put(
                    tripId,
                    routeId
            );

            tripStops.put(
                    tripId,
                    new ArrayList<>()
            );
        }

        reader.close();
    }


    // =====================================================
    // STOP_TIMES.TXT
    // =====================================================

    private void loadStopTimes()
            throws Exception {

        BufferedReader reader =
                openFile("stop_times.txt");

        String line;
        boolean firstLine = true;

        while ((line = reader.readLine()) != null) {

            if (line.trim().isEmpty()) {
                continue;
            }

            if (firstLine) {
                firstLine = false;
                continue;
            }

            String[] columns =
                    splitCSV(line);

            if (columns.length < 5) {
                continue;
            }

            String tripId =
                    columns[0].trim();

            String arrivalTime =
                    columns[1].trim();

            String departureTime =
                    columns[2].trim();

            String stopId =
                    columns[3].trim();

            int stopSequence;

            try {

                stopSequence =
                        Integer.parseInt(
                                columns[4].trim()
                        );

            } catch (Exception e) {

                continue;
            }

            List<StopTime> stops =
                    tripStops.get(tripId);

            if (stops == null) {

                stops =
                        new ArrayList<>();

                tripStops.put(
                        tripId,
                        stops
                );
            }

            stops.add(
                    new StopTime(
                            stopId,
                            stopSequence,
                            arrivalTime,
                            departureTime
                    )
            );
        }

        reader.close();

        // =================================================
        // SORT EACH TRIP BY STOP SEQUENCE
        // =================================================

        for (List<StopTime> stops :
                tripStops.values()) {

            Collections.sort(
                    stops,
                    Comparator.comparingInt(
                            stop -> stop.sequence
                    )
            );
        }
    }


    // =====================================================
    // GET ROUTE NAMES
    // =====================================================

    public List<String> getRouteNames() {

        return new ArrayList<>(
                routeNames.values()
        );
    }


    // =====================================================
    // GET STOP NAMES
    // =====================================================

    public List<String> getStopNames() {

        /*
         * Use LinkedHashMap so duplicate names
         * are removed while preserving order.
         */

        Map<String, String> unique =
                new LinkedHashMap<>();

        for (String name :
                stopNames.values()) {

            if (name == null) {
                continue;
            }

            String cleanName =
                    name.trim();

            if (!cleanName.isEmpty()) {

                unique.put(
                        cleanName.toLowerCase(),
                        cleanName
                );
            }
        }

        List<String> result =
                new ArrayList<>(
                        unique.values()
                );

        Collections.sort(
                result,
                String.CASE_INSENSITIVE_ORDER
        );

        return result;
    }


    // =====================================================
    // SEARCH STOPS
    // =====================================================

    public List<String> searchStops(
            String query
    ) {

        List<String> results =
                new ArrayList<>();

        if (query == null ||
                query.trim().isEmpty()) {

            return results;
        }

        String search =
                query.trim()
                        .toLowerCase();

        for (String stopName :
                getStopNames()) {

            if (stopName
                    .toLowerCase()
                    .contains(search)) {

                results.add(
                        stopName
                );
            }
        }

        return results;
    }


    // =====================================================
    // GET STOPS FOR TRIP
    // =====================================================

    public List<String> getStopsForTrip(
            String tripId
    ) {

        List<String> result =
                new ArrayList<>();

        List<StopTime> stops =
                tripStops.get(tripId);

        if (stops == null) {
            return result;
        }

        String lastStop = null;

        for (StopTime stop :
                stops) {

            String stopName =
                    stopNames.get(
                            stop.stopId
                    );

            if (stopName == null) {
                continue;
            }

            stopName =
                    stopName.trim();

            /*
             * Prevent the same stop from appearing
             * consecutively.
             */

            if (lastStop != null &&
                    lastStop.equalsIgnoreCase(
                            stopName
                    )) {

                continue;
            }

            result.add(
                    stopName
            );

            lastStop =
                    stopName;
        }

        return result;
    }


    // =====================================================
    // GET ROUTE FOR TRIP
    // =====================================================

    public String getRouteForTrip(
            String tripId
    ) {

        String routeId =
                tripRoutes.get(tripId);

        if (routeId == null) {
            return null;
        }

        String routeName =
                routeNames.get(routeId);

        if (routeName == null) {
            return routeId;
        }

        return routeName;
    }


    // =====================================================
    // FIND DIRECT PMPML ROUTES
    // =====================================================

    public List<RouteResult> findDirectRoutes(
            String fromStopName,
            String toStopName
    ) {

        List<RouteResult> results =
                new ArrayList<>();

        if (fromStopName == null ||
                toStopName == null) {

            return results;
        }

        String from =
                fromStopName.trim();

        String to =
                toStopName.trim();

        // =================================================
        // CHECK EVERY TRIP
        // =================================================

        for (Map.Entry<String,
                List<StopTime>> entry :
                tripStops.entrySet()) {

            String tripId =
                    entry.getKey();

            List<StopTime> stops =
                    entry.getValue();

            if (stops == null ||
                    stops.isEmpty()) {

                continue;
            }

            int fromIndex = -1;
            int toIndex = -1;

            // =============================================
            // FIND FROM AND TO
            // =============================================

            for (int i = 0;
                 i < stops.size();
                 i++) {

                StopTime stop =
                        stops.get(i);

                String stopName =
                        stopNames.get(
                                stop.stopId
                        );

                if (stopName == null) {
                    continue;
                }

                stopName =
                        stopName.trim();

                if (stopName.equalsIgnoreCase(from)
                        && fromIndex == -1) {

                    fromIndex = i;
                }

                if (stopName.equalsIgnoreCase(to)
                        && toIndex == -1) {

                    toIndex = i;
                }
            }

            // =============================================
            // FROM MUST COME BEFORE TO
            // =============================================

            if (fromIndex >= 0 &&
                    toIndex >= 0 &&
                    fromIndex < toIndex) {

                String routeName =
                        getRouteForTrip(
                                tripId
                        );

                if (routeName == null) {
                    continue;
                }

                // =========================================
                // BUILD CLEAN STOP LIST
                // =========================================

                List<String> journeyStops =
                        new ArrayList<>();

                String lastStopName = null;

                for (int i = fromIndex;
                     i <= toIndex;
                     i++) {

                    StopTime stop =
                            stops.get(i);

                    String stopName =
                            stopNames.get(
                                    stop.stopId
                            );

                    if (stopName == null) {
                        continue;
                    }

                    stopName =
                            stopName.trim();

                    /*
                     * Remove repeated consecutive stops.
                     */

                    if (lastStopName != null &&
                            lastStopName.equalsIgnoreCase(
                                    stopName
                            )) {

                        continue;
                    }

                    /*
                     * Also prevent the same stop name
                     * from appearing twice in the
                     * same journey.
                     */

                    if (!journeyStops.contains(
                            stopName
                    )) {

                        journeyStops.add(
                                stopName
                        );
                    }

                    lastStopName =
                            stopName;
                }

                if (journeyStops.size() >= 2) {

                    results.add(
                            new RouteResult(
                                    tripId,
                                    routeName,
                                    journeyStops
                            )
                    );
                }
            }
        }


        // =================================================
        // REMOVE DUPLICATE ROUTES
        // =================================================

        Map<String, RouteResult>
                uniqueRoutes =
                new LinkedHashMap<>();

        for (RouteResult result :
                results) {

            /*
             * Build a unique key from:
             *
             * route name
             * complete stop sequence
             */

            StringBuilder keyBuilder =
                    new StringBuilder();

            keyBuilder.append(
                    result.routeName
            );

            keyBuilder.append("|");

            for (String stop :
                    result.stops) {

                keyBuilder.append(
                        stop.toLowerCase()
                );

                keyBuilder.append(
                        ">"
                );
            }

            String key =
                    keyBuilder.toString();

            if (!uniqueRoutes.containsKey(key)) {

                uniqueRoutes.put(
                        key,
                        result
                );
            }
        }


        return new ArrayList<>(
                uniqueRoutes.values()
        );
    }


    // =====================================================
    // CHECK GTFS FILES
    // =====================================================

    public boolean hasGTFSFiles() {

        try {

            AssetManager assets =
                    context.getAssets();

            assets.open(
                    "gtfs/routes.txt"
            ).close();

            assets.open(
                    "gtfs/stops.txt"
            ).close();

            assets.open(
                    "gtfs/trips.txt"
            ).close();

            assets.open(
                    "gtfs/stop_times.txt"
            ).close();

            return true;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }


    // =====================================================
    // OPEN ASSET FILE
    // =====================================================

    private BufferedReader openFile(
            String fileName
    ) throws Exception {

        AssetManager assets =
                context.getAssets();

        InputStream inputStream =
                assets.open(
                        "gtfs/" + fileName
                );

        return new BufferedReader(
                new InputStreamReader(
                        inputStream,
                        "UTF-8"
                )
        );
    }


    // =====================================================
    // CSV PARSER
    // =====================================================

    private String[] splitCSV(
            String line
    ) {

        List<String> values =
                new ArrayList<>();

        StringBuilder current =
                new StringBuilder();

        boolean insideQuotes =
                false;

        for (int i = 0;
             i < line.length();
             i++) {

            char character =
                    line.charAt(i);

            if (character == '"') {

                insideQuotes =
                        !insideQuotes;

            } else if (
                    character == ','
                            && !insideQuotes
            ) {

                values.add(
                        current
                                .toString()
                                .trim()
                );

                current.setLength(0);

            } else {

                current.append(
                        character
                );
            }
        }

        values.add(
                current
                        .toString()
                        .trim()
        );

        return values.toArray(
                new String[0]
        );
    }


    // =====================================================
    // STOP TIME
    // =====================================================

    public static class StopTime {

        String stopId;

        int sequence;

        String arrivalTime;

        String departureTime;


        public StopTime(
                String stopId,
                int sequence,
                String arrivalTime,
                String departureTime
        ) {

            this.stopId =
                    stopId;

            this.sequence =
                    sequence;

            this.arrivalTime =
                    arrivalTime;

            this.departureTime =
                    departureTime;
        }
    }


    // =====================================================
    // ROUTE RESULT
    // =====================================================

    public static class RouteResult {

        public String tripId;

        public String routeName;

        public List<String> stops;


        public RouteResult(
                String tripId,
                String routeName,
                List<String> stops
        ) {

            this.tripId =
                    tripId;

            this.routeName =
                    routeName;

            this.stops =
                    stops;
        }
    }
}