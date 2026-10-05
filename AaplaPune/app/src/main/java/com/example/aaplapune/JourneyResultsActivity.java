package com.example.aaplapune;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class JourneyResultsActivity extends AppCompatActivity {

    TextView backButton;
    TextView fromText;
    TextView toText;

    LinearLayout metroCard;
    LinearLayout busCard;
    LinearLayout autoCard;

    String from;
    String to;

    private GTFSReader gtfsReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_journey_results
        );

        // =================================================
        // FIND VIEWS
        // =================================================

        backButton =
                findViewById(
                        R.id.resultsBackButton
                );

        fromText =
                findViewById(
                        R.id.resultsFrom
                );

        toText =
                findViewById(
                        R.id.resultsTo
                );

        metroCard =
                findViewById(
                        R.id.metroCard
                );

        busCard =
                findViewById(
                        R.id.busCard
                );

        autoCard =
                findViewById(
                        R.id.autoCard
                );


        // =================================================
        // GET FROM / TO
        // =================================================

        from =
                getIntent().getStringExtra(
                        "FROM_LOCATION"
                );

        to =
                getIntent().getStringExtra(
                        "TO_LOCATION"
                );


        if (from == null) {
            from = "";
        }

        if (to == null) {
            to = "";
        }


        // =================================================
        // SHOW FROM / TO
        // =================================================

        fromText.setText(from);

        toText.setText(to);


        // =================================================
        // LOAD PMPML GTFS DATA
        // =================================================

        gtfsReader =
                new GTFSReader(this);

        gtfsReader.loadData();


        // =================================================
        // BACK
        // =================================================

        backButton.setOnClickListener(
                v -> finish()
        );


        // =================================================
        // METRO
        // =================================================

        metroCard.setOnClickListener(
                v -> openRoute("Metro")
        );


        // =================================================
        // PMPML BUS
        // =================================================

        busCard.setOnClickListener(
                v -> showPMPMLBusRoutes()
        );


        // =================================================
        // AUTO
        // =================================================

        autoCard.setOnClickListener(
                v -> openRoute("Auto")
        );
    }


    // =====================================================
    // FIND ALL PMPML BUS ROUTES
    // =====================================================

    private void showPMPMLBusRoutes() {

        // -------------------------------------------------
        // CHECK INPUT
        // -------------------------------------------------

        if (from.isEmpty() ||
                to.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please select From and To locations",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // -------------------------------------------------
        // SEARCH ACTUAL GTFS DATA
        // -------------------------------------------------

        List<GTFSReader.RouteResult> routes =
                gtfsReader.findDirectRoutes(
                        from,
                        to
                );


        // -------------------------------------------------
        // NO ROUTE
        // -------------------------------------------------

        if (routes == null ||
                routes.isEmpty()) {

            Toast.makeText(
                    this,
                    "No direct PMPML bus found for these stops",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // -------------------------------------------------
        // ONLY ONE ROUTE
        // -------------------------------------------------

        if (routes.size() == 1) {

            openPMPMLRoute(
                    routes.get(0)
            );

            return;
        }


        // -------------------------------------------------
        // MULTIPLE ROUTES
        // -------------------------------------------------

        String[] routeNames =
                new String[routes.size()];


        for (int i = 0;
             i < routes.size();
             i++) {

            GTFSReader.RouteResult route =
                    routes.get(i);


            String routeName =
                    route.routeName;


            if (routeName == null ||
                    routeName.trim().isEmpty()) {

                routeName =
                        "PMPML Route " +
                                (i + 1);
            }


            routeNames[i] =
                    routeName;
        }


        // -------------------------------------------------
        // SHOW ROUTE SELECTION
        // -------------------------------------------------

        new AlertDialog.Builder(this)

                .setTitle(
                        "PMPML Routes Found"
                )

                .setItems(
                        routeNames,
                        (dialog, which) -> {

                            GTFSReader.RouteResult
                                    selectedRoute =
                                    routes.get(which);

                            openPMPMLRoute(
                                    selectedRoute
                            );
                        }
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .show();
    }


    // =====================================================
    // OPEN SELECTED PMPML ROUTE
    // =====================================================

    private void openPMPMLRoute(
            GTFSReader.RouteResult route
    ) {

        if (route == null) {

            Toast.makeText(
                    this,
                    "Unable to open route",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        Intent intent =
                new Intent(
                        JourneyResultsActivity.this,
                        RouteDetailsActivity.class
                );


        // -------------------------------------------------
        // FROM
        // -------------------------------------------------

        intent.putExtra(
                "FROM_LOCATION",
                from
        );


        // -------------------------------------------------
        // TO
        // -------------------------------------------------

        intent.putExtra(
                "TO_LOCATION",
                to
        );


        // -------------------------------------------------
        // TRANSPORT MODE
        // -------------------------------------------------

        intent.putExtra(
                "TRANSPORT_MODE",
                "PMPML Bus"
        );


        // -------------------------------------------------
        // ROUTE NAME
        // -------------------------------------------------

        if (route.routeName != null) {

            intent.putExtra(
                    "ROUTE_NAME",
                    route.routeName
            );
        }


        // -------------------------------------------------
        // TRIP ID
        // -------------------------------------------------

        if (route.tripId != null) {

            intent.putExtra(
                    "TRIP_ID",
                    route.tripId
            );
        }


        // -------------------------------------------------
        // ACTUAL ROUTE STOPS
        // -------------------------------------------------

        if (route.stops != null &&
                !route.stops.isEmpty()) {

            StringBuilder stops =
                    new StringBuilder();


            for (int i = 0;
                 i < route.stops.size();
                 i++) {

                String stop =
                        route.stops.get(i);


                if (stop == null ||
                        stop.trim().isEmpty()) {

                    continue;
                }


                stops.append(
                        stop.trim()
                );


                if (i <
                        route.stops.size() - 1) {

                    stops.append(
                            "\n"
                    );
                }
            }


            intent.putExtra(
                    "ROUTE_STOPS",
                    stops.toString()
            );
        }


        // -------------------------------------------------
        // OPEN DETAILS
        // -------------------------------------------------

        startActivity(intent);
    }


    // =====================================================
    // METRO / AUTO
    // =====================================================

    private void openRoute(
            String transportMode
    ) {

        Intent intent =
                new Intent(
                        JourneyResultsActivity.this,
                        RouteDetailsActivity.class
                );


        intent.putExtra(
                "FROM_LOCATION",
                from
        );


        intent.putExtra(
                "TO_LOCATION",
                to
        );


        intent.putExtra(
                "TRANSPORT_MODE",
                transportMode
        );


        startActivity(intent);
    }
}