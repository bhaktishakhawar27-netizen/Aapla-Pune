package com.example.aaplapune;

import java.util.ArrayList;
import java.util.List;

public class TransportData {

    public static final List<TransportRoute> routes =
            new ArrayList<>();


    static {

        // =================================================
        // METRO
        // =================================================

        routes.add(
                new TransportRoute(
                        "Metro",
                        "Metro Route",
                        "PCMC",
                        "Swargate",
                        "Approx. journey time",
                        "Metro fare",
                        "PCMC",
                        "Sant Tukaram Nagar",
                        "Bhosari",
                        "Kasarwadi",
                        "Phugewadi",
                        "Dapodi",
                        "Bopodi",
                        "Khadki",
                        "Shivajinagar",
                        "Civil Court",
                        "Budhwar Peth",
                        "Mandai",
                        "Swargate"
                )
        );


        // =================================================
        // BUS
        // =================================================

        routes.add(
                new TransportRoute(
                        "Bus",
                        "Bus Route",
                        "Swargate",
                        "PCMC",
                        "Approx. journey time",
                        "Bus fare",
                        "Swargate",
                        "Dhulya Maruti",
                        "Shivajinagar",
                        "Khadki",
                        "Dapodi",
                        "Bhosari",
                        "PCMC"
                )
        );


        // =================================================
        // AUTO
        // =================================================

        routes.add(
                new TransportRoute(
                        "Auto",
                        "Auto Route",
                        "Swargate",
                        "PCMC",
                        "Approx. journey time",
                        "Estimated fare",
                        "Swargate",
                        "PCMC"
                )
        );
    }
}