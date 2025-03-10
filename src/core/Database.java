package core;

import entities.Entry;

public interface Database {
    boolean saveEntry(Entry entry);

    Entry readEntry(String title);

    boolean deleteEntry(Entry entry);

    boolean updateEntry(Entry entry);

    Entry[] listEntries();
}
