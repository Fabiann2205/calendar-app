package com.calendar;

import com.calendar.interfaces.Database;
import com.calendar.interfaces.Frontend;
import com.calendar.interfaces.Observable;
import com.calendar.interfaces.Observer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class Core implements Observable {
    private static final Logger logger = Logger.getLogger(Core.class.getName());
    private final Database database;
    private final Frontend frontend;
    public final CommandExecutor commandExecutor;
    private final List<Observer> observers = new ArrayList<>();

    List<Calendar> calendars;

    public Core(Database database, Frontend frontend) {
        this.database = database;
        this.frontend = frontend;
        this.commandExecutor = CommandExecutor.getInstance(this);
        this.frontend.initialize(this.commandExecutor, this);
        this.database.createTables();

        this.calendars = new ArrayList<>(Arrays.asList(this.database.listCalendars()));
        if (this.calendars.isEmpty()) {
            this.database.save(new Calendar("Private", "Private Calendar"));
        }
        this.calendars = new ArrayList<>(Arrays.asList(this.database.listCalendars()));
        notifyObservers();
    }

    public boolean addEntry(Entry entry, UUID calendarId) {
        for (Calendar calendar : calendars) {
            if (calendar.getUuid().equals(calendarId)) {
                calendar.addEntry(entry);
                database.save(calendar);
                notifyObservers();
                return true;
            }
        }
        return false;
    }

    public boolean removeEntry(Entry entry, UUID calendarId) {
        for (Calendar calendar : calendars) {
            if (calendar.getUuid().equals(calendarId)) {
                if (calendar.removeEntry(entry)) {
                    database.save(calendar);
                    notifyObservers();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean editEntry(Entry entry, UUID calendarId) {
        for (Calendar calendar : calendars) {
            if (calendar.getUuid().equals(calendarId)) {
                if (calendar.updateEntry(entry)) {
                    database.save(calendar);
                    notifyObservers();
                    return true;
                }
            }
        }
        return false;
    }

    public Entry viewEntry(UUID entryId, UUID calendarId) {
        for (Calendar calendar : calendars) {
            if (calendar.getUuid().equals(calendarId)) {
                return calendar.getEntry(entryId);
            }
        }
        return null;
    }

    public Entry[] viewAllEntries(UUID calendarId) {
        for (Calendar calendar : calendars) {
            if (calendar.getUuid().equals(calendarId)) {
                return calendar.getEntries();
            }
        }
        return new Entry[0];
    }

    @Override
    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        Calendar[] current_calendars = this.database.listCalendars();
        for (Observer observer : this.observers) {
            observer.update(current_calendars);
        }
    }

    public boolean saveCalendar(Calendar calendar) {
        boolean result = database.save(calendar);
        this.calendars = new ArrayList<>(Arrays.asList(this.database.listCalendars()));
        notifyObservers();
        return result;
    }
}
