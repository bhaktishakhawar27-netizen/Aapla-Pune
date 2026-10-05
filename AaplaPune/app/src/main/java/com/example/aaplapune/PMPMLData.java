package com.example.aaplapune;

import java.util.ArrayList;
import java.util.List;

public class PMPMLData {

    public static final List<PMPMLRoute> routes =
            new ArrayList<>();

    // Add PMPML routes here.
    // Each route will contain:
    // Route number
    // Route name
    // Starting stop
    // Ending stop
    // Complete stop sequence


    // ==========================================
    // GET ALL UNIQUE PMPML STOPS
    // ==========================================

    public static List<String> getAllStops() {

        List<String> allStops =
                new ArrayList<>();

        for (PMPMLRoute route : routes) {

            for (String stop : route.getStops()) {

                if (!allStops.contains(stop)) {
                    allStops.add(stop);
                }
            }
        }

        return allStops;
    }


    // ==========================================
    // FIND ROUTES BETWEEN TWO STOPS
    // ==========================================

    public static List<PMPMLRoute> findRoutes(
            String from,
            String to
    ) {

        List<PMPMLRoute> matchingRoutes =
                new ArrayList<>();

        for (PMPMLRoute route : routes) {

            List<String> stops =
                    route.getStops();

            int fromIndex = -1;
            int toIndex = -1;

            for (int i = 0; i < stops.size(); i++) {

                if (stops.get(i)
                        .equalsIgnoreCase(from)) {

                    fromIndex = i;
                }

                if (stops.get(i)
                        .equalsIgnoreCase(to)) {

                    toIndex = i;
                }
            }

            if (
                    fromIndex != -1 &&
                            toIndex != -1 &&
                            fromIndex < toIndex
            ) {

                matchingRoutes.add(route);
            }
        }

        return matchingRoutes;
    }


    // ==========================================
    // SEARCH STOPS
    // ==========================================

    public static List<String> searchStops(
            String query
    ) {

        List<String> results =
                new ArrayList<>();

        if (query == null ||
                query.trim().isEmpty()) {

            return results;
        }

        String search =
                query.trim().toLowerCase();

        for (String stop : getAllStops()) {

            if (stop.toLowerCase()
                    .contains(search)) {

                results.add(stop);
            }
        }

        return results;
    }
}