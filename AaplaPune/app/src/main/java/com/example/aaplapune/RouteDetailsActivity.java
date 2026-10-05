package com.example.aaplapune;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RouteDetailsActivity extends AppCompatActivity {

    TextView backButton;
    TextView routeTitle;
    TextView transportType;
    TextView routeFrom;
    TextView routeTo;

    LinearLayout stopsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_route_details
        );

        // =================================================
        // FIND VIEWS
        // =================================================

        backButton =
                findViewById(
                        R.id.routeBackButton
                );

        routeTitle =
                findViewById(
                        R.id.routeTitle
                );

        transportType =
                findViewById(
                        R.id.transportType
                );

        routeFrom =
                findViewById(
                        R.id.routeFrom
                );

        routeTo =
                findViewById(
                        R.id.routeTo
                );

        stopsContainer =
                findViewById(
                        R.id.stopsContainer
                );


        // =================================================
        // GET JOURNEY DATA
        // =================================================

        String from =
                getIntent().getStringExtra(
                        "FROM_LOCATION"
                );

        String to =
                getIntent().getStringExtra(
                        "TO_LOCATION"
                );

        String mode =
                getIntent().getStringExtra(
                        "TRANSPORT_MODE"
                );

        String routeName =
                getIntent().getStringExtra(
                        "ROUTE_NAME"
                );

        String routeStops =
                getIntent().getStringExtra(
                        "ROUTE_STOPS"
                );


        // =================================================
        // NULL SAFETY
        // =================================================

        if (from == null) {
            from = "";
        }

        if (to == null) {
            to = "";
        }

        if (mode == null) {
            mode = "Metro";
        }


        // =================================================
        // SHOW FROM / TO
        // =================================================

        routeFrom.setText(from);

        routeTo.setText(to);


        // =================================================
        // SHOW TRANSPORT TYPE
        // =================================================

        transportType.setText(
                mode.toUpperCase()
        );


        // =================================================
        // SHOW ROUTE NAME
        // =================================================

        if (routeName != null &&
                !routeName.trim().isEmpty()) {

            routeTitle.setText(
                    routeName
            );

        } else {

            routeTitle.setText(
                    mode + " Route"
            );
        }


        // =================================================
        // SHOW ACTUAL PMPML STOPS
        // =================================================

        if (routeStops != null &&
                !routeStops.trim().isEmpty()) {

            displayRouteStops(
                    routeStops
            );

        } else {

            displayFallbackStops(
                    from,
                    to
            );
        }


        // =================================================
        // BACK BUTTON
        // =================================================

        backButton.setOnClickListener(
                v -> finish()
        );
    }


    // =====================================================
    // DISPLAY ACTUAL ROUTE STOPS
    // =====================================================

    private void displayRouteStops(
            String routeStops
    ) {

        stopsContainer.removeAllViews();


        String[] stops =
                routeStops.split(
                        "\\n"
                );


        int number = 1;


        for (String stop :
                stops) {

            if (stop == null) {
                continue;
            }

            stop =
                    stop.trim();


            if (stop.isEmpty()) {
                continue;
            }


            TextView stopView =
                    new TextView(this);


            stopView.setText(
                    number +
                            ".  " +
                            stop
            );


            stopView.setTextSize(
                    16
            );


            stopView.setTextColor(
                    getColor(
                            R.color.route_text
                    )
            );


            stopView.setPadding(
                    18,
                    18,
                    18,
                    18
            );


            stopsContainer.addView(
                    stopView
            );


            number++;
        }
    }


    // =====================================================
    // FALLBACK
    // =====================================================

    private void displayFallbackStops(
            String from,
            String to
    ) {

        stopsContainer.removeAllViews();


        TextView fallback =
                new TextView(this);


        fallback.setText(
                "1.  " +
                        from +
                        "\n\n↓\n\n" +
                        "2.  " +
                        to
        );


        fallback.setTextSize(
                16
        );


        fallback.setTextColor(
                getColor(
                        R.color.route_text
                )
        );


        fallback.setPadding(
                18,
                20,
                18,
                20
        );


        stopsContainer.addView(
                fallback
        );
    }
}