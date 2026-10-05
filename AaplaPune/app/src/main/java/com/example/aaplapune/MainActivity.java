package com.example.aaplapune;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // =====================================================
    // MAIN UI
    // =====================================================

    LinearLayout splashScreen;
    LinearLayout mainApp;

    EditText fromInput;
    EditText toInput;

    LinearLayout fromSuggestions;
    LinearLayout toSuggestions;

    TextView swapButton;
    TextView menuButton;

    TextView bottomHome;
    TextView bottomJourney;
    TextView bottomTickets;
    TextView bottomProfile;

    TextView menuHome;
    TextView menuJourney;
    TextView menuStops;
    TextView menuTracking;
    TextView menuCrowd;
    TextView menuTickets;
    TextView menuProfile;
    TextView closeMenu;

    View menuOverlay;
    LinearLayout sideMenu;

    // =====================================================
    // TRANSPORT BUTTONS
    // =====================================================

    android.widget.Button autoMode;
    android.widget.Button metroMode;
    android.widget.Button busMode;
    android.widget.Button planJourneyButton;

    // =====================================================
    // GTFS
    // =====================================================

    GTFSReader gtfsReader;

    // =====================================================
    // SELECTED LOCATIONS
    // =====================================================

    String selectedFrom = "";
    String selectedTo = "";

    // =====================================================
    // ON CREATE
    // =====================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_main
        );

        // =================================================
        // FIND MAIN VIEWS
        // =================================================

        splashScreen =
                findViewById(
                        R.id.splashScreen
                );

        mainApp =
                findViewById(
                        R.id.mainApp
                );

        fromInput =
                findViewById(
                        R.id.fromInput
                );

        toInput =
                findViewById(
                        R.id.toInput
                );

        fromSuggestions =
                findViewById(
                        R.id.fromSuggestions
                );

        toSuggestions =
                findViewById(
                        R.id.toSuggestions
                );

        swapButton =
                findViewById(
                        R.id.swapButton
                );

        menuButton =
                findViewById(
                        R.id.menuButton
                );

        menuOverlay =
                findViewById(
                        R.id.menuOverlay
                );

        sideMenu =
                findViewById(
                        R.id.sideMenu
                );

        closeMenu =
                findViewById(
                        R.id.closeMenu
                );

        // =================================================
        // TRANSPORT BUTTONS
        // =================================================

        autoMode =
                findViewById(
                        R.id.autoMode
                );

        metroMode =
                findViewById(
                        R.id.metroMode
                );

        busMode =
                findViewById(
                        R.id.busMode
                );

        planJourneyButton =
                findViewById(
                        R.id.planJourneyButton
                );

        // =================================================
        // BOTTOM NAVIGATION
        // =================================================

        bottomHome =
                findViewById(
                        R.id.bottomHome
                );

        bottomJourney =
                findViewById(
                        R.id.bottomJourney
                );

        bottomTickets =
                findViewById(
                        R.id.bottomTickets
                );

        bottomProfile =
                findViewById(
                        R.id.bottomProfile
                );

        // =================================================
        // SIDE MENU
        // =================================================

        menuHome =
                findViewById(
                        R.id.menuHome
                );

        menuJourney =
                findViewById(
                        R.id.menuJourney
                );

        menuStops =
                findViewById(
                        R.id.menuStops
                );

        menuTracking =
                findViewById(
                        R.id.menuTracking
                );

        menuCrowd =
                findViewById(
                        R.id.menuCrowd
                );

        menuTickets =
                findViewById(
                        R.id.menuTickets
                );

        menuProfile =
                findViewById(
                        R.id.menuProfile
                );

        // =================================================
        // LOAD GTFS
        // =================================================

        gtfsReader =
                new GTFSReader(this);

        gtfsReader.loadData();

        // =================================================
        // SPLASH SCREEN
        // =================================================

        showSplash();

        // =================================================
        // AUTOCOMPLETE
        // =================================================

        setupFromAutocomplete();

        setupToAutocomplete();

        // =================================================
        // SWAP
        // =================================================

        swapButton.setOnClickListener(
                v -> swapLocations()
        );

        // =================================================
        // PLAN JOURNEY
        // =================================================

        planJourneyButton.setOnClickListener(
                v -> planJourney()
        );

        // =================================================
        // MENU
        // =================================================

        menuButton.setOnClickListener(
                v -> openMenu()
        );

        closeMenu.setOnClickListener(
                v -> closeMenu()
        );

        menuOverlay.setOnClickListener(
                v -> closeMenu()
        );

        menuHome.setOnClickListener(
                v -> closeMenu()
        );

        menuJourney.setOnClickListener(
                v -> {

                    closeMenu();

                    Intent intent =
                            new Intent(
                                    MainActivity.this,
                                    JourneyActivity.class
                            );

                    startActivity(intent);
                }
        );

        menuStops.setOnClickListener(
                v -> {

                    closeMenu();

                    Toast.makeText(
                            this,
                            "Route Stops coming soon",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        menuTracking.setOnClickListener(
                v -> {

                    closeMenu();

                    Toast.makeText(
                            this,
                            "Live Tracking coming soon",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        menuCrowd.setOnClickListener(
                v -> {

                    closeMenu();

                    Toast.makeText(
                            this,
                            "Crowd Prediction coming soon",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        menuTickets.setOnClickListener(
                v -> {

                    closeMenu();

                    Toast.makeText(
                            this,
                            "My Tickets coming soon",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        menuProfile.setOnClickListener(
                v -> {

                    closeMenu();

                    Toast.makeText(
                            this,
                            "Profile coming soon",
                            Toast.LENGTH_SHORT
                    ).show();
                });

        // =================================================
        // BOTTOM NAVIGATION
        // =================================================

        bottomHome.setOnClickListener(
                v -> {
                    // Already on Home
                }
        );

        bottomJourney.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    MainActivity.this,
                                    JourneyActivity.class
                            );

                    startActivity(intent);
                }
        );

        bottomTickets.setOnClickListener(
                v -> {

                    Toast.makeText(
                            this,
                            "My Tickets coming soon",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        bottomProfile.setOnClickListener(
                v -> {

                    Toast.makeText(
                            this,
                            "Profile coming soon",
                            Toast.LENGTH_SHORT
                    ).show();
                });

        // =================================================
        // TRANSPORT SELECTION
        // =================================================

        autoMode.setOnClickListener(
                v -> selectTransport("AUTO")
        );

        metroMode.setOnClickListener(
                v -> {

                    selectTransport("METRO");

                    // Open Metro lines + live timings
                    startActivity(
                            new Intent(
                                    MainActivity.this,
                                    MetroActivity.class
                            )
                    );
                }
        );

        busMode.setOnClickListener(
                v -> selectTransport("BUS")
        );
    }


    // =====================================================
    // SPLASH
    // =====================================================

    private void showSplash() {

        splashScreen.setVisibility(
                View.VISIBLE
        );

        mainApp.setVisibility(
                View.GONE
        );

        splashScreen.postDelayed(
                () -> {

                    splashScreen.setVisibility(
                            View.GONE
                    );

                    mainApp.setVisibility(
                            View.VISIBLE
                    );

                },
                2200
        );
    }


    // =====================================================
    // FROM AUTOCOMPLETE
    // =====================================================

    private void setupFromAutocomplete() {

        fromInput.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }


                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        selectedFrom = "";

                        showSuggestions(
                                fromInput,
                                fromSuggestions
                        );
                    }


                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );


        fromInput.setOnFocusChangeListener(
                (v, hasFocus) -> {

                    if (hasFocus) {

                        showSuggestions(
                                fromInput,
                                fromSuggestions
                        );
                    }
                }
        );
    }


    // =====================================================
    // TO AUTOCOMPLETE
    // =====================================================

    private void setupToAutocomplete() {

        toInput.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }


                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        selectedTo = "";

                        showSuggestions(
                                toInput,
                                toSuggestions
                        );
                    }


                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );


        toInput.setOnFocusChangeListener(
                (v, hasFocus) -> {

                    if (hasFocus) {

                        showSuggestions(
                                toInput,
                                toSuggestions
                        );
                    }
                }
        );
    }


    // =====================================================
    // SHOW SUGGESTIONS
    // =====================================================

    private void showSuggestions(
            EditText input,
            LinearLayout container
    ) {

        String query =
                input.getText()
                        .toString()
                        .trim();

        container.removeAllViews();

        if (query.isEmpty()) {

            container.setVisibility(
                    View.GONE
            );

            return;
        }

        List<String> results =
                gtfsReader.searchStops(
                        query
                );

        if (results.isEmpty()) {

            container.setVisibility(
                    View.GONE
            );

            return;
        }

        // Show maximum 8 suggestions
        int limit =
                Math.min(
                        results.size(),
                        8
                );

        for (int i = 0;
             i < limit;
             i++) {

            String stop =
                    results.get(i);

            TextView suggestion =
                    createSuggestionView(
                            stop
                    );

            suggestion.setOnClickListener(
                    v -> {

                        input.setText(
                                stop
                        );

                        input.setSelection(
                                input.length()
                        );

                        container.setVisibility(
                                View.GONE
                        );

                        if (input == fromInput) {

                            selectedFrom =
                                    stop;

                        } else {

                            selectedTo =
                                    stop;
                        }
                    }
            );

            container.addView(
                    suggestion
            );
        }

        container.setVisibility(
                View.VISIBLE
        );
    }


    // =====================================================
    // CREATE SUGGESTION VIEW
    // =====================================================

    private TextView createSuggestionView(
            String stopName
    ) {

        TextView view =
                new TextView(this);

        view.setText(
                "📍  " + stopName
        );

        view.setTextSize(
                14
        );

        view.setTextColor(
                Color.rgb(
                        8,
                        35,
                        51
                )
        );

        view.setGravity(
                Gravity.CENTER_VERTICAL
        );

        view.setPadding(
                16,
                15,
                16,
                15
        );

        view.setBackgroundColor(
                Color.WHITE
        );

        view.setClickable(
                true
        );

        view.setFocusable(
                true
        );

        return view;
    }


    // =====================================================
    // SWAP LOCATIONS
    // =====================================================

    private void swapLocations() {

        String from =
                fromInput.getText()
                        .toString();

        String to =
                toInput.getText()
                        .toString();

        fromInput.setText(
                to
        );

        toInput.setText(
                from
        );

        selectedFrom =
                to;

        selectedTo =
                from;

        fromSuggestions.setVisibility(
                View.GONE
        );

        toSuggestions.setVisibility(
                View.GONE
        );
    }


    // =====================================================
    // SELECT TRANSPORT
    // =====================================================

    private void selectTransport(
            String mode
    ) {

        // Reset
        autoMode.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.rgb(
                                244,
                                246,
                                247
                        )
                )
        );

        metroMode.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.rgb(
                                244,
                                246,
                                247
                        )
                )
        );

        busMode.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.rgb(
                                244,
                                246,
                                247
                        )
                )
        );


        // Selected
        if (mode.equals("AUTO")) {

            autoMode.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.rgb(
                                    217,
                                    239,
                                    24
                            )
                    )
            );

        } else if (mode.equals("METRO")) {

            metroMode.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.rgb(
                                    217,
                                    239,
                                    24
                            )
                    )
            );

        } else {

            busMode.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.rgb(
                                    217,
                                    239,
                                    24
                            )
                    )
            );
        }
    }


    // =====================================================
    // PLAN JOURNEY
    // =====================================================

    private void planJourney() {

        String from =
                fromInput.getText()
                        .toString()
                        .trim();

        String to =
                toInput.getText()
                        .toString()
                        .trim();

        if (from.isEmpty()) {

            fromInput.requestFocus();

            Toast.makeText(
                    this,
                    "Please select a starting PMPML stop",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (to.isEmpty()) {

            toInput.requestFocus();

            Toast.makeText(
                    this,
                    "Please select a destination PMPML stop",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (from.equalsIgnoreCase(to)) {

            Toast.makeText(
                    this,
                    "From and To stops cannot be the same",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =================================================
        // OPEN RESULTS
        // =================================================

        Intent intent =
                new Intent(
                        MainActivity.this,
                        JourneyResultsActivity.class
                );

        intent.putExtra(
                "FROM_LOCATION",
                from
        );

        intent.putExtra(
                "TO_LOCATION",
                to
        );

        startActivity(
                intent
        );
    }


    // =====================================================
    // OPEN MENU
    // =====================================================

    private void openMenu() {

        menuOverlay.setVisibility(
                View.VISIBLE
        );

        sideMenu.setVisibility(
                View.VISIBLE
        );
    }


    // =====================================================
    // CLOSE MENU
    // =====================================================

    private void closeMenu() {

        sideMenu.setVisibility(
                View.GONE
        );

        menuOverlay.setVisibility(
                View.GONE
        );
    }


    // =====================================================
    // BACK BUTTON
    // =====================================================

    @Override
    public void onBackPressed() {

        if (sideMenu.getVisibility()
                == View.VISIBLE) {

            closeMenu();

            return;
        }

        if (fromSuggestions.getVisibility()
                == View.VISIBLE) {

            fromSuggestions.setVisibility(
                    View.GONE
            );

            return;
        }

        if (toSuggestions.getVisibility()
                == View.VISIBLE) {

            toSuggestions.setVisibility(
                    View.GONE
            );

            return;
        }

        super.onBackPressed();
    }
}