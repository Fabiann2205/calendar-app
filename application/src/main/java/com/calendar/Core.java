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
    private final CommandExecutor commandExecutor;
    private final List<Observer> observers = new ArrayList<>();

    private List<Calendar> calendars;

    public Core(Database database, Frontend frontend) {
        this.database = database;
        this.frontend = frontend;
        this.commandExecutor = new CommandExecutor(this);
        this.frontend.initialize(this.commandExecutor, this);
        this.database.createTables();
        this.calendars = new ArrayList<>(Arrays.asList(this.database.listCalendars()));

        // For Testing
        this.calendars.add(new Calendar("Test Calendar", "Test Description"));
        this.database.save(this.calendars.getFirst());
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
}
