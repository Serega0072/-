package com.example.russiancalendar.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.russiancalendar.models.Event;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventStorage {

    private static final String PREFS_NAME = "RussianCalendarPrefs";
    private static final String KEY_EVENTS = "events";
    private static final Gson gson = new Gson();

    public static List<Event> loadEvents(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_EVENTS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Event>>() {}.getType();
        List<Event> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    public static void saveEvents(Context context, List<Event> events) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_EVENTS, gson.toJson(events)).apply();
    }

    public static void addEvent(Context context, Event event) {
        List<Event> events = loadEvents(context);
        // Generate simple id
        long id = System.currentTimeMillis();
        event.setId(id);
        events.add(event);
        saveEvents(context, events);
    }

    public static List<Event> getEventsForDate(Context context, String date) {
        List<Event> all = loadEvents(context);
        List<Event> result = new ArrayList<>();
        for (Event e : all) {
            if (date.equals(e.getDate())) result.add(e);
        }
        result.sort((a, b) -> a.getTime().compareTo(b.getTime()));
        return result;
    }

    public static void deleteEvent(Context context, long eventId) {
        List<Event> events = loadEvents(context);
        events.removeIf(e -> e.getId() == eventId);
        saveEvents(context, events);
    }

    public static void deletePastEvents(Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());
        List<Event> all = loadEvents(context);
        all.removeIf(e -> e.getDate().compareTo(today) < 0);
        saveEvents(context, all);
    }
}
