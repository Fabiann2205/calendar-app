package core.interfaces;

import entities.Calendar;
import entities.Entry;

import java.util.UUID;

public interface Database {
    boolean save(Calendar calendar);

    boolean save(Entry entry, UUID calendarId);

    Entry readEntry(UUID id);

    Calendar readCalendar(UUID id);

    boolean deleteEntry(Entry entry);

    boolean deleteCalendar(Calendar calendar);

    boolean deleteEntry(UUID id);

    boolean deleteCalendar(UUID id);

    boolean updateEntry(UUID id, Entry entry);

    boolean updateCalendar(UUID id, Calendar calendar);

    Entry[] listEntries();

    Calendar[] listCalendars();

    boolean createTables();
}
