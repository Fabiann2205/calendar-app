package com.calendar.database;

import com.calendar.Calendar;
import com.calendar.Entry;
import com.calendar.enums.Category;
import com.calendar.enums.Priority;
import com.calendar.enums.Status;
import com.calendar.interfaces.Database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class FileDatabase implements Database {
    private static final Logger logger = Logger.getLogger(FileDatabase.class.getName());
    private final Path databasePath;

    public FileDatabase(String directory) {
        this.databasePath = Paths.get(directory);
        createTables();
    }

    @Override
    public boolean save(Calendar calendar) {
        try {
            // Delete existing entries for the calendar
            deleteEntriesByCalendarId(calendar.getUuid());

            // Check if calendar exists
            List<String> calendarLines = Files.readAllLines(databasePath.resolve("calendars.csv"));
            boolean calendarExists = false;
            for (String line : calendarLines.subList(1, calendarLines.size())) {
                if (line.split(",")[0].equals(calendar.getUuid().toString())) {
                    calendarExists = true;
                    break;
                }
            }

            // Append or update calendar
            if (calendarExists) {
                updateCalendar(calendar.getUuid(), calendar);
            } else {
                List<String> lines = new ArrayList<>();
                lines.add(calendar.getUuid() + "," + calendar.getName() + "," + calendar.getDescription() + "," + calendar.getCreatedAt());
                Files.write(databasePath.resolve("calendars.csv"), lines, StandardOpenOption.APPEND);
            }

            // Save entries
            for (Entry entry : calendar.getEntries()) {
                save(entry, calendar.getUuid());
            }
            return true;
        } catch (IOException e) {
            logger.severe("Error saving calendar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean save(Entry entry, UUID calendarId) {
        try {
            // Check if entry exists
            List<String> entryLines = Files.readAllLines(databasePath.resolve("entries.csv"));
            boolean entryExists = false;
            for (String line : entryLines.subList(1, entryLines.size())) {
                if (line.split(",")[0].equals(entry.getUuid().toString())) {
                    entryExists = true;
                    break;
                }
            }

            // Append or update entry
            if (entryExists) {
                updateEntry(entry.getUuid(), entry);
            } else {
                List<String> lines = new ArrayList<>();
                lines.add(entry.getUuid() + "," + entry.getTitle() + "," + entry.getDescription() + "," + entry.getDateAndTime() + "," + entry.getLocation() + "," + (entry.getCategory() != null ? entry.getCategory().name() : "") + "," + (entry.getPriority() != null ? entry.getPriority().name() : "") + "," + (entry.getStatus() != null ? entry.getStatus().name() : "") + "," + entry.getNotes() + "," + entry.getCreatedAt() + "," + calendarId);
                Files.write(databasePath.resolve("entries.csv"), lines, StandardOpenOption.APPEND);
            }
            return true;
        } catch (IOException e) {
            logger.severe("Error saving entry: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateEntry(UUID id, Entry entry) {
        deleteEntry(id);
        return save(entry, id);
    }

    @Override
    public boolean updateCalendar(UUID id, Calendar calendar) {
        deleteCalendar(id);
        return save(calendar);
    }

    private void deleteEntriesByCalendarId(UUID calendarId) {
        try {
            List<String> lines = Files.readAllLines(databasePath.resolve("entries.csv"));
            List<String> updatedLines = new ArrayList<>();
            updatedLines.add(lines.get(0));
            for (String line : lines.subList(1, lines.size())) {
                if (!line.split(",")[10].equals(calendarId.toString())) {
                    updatedLines.add(line);
                }
            }
            Files.write(databasePath.resolve("entries.csv"), updatedLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            logger.severe("Error deleting entries by calendar ID: " + e.getMessage());
        }
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
        try {
            List<String> lines = Files.readAllLines(databasePath.resolve("entries.csv"));
            List<String> updatedLines = new ArrayList<>();
            updatedLines.add(lines.get(0));
            for (String line : lines.subList(1, lines.size())) {
                if (!line.split(",")[0].equals(id.toString())) {
                    updatedLines.add(line);
                }
            }
            Files.write(databasePath.resolve("entries.csv"), updatedLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException e) {
            logger.severe("Error deleting entry: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteCalendar(UUID id) {
        try {
            List<String> lines = Files.readAllLines(databasePath.resolve("calendars.csv"));
            List<String> updatedLines = new ArrayList<>();
            updatedLines.add(lines.get(0));
            for (String line : lines.subList(1, lines.size())) {
                if (!line.split(",")[0].equals(id.toString())) {
                    updatedLines.add(line);
                }
            }
            Files.write(databasePath.resolve("calendars.csv"), updatedLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            // Delete all entries for the calendar
            deleteEntriesByCalendarId(id);
            return true;
        } catch (IOException e) {
            logger.severe("Error deleting calendar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Entry readEntry(UUID id) {
        try {
            List<String> lines = Files.readAllLines(databasePath.resolve("entries.csv"));
            for (String line : lines.subList(1, lines.size())) {
                String[] parts = line.split(",");
                if (parts[0].equals(id.toString())) {
                    return new Entry(UUID.fromString(parts[0]), parts[1], parts[2], ZonedDateTime.parse(parts[3]), parts[4], parts[5].isEmpty() ? null : Category.valueOf(parts[5]), parts[6].isEmpty() ? null : Priority.valueOf(parts[6]), parts[7].isEmpty() ? null : Status.valueOf(parts[7]), parts[8], ZonedDateTime.parse(parts[9]));
                }
            }
        } catch (IOException e) {
            logger.severe("Error reading entry: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Calendar readCalendar(UUID id) {
        try {
            List<String> lines = Files.readAllLines(databasePath.resolve("calendars.csv"));
            for (String line : lines.subList(1, lines.size())) {
                String[] parts = line.split(",");
                if (parts[0].equals(id.toString())) {
                    Calendar calendar = new Calendar(UUID.fromString(parts[0]), parts[1], parts[2], new Entry[0], ZonedDateTime.parse(parts[3]));
                    List<Entry> entries = new ArrayList<>();
                    List<String> entryLines = Files.readAllLines(databasePath.resolve("entries.csv"));
                    for (String entryLine : entryLines.subList(1, entryLines.size())) {
                        String[] entryParts = entryLine.split(",");
                        if (entryParts[10].equals(id.toString())) {
                            entries.add(new Entry(UUID.fromString(entryParts[0]), entryParts[1], entryParts[2], ZonedDateTime.parse(entryParts[3]), entryParts[4], entryParts[5].isEmpty() ? null : Category.valueOf(entryParts[5]), entryParts[6].isEmpty() ? null : Priority.valueOf(entryParts[6]), entryParts[7].isEmpty() ? null : Status.valueOf(entryParts[7]), entryParts[8], ZonedDateTime.parse(entryParts[9])));
                        }
                    }
                    calendar.setEntries(entries.toArray(new Entry[0]));
                    return calendar;
                }
            }
        } catch (IOException e) {
            logger.severe("Error reading calendar: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Entry[] listEntries() {
        try {
            List<String> lines = Files.readAllLines(databasePath.resolve("entries.csv"));
            List<Entry> entries = new ArrayList<>();
            for (String line : lines.subList(1, lines.size())) {
                String[] parts = line.split(",");
                entries.add(new Entry(UUID.fromString(parts[0]), parts[1], parts[2], ZonedDateTime.parse(parts[3]), parts[4], parts[5].isEmpty() ? null : Category.valueOf(parts[5]), parts[6].isEmpty() ? null : Priority.valueOf(parts[6]), parts[7].isEmpty() ? null : Status.valueOf(parts[7]), parts[8], ZonedDateTime.parse(parts[9])));
            }
            return entries.toArray(new Entry[0]);
        } catch (IOException e) {
            logger.severe("Error listing entries: " + e.getMessage());
            return new Entry[0];
        }
    }

    @Override
    public Calendar[] listCalendars() {
        try {
            List<String> lines = Files.readAllLines(databasePath.resolve("calendars.csv"));
            List<Calendar> calendars = new ArrayList<>();
            for (String line : lines.subList(1, lines.size())) {
                String[] parts = line.split(",");
                Calendar calendar = new Calendar(UUID.fromString(parts[0]), parts[1], parts[2], new Entry[0], ZonedDateTime.parse(parts[3]));
                List<Entry> entries = new ArrayList<>();
                List<String> entryLines = Files.readAllLines(databasePath.resolve("entries.csv"));
                for (String entryLine : entryLines.subList(1, entryLines.size())) {
                    String[] entryParts = entryLine.split(",");
                    if (entryParts[10].equals(parts[0])) {
                        entries.add(new Entry(UUID.fromString(entryParts[0]), entryParts[1], entryParts[2], ZonedDateTime.parse(entryParts[3]), entryParts[4], entryParts[5].isEmpty() ? null : Category.valueOf(entryParts[5]), entryParts[6].isEmpty() ? null : Priority.valueOf(entryParts[6]), entryParts[7].isEmpty() ? null : Status.valueOf(entryParts[7]), entryParts[8], ZonedDateTime.parse(entryParts[9])));
                    }
                }
                calendar.setEntries(entries.toArray(new Entry[0]));
                calendars.add(calendar);
            }
            return calendars.toArray(new Calendar[0]);
        } catch (IOException e) {
            logger.severe("Error listing calendars: " + e.getMessage());
            return new Calendar[0];
        }
    }

    @Override
    public boolean createTables() {
        try {
            Files.createDirectories(databasePath);
            Files.write(databasePath.resolve("calendars.csv"), Collections.singletonList("uuid,name,description,createdAt"), StandardOpenOption.CREATE);
            Files.write(databasePath.resolve("entries.csv"), Collections.singletonList("uuid,title,description,dateAndTime,location,category,priority,status,notes,createdAt,calendarId"), StandardOpenOption.CREATE);
            return true;
        } catch (IOException e) {
            logger.severe("Error creating tables: " + e.getMessage());
            return false;
        }
    }
}