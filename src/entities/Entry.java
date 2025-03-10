package entities;

import entities.enums.Category;
import entities.enums.Priority;
import entities.enums.Status;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Represents an entry with a title, description, date and time, location, category, priority, status, and notes.
 * <p>
 * The `title` and `dateAndTime` fields must not be null.
 */
public class Entry {
    private String title;
    private String description;
    private ZonedDateTime dateAndTime;
    private String location;
    private Category category;
    private Priority priority;
    private Status status;
    private String notes;

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
        this.title = title;
        this.description = description;
        this.dateAndTime = dateAndTime;
        this.location = location;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.notes = notes;
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
        this.title = title;
        this.dateAndTime = dateAndTime;
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
        this.title = title;
        this.dateAndTime = dateAndTime;
        this.location = location;
    }

    public String getTitle() {
        return title;
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
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Entry entry = (Entry) o;
        return getTitle().equals(entry.getTitle()) && Objects.equals(getDescription(), entry.getDescription()) && getDateAndTime().equals(entry.getDateAndTime()) && Objects.equals(getLocation(), entry.getLocation()) && getCategory() == entry.getCategory() && getPriority() == entry.getPriority() && getStatus() == entry.getStatus() && Objects.equals(getNotes(), entry.getNotes());
    }

    @Override
    public int hashCode() {
        int result = getTitle().hashCode();
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + getDateAndTime().hashCode();
        result = 31 * result + Objects.hashCode(getLocation());
        result = 31 * result + Objects.hashCode(getCategory());
        result = 31 * result + Objects.hashCode(getPriority());
        result = 31 * result + Objects.hashCode(getStatus());
        result = 31 * result + Objects.hashCode(getNotes());
        return result;
    }
}
