package plugins.database;

import core.Database;
import entities.Entry;
import entities.enums.Category;
import entities.enums.Priority;
import entities.enums.Status;

import java.sql.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
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
    public boolean saveEntry(Entry entry) {
        String sql = "INSERT INTO entries(title, description, dateAndTime, location, category, priority, status, notes) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entry.getTitle());
            pstmt.setString(2, entry.getDescription());
            pstmt.setString(3, entry.getDateAndTime().toString());
            pstmt.setString(4, entry.getLocation());
            pstmt.setString(5, entry.getCategory() != null ? entry.getCategory().name() : null);
            pstmt.setString(6, entry.getPriority() != null ? entry.getPriority().name() : null);
            pstmt.setString(7, entry.getStatus() != null ? entry.getStatus().name() : null);
            pstmt.setString(8, entry.getNotes());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Entry readEntry(String title) {
        String sql = "SELECT * FROM entries WHERE title = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Entry(
                        rs.getString("title"),
                        rs.getString("description"),
                        ZonedDateTime.parse(rs.getString("dateAndTime")),
                        rs.getString("location"),
                        rs.getString("category") != null ? Category.valueOf(rs.getString("category")) : null,
                        rs.getString("priority") != null ? Priority.valueOf(rs.getString("priority")) : null,
                        rs.getString("status") != null ? Status.valueOf(rs.getString("status")) : null,
                        rs.getString("notes")
                );
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean deleteEntry(Entry entry) {
        String sql = "DELETE FROM entries WHERE title = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entry.getTitle());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateEntry(Entry entry) {
        String sql = "UPDATE entries SET description = ?, dateAndTime = ?, location = ?, category = ?, priority = ?, status = ?, notes = ? WHERE title = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entry.getDescription());
            pstmt.setString(2, entry.getDateAndTime().toString());
            pstmt.setString(3, entry.getLocation());
            pstmt.setString(4, entry.getCategory() != null ? entry.getCategory().name() : null);
            pstmt.setString(5, entry.getPriority() != null ? entry.getPriority().name() : null);
            pstmt.setString(6, entry.getStatus() != null ? entry.getStatus().name() : null);
            pstmt.setString(7, entry.getNotes());
            pstmt.setString(8, entry.getTitle());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Entry[] listEntries() {
        String sql = "SELECT * FROM entries";
        List<Entry> entries = new ArrayList<>();

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                entries.add(new Entry(
                        rs.getString("title"),
                        rs.getString("description"),
                        ZonedDateTime.parse(rs.getString("dateAndTime")),
                        rs.getString("location"),
                        rs.getString("category") != null ? Category.valueOf(rs.getString("category")) : null,
                        rs.getString("priority") != null ? Priority.valueOf(rs.getString("priority")) : null,
                        rs.getString("status") != null ? Status.valueOf(rs.getString("status")) : null,
                        rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return entries.toArray(new Entry[0]);
    }

    @Override
    public boolean createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS entries (" +
                "title TEXT PRIMARY KEY," +
                "description TEXT," +
                "dateAndTime TEXT NOT NULL," +
                "location TEXT," +
                "category TEXT," +
                "priority TEXT," +
                "status TEXT," +
                "notes TEXT" +
                ")";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }
}