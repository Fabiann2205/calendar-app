package com.calendar;

import com.calendar.enums.Category;
import com.calendar.enums.Priority;
import com.calendar.enums.Status;

import java.time.ZonedDateTime;

public class EntryResource extends Entry {
    private final Entry entry;

    public EntryResource(String title, String description, ZonedDateTime dateAndTime, String location, Category category, Priority priority, Status status, String notes) {
        super(title, description, dateAndTime, location, category, priority, status, notes);
        this.entry = new Entry(title, description, dateAndTime, location, category, priority, status, notes);
    }

    public EntryResource(String title, ZonedDateTime dateAndTime) {
        super(title, dateAndTime);
        this.entry = new Entry(title, dateAndTime);
    }

    public EntryResource(String title, ZonedDateTime dateAndTime, String location) {
        super(title, dateAndTime, location);
        this.entry = new Entry(title, dateAndTime, location);
    }

    public Entry getEntry() {
        return entry;
    }
}
