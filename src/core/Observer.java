package core;

import entities.Calendar;

public interface Observer {
    void update(Calendar[] calendars);
}
