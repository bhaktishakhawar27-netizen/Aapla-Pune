package com.example.aaplapune;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class MetroActivity extends AppCompatActivity {

    private static final int NAVY = Color.rgb(8, 35, 51);
    private static final int GREY = Color.rgb(113, 128, 138);

    private TextView backButton;
    private TextView liveStatus;
    private LinearLayout content;

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private final List<LineUi> lineUis = new ArrayList<>();

    // Updates every second
    private final Runnable ticker = new Runnable() {

        @Override
        public void run() {

            refreshAll();

            handler.postDelayed(this, 1000);
        }
    };


    // =====================================================
    // PER-LINE UI HOLDER
    // =====================================================

    private static class LineUi {

        MetroData.Line line;
        int selected = 0;

        TextView panelTitle;
        TextView forwardText;
        TextView backwardText;

        List<LinearLayout> rows = new ArrayList<>();
    }


    // =====================================================
    // ON CREATE
    // =====================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_metro);

        backButton = findViewById(R.id.metroBackButton);
        liveStatus = findViewById(R.id.metroLiveStatus);
        content = findViewById(R.id.metroContent);

        backButton.setOnClickListener(v -> finish());

        for (MetroData.Line line : MetroData.LINES) {

            content.addView(buildLineCard(line));
        }

        content.addView(buildInfoCard());

        refreshAll();
    }

    @Override
    protected void onResume() {

        super.onResume();

        handler.removeCallbacks(ticker);
        handler.post(ticker);
    }

    @Override
    protected void onPause() {

        super.onPause();

        handler.removeCallbacks(ticker);
    }


    // =====================================================
    // BUILD LINE CARD
    // =====================================================

    private View buildLineCard(MetroData.Line line) {

        LineUi ui = new LineUi();
        ui.line = line;

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundColor(Color.WHITE);
        card.setElevation(dp(4));
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        cardParams.bottomMargin = dp(16);
        card.setLayoutParams(cardParams);


        // ---------- Title row: coloured pill + status ----------

        LinearLayout titleRow = new LinearLayout(this);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView pill = new TextView(this);
        pill.setText(line.name.toUpperCase());
        pill.setTextColor(Color.WHITE);
        pill.setTextSize(13);
        pill.setTypeface(null, Typeface.BOLD);
        pill.setPadding(dp(14), dp(7), dp(14), dp(7));
        pill.setBackground(rounded(line.color, 20));

        titleRow.addView(pill);

        View spacer = new View(this);
        spacer.setLayoutParams(
                new LinearLayout.LayoutParams(0, 1, 1f)
        );
        titleRow.addView(spacer);

        TextView status = new TextView(this);
        status.setText(line.operational ? "● Operational" : "● Coming soon");
        status.setTextSize(12);
        status.setTypeface(null, Typeface.BOLD);
        status.setTextColor(
                line.operational
                        ? Color.rgb(30, 142, 62)
                        : Color.rgb(217, 119, 6)
        );

        titleRow.addView(status);
        card.addView(titleRow);


        // ---------- Route line ----------

        TextView route = new TextView(this);
        route.setText(
                line.from + "  →  " + line.to
                        + "\n" + line.stations.size() + " stations  •  "
                        + line.statusText
        );
        route.setTextColor(NAVY);
        route.setTextSize(14);
        route.setPadding(0, dp(12), 0, 0);
        card.addView(route);


        // ---------- Live arrival panel (operational lines only) ----------

        if (line.operational) {

            LinearLayout panel = new LinearLayout(this);
            panel.setOrientation(LinearLayout.VERTICAL);
            panel.setPadding(dp(14), dp(12), dp(14), dp(12));
            panel.setBackground(
                    rounded(withAlpha(line.color, 28), 12)
            );

            LinearLayout.LayoutParams panelParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
            panelParams.topMargin = dp(14);
            panel.setLayoutParams(panelParams);

            ui.panelTitle = new TextView(this);
            ui.panelTitle.setTextColor(line.color);
            ui.panelTitle.setTextSize(11);
            ui.panelTitle.setTypeface(null, Typeface.BOLD);
            panel.addView(ui.panelTitle);

            ui.forwardText = new TextView(this);
            ui.forwardText.setTextColor(NAVY);
            ui.forwardText.setTextSize(14);
            ui.forwardText.setPadding(0, dp(8), 0, 0);
            panel.addView(ui.forwardText);

            ui.backwardText = new TextView(this);
            ui.backwardText.setTextColor(NAVY);
            ui.backwardText.setTextSize(14);
            ui.backwardText.setPadding(0, dp(10), 0, 0);
            panel.addView(ui.backwardText);

            card.addView(panel);

            TextView hint = new TextView(this);
            hint.setText("Tap a station to see its live timings");
            hint.setTextColor(GREY);
            hint.setTextSize(11);
            hint.setPadding(0, dp(12), 0, dp(4));
            card.addView(hint);

        } else {

            TextView soon = new TextView(this);
            soon.setText(
                    "This line is still being built. Live timings will "
                            + "appear here once it opens."
            );
            soon.setTextColor(GREY);
            soon.setTextSize(12);
            soon.setPadding(0, dp(10), 0, dp(4));
            card.addView(soon);
        }


        // ---------- Station list ----------

        for (int i = 0; i < line.stations.size(); i++) {

            card.addView(buildStationRow(ui, i));
        }

        if (line.operational) {

            // Default station: first interchange, else first station
            for (int i = 0; i < line.stations.size(); i++) {

                if (MetroData.isInterchange(line.stations.get(i))) {
                    ui.selected = i;
                    break;
                }
            }

            highlightRows(ui);

            lineUis.add(ui);
        }

        return card;
    }


    // =====================================================
    // STATION ROW
    // =====================================================

    private LinearLayout buildStationRow(LineUi ui, int index) {

        MetroData.Line line = ui.line;

        String name = line.stations.get(index);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(8), dp(9), dp(8), dp(9));

        // Coloured dot with a connector feel
        TextView dot = new TextView(this);
        boolean isEnd =
                index == 0 || index == line.stations.size() - 1;
        dot.setText(isEnd ? "◉" : "●");
        dot.setTextColor(line.color);
        dot.setTextSize(isEnd ? 16 : 12);
        dot.setGravity(Gravity.CENTER);
        dot.setLayoutParams(
                new LinearLayout.LayoutParams(dp(28), dp(22))
        );
        row.addView(dot);

        TextView label = new TextView(this);
        label.setText(name);
        label.setTextColor(NAVY);
        label.setTextSize(14);
        label.setTypeface(null, isEnd ? Typeface.BOLD : Typeface.NORMAL);
        label.setLayoutParams(
                new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        );
        row.addView(label);

        if (MetroData.isInterchange(name)) {

            TextView tag = new TextView(this);
            tag.setText("⇄ Interchange");
            tag.setTextSize(10);
            tag.setTypeface(null, Typeface.BOLD);
            tag.setTextColor(NAVY);
            tag.setPadding(dp(8), dp(3), dp(8), dp(3));
            tag.setBackground(
                    rounded(Color.rgb(217, 239, 24), 10)
            );
            row.addView(tag);
        }

        if (line.operational) {

            row.setClickable(true);
            row.setFocusable(true);

            row.setOnClickListener(v -> {

                ui.selected = index;

                highlightRows(ui);

                refreshLine(ui, currentSeconds());
            });

            ui.rows.add(row);
        }

        return row;
    }

    private void highlightRows(LineUi ui) {

        for (int i = 0; i < ui.rows.size(); i++) {

            ui.rows.get(i).setBackground(
                    i == ui.selected
                            ? rounded(withAlpha(ui.line.color, 40), 10)
                            : null
            );
        }
    }


    // =====================================================
    // FARES / BOOKING CARD
    // =====================================================

    private View buildInfoCard() {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundColor(Color.WHITE);
        card.setElevation(dp(4));
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        TextView title = new TextView(this);
        title.setText("TICKETS & FARES");
        title.setTextColor(GREY);
        title.setTextSize(11);
        title.setTypeface(null, Typeface.BOLD);
        card.addView(title);

        TextView body = new TextView(this);
        body.setText(
                MetroData.FARE_INFO
                        + "\n\n"
                        + MetroData.BOOKING_INFO
                        + "\n\nService: 6:00 AM – 11:00 PM daily  •  "
                        + "Train every 5 min (peak) / 10 min (off-peak)  •  "
                        + "30 sec stop at each station"
        );
        body.setTextColor(NAVY);
        body.setTextSize(13);
        body.setPadding(0, dp(8), 0, 0);
        card.addView(body);

        TextView note = new TextView(this);
        note.setText(
                "Live times are calculated from the official timetable "
                        + "(not GPS). Actual arrivals may vary by a minute or two."
        );
        note.setTextColor(GREY);
        note.setTextSize(11);
        note.setPadding(0, dp(12), 0, 0);
        card.addView(note);

        return card;
    }


    // =====================================================
    // LIVE REFRESH
    // =====================================================

    private void refreshAll() {

        int now = currentSeconds();

        // Header
        if (MetroData.isServiceOpen(now)) {

            int hw = MetroData.headway(now) / 60;

            liveStatus.setText(
                    "🕒 " + MetroData.formatClock(now)
                            + "  •  "
                            + (MetroData.isPeak(now) ? "Peak hours" : "Off-peak")
                            + "  •  train every " + hw + " min"
            );

        } else {

            liveStatus.setText(
                    "🕒 " + MetroData.formatClock(now)
                            + "  •  Service closed  •  First train 6:00 AM"
            );
        }

        for (LineUi ui : lineUis) {

            refreshLine(ui, now);
        }
    }

    private void refreshLine(LineUi ui, int now) {

        MetroData.Line line = ui.line;

        int count = line.stations.size();

        String station = line.stations.get(ui.selected);

        ui.panelTitle.setText("LIVE AT  " + station.toUpperCase());

        // Towards last station
        ui.forwardText.setText(
                describe(
                        "Towards " + line.to,
                        ui.selected == count - 1,
                        MetroData.upcomingArrivals(
                                ui.selected, count, true, now, 2
                        ),
                        now
                )
        );

        // Towards first station
        ui.backwardText.setText(
                describe(
                        "Towards " + line.from,
                        ui.selected == 0,
                        MetroData.upcomingArrivals(
                                ui.selected, count, false, now, 2
                        ),
                        now
                )
        );
    }

    private String describe(
            String direction,
            boolean terminus,
            List<Integer> arrivals,
            int now
    ) {

        if (terminus) {

            return "🚆 " + direction + "\nLast station in this direction";
        }

        if (arrivals.isEmpty()) {

            return "🚆 " + direction + "\nNo trains scheduled";
        }

        int first = arrivals.get(0);

        StringBuilder sb = new StringBuilder();

        sb.append("🚆 ").append(direction).append("\n");

        sb.append(MetroData.formatWait(first - now))
                .append("   (")
                .append(MetroData.formatClock(first))
                .append(first >= 86400 ? ", tomorrow" : "")
                .append(")");

        if (arrivals.size() > 1) {

            int second = arrivals.get(1);

            sb.append("\nThen ")
                    .append(MetroData.formatClock(second))
                    .append(second >= 86400 ? " (tomorrow)" : "");
        }

        return sb.toString();
    }


    // =====================================================
    // HELPERS
    // =====================================================

    /** Seconds since midnight, India time. */
    private int currentSeconds() {

        Calendar c =
                Calendar.getInstance(
                        TimeZone.getTimeZone("Asia/Kolkata")
                );

        return c.get(Calendar.HOUR_OF_DAY) * 3600
                + c.get(Calendar.MINUTE) * 60
                + c.get(Calendar.SECOND);
    }

    private int dp(int value) {

        return Math.round(
                value * getResources().getDisplayMetrics().density
        );
    }

    private int withAlpha(int color, int alpha) {

        return Color.argb(
                alpha,
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );
    }

    private GradientDrawable rounded(int color, int radiusDp) {

        GradientDrawable d = new GradientDrawable();

        d.setColor(color);
        d.setCornerRadius(dp(radiusDp));

        return d;
    }
}
