package plugins.database;

import core.Database;
import entities.Entry;

public class FileDatabase implements Database {
    private String path;

    public FileDatabase(String path) {
        this.path = path;
    }

    @Override
    public boolean saveEntry(Entry entry) {
        return false;
    }

    @Override
    public Entry readEntry(String title) {
        return null;
    }

    @Override
    public boolean deleteEntry(Entry entry) {
        return false;
    }

    @Override
    public boolean updateEntry(Entry entry) {
        return false;
    }

    @Override
    public Entry[] listEntries() {
        return new Entry[0];
    }
}
