package core.interfaces;

import entities.Calendar;

public interface Observer {
    void update(Calendar[] calendars);
}
