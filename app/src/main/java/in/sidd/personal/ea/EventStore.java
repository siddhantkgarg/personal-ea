package in.sidd.personal.ea;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EventStore {
    private static final String PREFS = "personal_ea_events";
    private static final String KEY = "events";
    private static final int MAX_EVENTS = 200;

    public static class Event {
        public String packageName;
        public String appLabel;
        public String title;
        public String text;
        public long timestamp;
    }

    public static synchronized void addEvent(Context context, Event event) {
        List<Event> events = getEvents(context);
        events.add(event);
        Collections.sort(events, Comparator.comparingLong((Event e) -> e.timestamp).reversed());
        if (events.size() > MAX_EVENTS) {
            events = new ArrayList<>(events.subList(0, MAX_EVENTS));
        }
        save(context, events);
    }

    public static synchronized List<Event> getEvents(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String raw = prefs.getString(KEY, "[]");
        List<Event> out = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                Event e = new Event();
                e.packageName = o.optString("packageName", "");
                e.appLabel = o.optString("appLabel", e.packageName);
                e.title = o.optString("title", "");
                e.text = o.optString("text", "");
                e.timestamp = o.optLong("timestamp", 0L);
                out.add(e);
            }
        } catch (Exception ignored) { }
        Collections.sort(out, Comparator.comparingLong((Event e) -> e.timestamp).reversed());
        return out;
    }

    private static void save(Context context, List<Event> events) {
        JSONArray arr = new JSONArray();
        try {
            for (Event e : events) {
                JSONObject o = new JSONObject();
                o.put("packageName", e.packageName);
                o.put("appLabel", e.appLabel);
                o.put("title", e.title);
                o.put("text", e.text);
                o.put("timestamp", e.timestamp);
                arr.put(o);
            }
        } catch (Exception ignored) { }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY, arr.toString())
                .apply();
    }
}
