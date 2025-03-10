package core;

public class Core {
    private final Database database;
    private final Frontend frontend;

    public Core(Database database, Frontend frontend) {
        this.database = database;
        this.frontend = frontend;
    }

    public boolean addEntry() {
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
