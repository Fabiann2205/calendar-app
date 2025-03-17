package core;

import entities.Calendar;
import entities.Entry;

import java.time.ZonedDateTime;
import java.util.logging.Logger;

public class Core {
    private static final Logger logger = Logger.getLogger(Core.class.getName());
    private final Database database;
    private final Frontend frontend;

    public Core(Database database, Frontend frontend) {
        this.database = database;
        this.frontend = frontend;
        this.database.createTables();
    }

    public boolean addEntry() {
        Calendar calendar = new Calendar("My Calendar", "This is my calendar");
        Entry entry = new Entry("My Entry", "This is my entry", ZonedDateTime.now(), "My Location", null, null, null, "These are my notes");
        Entry entry1 = new Entry("My Entry1", "This is my entry1", ZonedDateTime.now(), "My Location1", null, null, null, "These are my notes1");
        calendar.addEntry(entry);
        calendar.addEntry(entry1);
        database.save(calendar);
        database.save(calendar);
        return true;
    }

    public boolean removeEntry() {
        Calendar[] calendars = database.listCalendars();
        Entry[] entries = calendars[calendars.length - 1].getEntries();
        calendars[0].removeEntry(entries[0]);
        database.updateCalendar(calendars[0].getUuid(), calendars[0]);
        return true;
    }

    public boolean editEntry() {
        return true;
    }

    public boolean viewEntry() {
        return true;
    }

    public boolean viewAllEntries() {
        return true;
    }

    public boolean viewEntriesByCategory() {
        return true;
    }

    public boolean viewEntriesByPriority() {
        return true;
    }

    public boolean viewEntriesByStatus() {
        return true;
    }
}
