package com.calendar.database;

import com.calendar.Calendar;
import com.calendar.Entry;
import com.calendar.enums.Category;
import com.calendar.enums.Priority;
import com.calendar.enums.Status;
import com.calendar.interfaces.Database;

import java.sql.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class SQLiteDatabase implements Database {
    private static final Logger logger = Logger.getLogger(SQLiteDatabase.class.getName());
    private final String url;

    public SQLiteDatabase(String url) {
        this.url = url;
        createTables();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }

    @Override
    public boolean save(Calendar calendar) {
        String checkSql = "SELECT COUNT(*) FROM calendars WHERE uuid = ?";
        String insertSql = "INSERT INTO calendars (uuid, name, description, createdAt) VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE calendars SET name = ?, description = ?, createdAt = ? WHERE uuid = ?";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            checkStmt.setString(1, calendar.getUuid().toString());
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            boolean exists = rs.getInt(1) > 0;

            if (exists) {
                updateStmt.setString(1, calendar.getName());
                updateStmt.setString(2, calendar.getDescription());
                updateStmt.setString(3, calendar.getCreatedAt().toString());
                updateStmt.setString(4, calendar.getUuid().toString());
                updateStmt.executeUpdate();
            } else {
                insertStmt.setString(1, calendar.getUuid().toString());
                insertStmt.setString(2, calendar.getName());
                insertStmt.setString(3, calendar.getDescription());
                insertStmt.setString(4, calendar.getCreatedAt().toString());
                insertStmt.executeUpdate();
            }

            // Delete all existing entries for the calendar
            String deleteEntriesSql = "DELETE FROM entries WHERE calendarId = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteEntriesSql)) {
                deleteStmt.setString(1, calendar.getUuid().toString());
                deleteStmt.executeUpdate();
            }

        } catch (SQLException e) {
            logger.severe("Error saving calendar: " + e.getMessage());
            return false;
        }
        for (Entry entry : calendar.getEntries()) {
            save(entry, calendar.getUuid());
        }
        return true;
    }

    @Override
    public boolean save(Entry entry, UUID calendarId) {
        String checkSql = "SELECT COUNT(*) FROM entries WHERE uuid = ?";
        String insertSql = "INSERT INTO entries (uuid, title, description, dateAndTime, location, category, priority, status, notes, createdAt, calendarId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE entries SET title = ?, description = ?, dateAndTime = ?, location = ?, category = ?, priority = ?, status = ?, notes = ?, createdAt = ?, calendarId = ? WHERE uuid = ?";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            checkStmt.setString(1, entry.getUuid().toString());
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            boolean exists = rs.getInt(1) > 0;

            if (exists) {
                updateStmt.setString(1, entry.getTitle());
                updateStmt.setString(2, entry.getDescription());
                updateStmt.setString(3, entry.getDateAndTime().toString());
                updateStmt.setString(4, entry.getLocation());
                updateStmt.setString(5, entry.getCategory() != null ? entry.getCategory().name() : null);
                updateStmt.setString(6, entry.getPriority() != null ? entry.getPriority().name() : null);
                updateStmt.setString(7, entry.getStatus() != null ? entry.getStatus().name() : null);
                updateStmt.setString(8, entry.getNotes());
                updateStmt.setString(9, entry.getCreatedAt().toString());
                updateStmt.setString(10, calendarId.toString());
                updateStmt.setString(11, entry.getUuid().toString());
                updateStmt.executeUpdate();
            } else {
                insertStmt.setString(1, entry.getUuid().toString());
                insertStmt.setString(2, entry.getTitle());
                insertStmt.setString(3, entry.getDescription());
                insertStmt.setString(4, entry.getDateAndTime().toString());
                insertStmt.setString(5, entry.getLocation());
                insertStmt.setString(6, entry.getCategory() != null ? entry.getCategory().name() : null);
                insertStmt.setString(7, entry.getPriority() != null ? entry.getPriority().name() : null);
                insertStmt.setString(8, entry.getStatus() != null ? entry.getStatus().name() : null);
                insertStmt.setString(9, entry.getNotes());
                insertStmt.setString(10, entry.getCreatedAt().toString());
                insertStmt.setString(11, calendarId.toString());
                insertStmt.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            logger.severe("Error saving entry: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Entry readEntry(UUID id) {
        String sql = "SELECT * FROM entries WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Entry(
                        UUID.fromString(rs.getString("uuid")),
                        rs.getString("title"),
                        rs.getString("description"),
                        ZonedDateTime.parse(rs.getString("dateAndTime")),
                        rs.getString("location"),
                        rs.getString("category") != null ? Category.valueOf(rs.getString("category")) : null,
                        rs.getString("priority") != null ? Priority.valueOf(rs.getString("priority")) : null,
                        rs.getString("status") != null ? Status.valueOf(rs.getString("status")) : null,
                        rs.getString("notes"),
                        ZonedDateTime.parse(rs.getString("createdAt"))
                );
            }
        } catch (SQLException e) {
            logger.severe("Error reading entry: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Calendar readCalendar(UUID id) {
        String sql = "SELECT * FROM calendars WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Calendar calendar = new Calendar(
                        UUID.fromString(rs.getString("uuid")),
                        rs.getString("name"),
                        rs.getString("description"),
                        new Entry[0],
                        ZonedDateTime.parse(rs.getString("createdAt"))
                );
                // Read entries for the calendar
                List<Entry> entries = new ArrayList<>();
                String entrySql = "SELECT * FROM entries WHERE calendarId = ?";
                try (PreparedStatement entryStmt = conn.prepareStatement(entrySql)) {
                    entryStmt.setString(1, calendar.getUuid().toString());
                    ResultSet entryRs = entryStmt.executeQuery();
                    while (entryRs.next()) {
                        entries.add(new Entry(
                                UUID.fromString(entryRs.getString("uuid")),
                                entryRs.getString("title"),
                                entryRs.getString("description"),
                                ZonedDateTime.parse(entryRs.getString("dateAndTime")),
                                entryRs.getString("location"),
                                entryRs.getString("category") != null ? Category.valueOf(entryRs.getString("category")) : null,
                                entryRs.getString("priority") != null ? Priority.valueOf(entryRs.getString("priority")) : null,
                                entryRs.getString("status") != null ? Status.valueOf(entryRs.getString("status")) : null,
                                entryRs.getString("notes"),
                                ZonedDateTime.parse(entryRs.getString("createdAt"))
                        ));
                    }
                }
                calendar.setEntries(entries.toArray(new Entry[0]));
                return calendar;
            }
        } catch (SQLException e) {
            logger.severe("Error reading calendar: " + e.getMessage());
        }
        return null;
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
        String sql = "DELETE FROM entries WHERE uuid = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.severe("Error deleting entry: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteCalendar(UUID id) {
        String sql = "DELETE FROM calendars WHERE uuid = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.severe("Error deleting calendar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateEntry(UUID id, Entry entry) {
        String sql = "UPDATE entries SET title = ?, description = ?, dateAndTime = ?, location = ?, category = ?, priority = ?, status = ?, notes = ?, createdAt = ? WHERE uuid = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entry.getTitle());
            pstmt.setString(2, entry.getDescription());
            pstmt.setString(3, entry.getDateAndTime().toString());
            pstmt.setString(4, entry.getLocation());
            pstmt.setString(5, entry.getCategory() != null ? entry.getCategory().name() : null);
            pstmt.setString(6, entry.getPriority() != null ? entry.getPriority().name() : null);
            pstmt.setString(7, entry.getStatus() != null ? entry.getStatus().name() : null);
            pstmt.setString(8, entry.getNotes());
            pstmt.setString(9, entry.getCreatedAt().toString());
            pstmt.setString(10, id.toString());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.severe("Error updating entry: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateCalendar(UUID id, Calendar calendar) {
        String checkSql = "SELECT COUNT(*) FROM calendars WHERE uuid = ?";
        String insertSql = "INSERT INTO calendars (uuid, name, description, createdAt) VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE calendars SET name = ?, description = ?, createdAt = ? WHERE uuid = ?";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            checkStmt.setString(1, id.toString());
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            boolean exists = rs.getInt(1) > 0;

            if (exists) {
                updateStmt.setString(1, calendar.getName());
                updateStmt.setString(2, calendar.getDescription());
                updateStmt.setString(3, calendar.getCreatedAt().toString());
                updateStmt.setString(4, id.toString());
                updateStmt.executeUpdate();
            } else {
                insertStmt.setString(1, calendar.getUuid().toString());
                insertStmt.setString(2, calendar.getName());
                insertStmt.setString(3, calendar.getDescription());
                insertStmt.setString(4, calendar.getCreatedAt().toString());
                insertStmt.executeUpdate();
            }

            // Delete all existing entries for the calendar
            String deleteEntriesSql = "DELETE FROM entries WHERE calendarId = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteEntriesSql)) {
                deleteStmt.setString(1, calendar.getUuid().toString());
                deleteStmt.executeUpdate();
            }
        } catch (SQLException e) {
            logger.severe("Error updating calendar: " + e.getMessage());
            return false;
        }

        for (Entry entry : calendar.getEntries()) {
            save(entry, calendar.getUuid());
        }
        return true;
    }

    @Override
    public Entry[] listEntries() {
        String sql = "SELECT * FROM entries";
        List<Entry> entries = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                entries.add(new Entry(
                        UUID.fromString(rs.getString("uuid")),
                        rs.getString("title"),
                        rs.getString("description"),
                        ZonedDateTime.parse(rs.getString("dateAndTime")),
                        rs.getString("location"),
                        rs.getString("category") != null ? Category.valueOf(rs.getString("category")) : null,
                        rs.getString("priority") != null ? Priority.valueOf(rs.getString("priority")) : null,
                        rs.getString("status") != null ? Status.valueOf(rs.getString("status")) : null,
                        rs.getString("notes"),
                        ZonedDateTime.parse(rs.getString("createdAt"))
                ));
            }
        } catch (SQLException e) {
            logger.severe("Error listing entries: " + e.getMessage());
        }
        return entries.toArray(new Entry[0]);
    }

    @Override
    public Calendar[] listCalendars() {
        String sql = "SELECT * FROM calendars";
        List<Calendar> calendars = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Calendar calendar = new Calendar(
                        UUID.fromString(rs.getString("uuid")),
                        rs.getString("name"),
                        rs.getString("description"),
                        new Entry[0],
                        ZonedDateTime.parse(rs.getString("createdAt"))
                );
                // Read entries for the calendar
                List<Entry> entries = new ArrayList<>();
                String entrySql = "SELECT * FROM entries WHERE calendarId = ?";
                try (PreparedStatement entryStmt = conn.prepareStatement(entrySql)) {
                    entryStmt.setString(1, calendar.getUuid().toString());
                    ResultSet entryRs = entryStmt.executeQuery();
                    while (entryRs.next()) {
                        entries.add(new Entry(
                                UUID.fromString(entryRs.getString("uuid")),
                                entryRs.getString("title"),
                                entryRs.getString("description"),
                                ZonedDateTime.parse(entryRs.getString("dateAndTime")),
                                entryRs.getString("location"),
                                entryRs.getString("category") != null ? Category.valueOf(entryRs.getString("category")) : null,
                                entryRs.getString("priority") != null ? Priority.valueOf(entryRs.getString("priority")) : null,
                                entryRs.getString("status") != null ? Status.valueOf(entryRs.getString("status")) : null,
                                entryRs.getString("notes"),
                                ZonedDateTime.parse(entryRs.getString("createdAt"))
                        ));
                    }
                }
                calendar.setEntries(entries.toArray(new Entry[0]));
                calendars.add(calendar);
            }
        } catch (SQLException e) {
            logger.severe("Error listing calendars: " + e.getMessage());
        }
        return calendars.toArray(new Calendar[0]);
    }

    @Override
    public boolean createTables() {
        String createCalendarsTable = "CREATE TABLE IF NOT EXISTS calendars (" +
                "uuid TEXT PRIMARY KEY NOT NULL," +
                "name TEXT NOT NULL," +
                "description TEXT," +
                "createdAt TEXT NOT NULL" +
                ")";
        String createEntriesTable = "CREATE TABLE IF NOT EXISTS entries (" +
                "uuid TEXT PRIMARY KEY NOT NULL," +
                "title TEXT NOT NULL," +
                "description TEXT," +
                "dateAndTime TEXT NOT NULL," +
                "location TEXT," +
                "category TEXT," +
                "priority TEXT," +
                "status TEXT," +
                "notes TEXT," +
                "createdAt TEXT NOT NULL," +
                "calendarId TEXT NOT NULL," +
                "FOREIGN KEY(calendarId) REFERENCES calendars(uuid)" +
                ")";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createCalendarsTable);
            stmt.execute(createEntriesTable);
            return true;
        } catch (SQLException e) {
            logger.severe("Error creating tables: " + e.getMessage());
            return false;
        }
    }
}