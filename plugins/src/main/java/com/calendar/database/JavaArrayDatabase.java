package com.calendar.database;

import com.calendar.Calendar;
import com.calendar.Entry;
import com.calendar.interfaces.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JavaArrayDatabase implements Database {
    private final List<Calendar> calendars = new ArrayList<>();
    private final List<Entry> entries = new ArrayList<>();

    @Override
    public boolean save(Calendar calendar) {
        deleteCalendar(calendar.getUuid()); // Entferne vorhandene Kalender mit derselben UUID
        calendars.add(calendar);
        for (Entry entry : calendar.getEntries()) {
            save(entry, calendar.getUuid());
        }
        return true;
    }

    @Override
    public boolean save(Entry entry, UUID calendarId) {
        deleteEntry(entry.getUuid()); // Entferne vorhandene Einträge mit derselben UUID
        entry = new Entry(
                entry.getUuid(),
                entry.getTitle(),
                entry.getDescription(),
                entry.getDateAndTime(),
                entry.getLocation(),
                entry.getCategory(),
                entry.getPriority(),
                entry.getStatus(),
                entry.getNotes(),
                entry.getCreatedAt()
        );
        entries.add(entry);
        return true;
    }

    @Override
    public Entry readEntry(UUID id) {
        return entries.stream()
                .filter(entry -> entry.getUuid().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Calendar readCalendar(UUID id) {
        Calendar calendar = calendars.stream()
                .filter(c -> c.getUuid().equals(id))
                .findFirst()
                .orElse(null);
        if (calendar != null) {
            List<Entry> calendarEntries = new ArrayList<>();
            for (Entry entry : entries) {
                if (entry.getNotes() != null && entry.getNotes().equals(id.toString())) {
                    calendarEntries.add(entry);
                }
            }
            calendar.setEntries(calendarEntries.toArray(new Entry[0]));
        }
        return calendar;
    }

    @Override
    public boolean deleteEntry(Entry entry) {
        return deleteEntry(entry.getUuid());
    }

    @Override
    public boolean deleteCalendar(Calendar calendar) {
        return deleteCalendar(calendar.getUuid());
    }

    @Override
    public boolean deleteEntry(UUID id) {
        return entries.removeIf(entry -> entry.getUuid().equals(id));
    }

    @Override
    public boolean deleteCalendar(UUID id) {
        boolean removed = calendars.removeIf(calendar -> calendar.getUuid().equals(id));
        if (removed) {
            entries.removeIf(entry -> entry.getNotes() != null && entry.getNotes().equals(id.toString()));
        }
        return removed;
    }

    @Override
    public boolean updateEntry(UUID id, Entry entry) {
        deleteEntry(id);
        return save(entry, UUID.fromString(entry.getNotes()));
    }

    @Override
    public boolean updateCalendar(UUID id, Calendar calendar) {
        deleteCalendar(id);
        return save(calendar);
    }

    @Override
    public Entry[] listEntries() {
        return entries.toArray(new Entry[0]);
    }

    @Override
    public Calendar[] listCalendars() {
        return calendars.toArray(new Calendar[0]);
    }

    @Override
    public boolean createTables() {
        calendars.clear();
        entries.clear();

        // Initialen Kalender erstellen, falls keiner existiert
        if (calendars.isEmpty()) {
            Calendar initialCalendar = new Calendar("Calendar", "Default Calendar");
            calendars.add(initialCalendar);
        }

        return true;
    }
}