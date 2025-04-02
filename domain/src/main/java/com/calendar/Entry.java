package com.calendar;

import com.calendar.enums.Category;
import com.calendar.enums.Priority;
import com.calendar.enums.Status;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an entry with a title, description, date and time, location, category, priority, status, and notes.
 * <p>
 * The `title` and `dateAndTime` fields must not be null.
 */
public class Entry {
    private final UUID uuid;
    private String title;
    private String description;
    private ZonedDateTime dateAndTime;
    private String location;
    private Category category;
    private Priority priority;
    private Status status;
    private String notes;
    private final ZonedDateTime createdAt;

    /**
     * Constructs a new Entry with the specified title, description, date and time, location, category, priority, status, and notes.
     *
     * @param title       the title of the entry, must not be null
     * @param description the description of the entry
     * @param dateAndTime the date and time of the entry, must not be null
     * @param location    the location of the entry
     * @param category    the category of the entry
     * @param priority    the priority of the entry
     * @param status      the status of the entry
     * @param notes       additional notes for the entry
     * @throws IllegalArgumentException if title or dateAndTime is null
     */
    public Entry(String title, String description, ZonedDateTime dateAndTime, String location, Category category, Priority priority, Status status, String notes) {
        if (title == null || dateAndTime == null) {
            throw new IllegalArgumentException("Title and dateAndTime must not be null");
        }
        this.uuid = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.dateAndTime = dateAndTime;
        this.location = location;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.notes = notes;
        this.createdAt = ZonedDateTime.now();
    }

    public Entry(UUID uuid, String title, String description, ZonedDateTime dateAndTime, String location, Category category, Priority priority, Status status, String notes, ZonedDateTime createdAt) {
        if (title == null || dateAndTime == null) {
            throw new IllegalArgumentException("Title and dateAndTime must not be null");
        }
        this.uuid = uuid;
        this.title = title;
        this.description = description;
        this.dateAndTime = dateAndTime;
        this.location = location;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    /**
     * Constructs a new Entry with the specified title and date and time.
     *
     * @param title       the title of the entry, must not be null
     * @param dateAndTime the date and time of the entry, must not be null
     * @throws IllegalArgumentException if title or dateAndTime is null
     */
    public Entry(String title, ZonedDateTime dateAndTime) {
        if (title == null || dateAndTime == null) {
            throw new IllegalArgumentException("Title and dateAndTime must not be null");
        }
        this.uuid = UUID.randomUUID();
        this.title = title;
        this.dateAndTime = dateAndTime;
        this.createdAt = ZonedDateTime.now();
    }

    /**
     * Constructs a new Entry with the specified title, date and time, and location.
     *
     * @param title       the title of the entry, must not be null
     * @param dateAndTime the date and time of the entry, must not be null
     * @param location    the location of the entry
     * @throws IllegalArgumentException if title or dateAndTime is null
     */
    public Entry(String title, ZonedDateTime dateAndTime, String location) {
        if (title == null || dateAndTime == null) {
            throw new IllegalArgumentException("Title and dateAndTime must not be null");
        }
        this.uuid = UUID.randomUUID();
        this.title = title;
        this.dateAndTime = dateAndTime;
        this.location = location;
        this.createdAt = ZonedDateTime.now();
    }

    public String getTitle() {
        return title;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(ZonedDateTime dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dateAndTime=" + dateAndTime +
                ", location='" + location + '\'' +
                ", category=" + category +
                ", priority=" + priority +
                ", status=" + status +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Entry entry = (Entry) o;
        return getUuid().equals(entry.getUuid());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getUuid());
        result = 31 * result + Objects.hashCode(getTitle());
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Objects.hashCode(getDateAndTime());
        result = 31 * result + Objects.hashCode(getLocation());
        result = 31 * result + Objects.hashCode(getCategory());
        result = 31 * result + Objects.hashCode(getPriority());
        result = 31 * result + Objects.hashCode(getStatus());
        result = 31 * result + Objects.hashCode(getNotes());
        result = 31 * result + Objects.hashCode(getCreatedAt());
        return result;
    }
}
