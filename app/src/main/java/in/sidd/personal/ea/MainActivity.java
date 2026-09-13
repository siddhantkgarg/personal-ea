package in.sidd.personal.ea;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {
    private LinearLayout eventsContainer;
    private TextView emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(20);
        root.setPadding(pad, pad, pad, pad);
        scroll.addView(root);

        TextView title = new TextView(this);
        title.setText("Personal EA");
        title.setTextSize(28);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Your local Android activity inbox");
        subtitle.setTextSize(16);
        subtitle.setPadding(0, dp(4), 0, dp(20));
        root.addView(subtitle);

        Button enable = new Button(this);
        enable.setText("Enable notification access");
        enable.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));
        root.addView(enable);

        Button refresh = new Button(this);
        refresh.setText("Refresh captured activity");
        refresh.setOnClickListener(v -> renderEvents());
        root.addView(refresh);

        TextView section = new TextView(this);
        section.setText("Recent activity");
        section.setTextSize(20);
        section.setPadding(0, dp(24), 0, dp(10));
        root.addView(section);

        emptyState = new TextView(this);
        emptyState.setText("No activity captured yet. Enable notification access, then use your phone normally.");
        emptyState.setTextSize(15);
        root.addView(emptyState);

        eventsContainer = new LinearLayout(this);
        eventsContainer.setOrientation(LinearLayout.VERTICAL);
        root.addView(eventsContainer);

        setContentView(scroll);
        renderEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderEvents();
    }

    private void renderEvents() {
        eventsContainer.removeAllViews();
        List<EventStore.Event> events = EventStore.getEvents(this);
        emptyState.setVisibility(events.isEmpty() ? View.VISIBLE : View.GONE);

        DateFormat fmt = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        for (EventStore.Event event : events) {
            TextView card = new TextView(this);
            StringBuilder text = new StringBuilder();
            text.append(event.appLabel).append("\n");
            if (!event.title.isEmpty()) text.append(event.title).append("\n");
            if (!event.text.isEmpty()) text.append(event.text).append("\n");
            text.append(fmt.format(new Date(event.timestamp)));
            card.setText(text.toString());
            card.setTextSize(15);
            card.setPadding(dp(14), dp(12), dp(14), dp(12));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 0, dp(10));
            card.setLayoutParams(lp);
            card.setBackgroundColor(0xFFF2F2F2);
            eventsContainer.addView(card);
        }
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
