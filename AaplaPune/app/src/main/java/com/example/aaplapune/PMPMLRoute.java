
package com.example.aaplapune;

import java.util.List;

public class PMPMLRoute {

    private String routeNumber;
    private String routeName;
    private String startPoint;
    private String endPoint;
    private List<String> stops;

    public PMPMLRoute(
            String routeNumber,
            String routeName,
            String startPoint,
            String endPoint,
            List<String> stops
    ) {

        this.routeNumber = routeNumber;
        this.routeName = routeName;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.stops = stops;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public List<String> getStops() {
        return stops;
    }
}