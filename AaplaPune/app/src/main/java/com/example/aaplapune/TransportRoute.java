package com.example.aaplapune;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransportRoute {

    private final String mode;
    private final String routeName;
    private final String from;
    private final String to;
    private final String duration;
    private final String fare;
    private final List<String> stops;

    public TransportRoute(
            String mode,
            String routeName,
            String from,
            String to,
            String duration,
            String fare,
            String... stops
    ) {

        this.mode = mode;
        this.routeName = routeName;
        this.from = from;
        this.to = to;
        this.duration = duration;
        this.fare = fare;

        this.stops =
                new ArrayList<>(
                        Arrays.asList(stops)
                );
    }

    public String getMode() {
        return mode;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getDuration() {
        return duration;
    }

    public String getFare() {
        return fare;
    }

    public List<String> getStops() {
        return stops;
    }
}