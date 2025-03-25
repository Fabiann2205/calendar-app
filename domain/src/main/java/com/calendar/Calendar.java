package com.calendar;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class Calendar {
    private final UUID uuid;
    private String name;
    private String description;
    private Entry[] entries;
    private final ZonedDateTime createdAt;

    public Calendar(UUID uuid, String description, String name, Entry[] entries, ZonedDateTime createdAt) {
        this.uuid = uuid;
        this.description = description;
        this.name = name;
        this.entries = entries;
        this.createdAt = createdAt;
    }

    public Calendar(String description, String name, Entry[] entries) {
        this.uuid = UUID.randomUUID();
        this.description = description;
        this.name = name;
        this.entries = entries;
        this.createdAt = ZonedDateTime.now();
    }

    public Calendar(String name, String description) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.createdAt = ZonedDateTime.now();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Entry[] getEntries() {
        return entries;
    }

    public void setEntries(Entry[] entries) {
        this.entries = entries;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean addEntry(Entry entry) {
        if (entries == null) {
            entries = new Entry[1];
            entries[0] = entry;
        } else {
            Entry[] newEntries = new Entry[entries.length + 1];
            System.arraycopy(entries, 0, newEntries, 0, entries.length);
            newEntries[entries.length] = entry;
            entries = newEntries;
        }
        return true;
    }

    public boolean removeEntry(Entry entry) {
        if (entries == null) {
            return false;
        }
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].equals(entry)) {
                return copyArray(i);
            }
        }
        return false;
    }

    public boolean removeEntry(int index) {
        if (entries == null || index < 0 || index >= entries.length) {
            return false;
        }
        return copyArray(index);
    }

    private boolean copyArray(int index) {
        Entry[] newEntries = new Entry[entries.length - 1];
        System.arraycopy(entries, 0, newEntries, 0, index);
        System.arraycopy(entries, index + 1, newEntries, index, entries.length - index - 1);
        entries = newEntries;
        return true;
    }

    public boolean updateEntry(int index, Entry entry) {
        if (entries == null || index < 0 || index >= entries.length) {
            return false;
        }
        entries[index] = entry;
        return true;
    }

    public Entry getEntry(int index) {
        if (entries == null || index < 0 || index >= entries.length) {
            return null;
        }
        return entries[index];
    }

    public int getEntryCount() {
        return entries == null ? 0 : entries.length;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Calendar calendar = (Calendar) o;
        return Objects.equals(getUuid(), calendar.getUuid()) && Objects.equals(getName(), calendar.getName()) && Objects.equals(getDescription(), calendar.getDescription()) && Arrays.equals(getEntries(), calendar.getEntries()) && Objects.equals(getCreatedAt(), calendar.getCreatedAt());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getUuid());
        result = 31 * result + Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Arrays.hashCode(getEntries());
        result = 31 * result + Objects.hashCode(getCreatedAt());
        return result;
    }
}
