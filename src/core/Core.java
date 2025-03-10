package core;

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
        this.database.saveEntry(new Entry("test", ZonedDateTime.now()));
        return true;
    }

    public boolean removeEntry() {
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
