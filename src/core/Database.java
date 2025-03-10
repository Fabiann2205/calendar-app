package core;

import entities.Entry;

public interface Database {
    boolean saveEntry(Entry entry);

    Entry readEntry(String title);

    Entry readEntry(int id);

    boolean deleteEntry(Entry entry);

    boolean deleteEntry(int id);

    boolean updateEntry(int id, Entry entry);

    Entry[] listEntries();

    boolean createTables();
}
