package com.calendar.commands;

import com.calendar.Calendar;
import com.calendar.Core;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AddCalendarCommand implements Command {
    private static final Logger logger = Logger.getLogger(AddCalendarCommand.class.getName());
    private final Calendar calendar;

    public AddCalendarCommand(Calendar calendar) {
        this.calendar = calendar;
    }

    @Override
    public void execute(Core core) {
        if (core.saveCalendar(calendar)) {
            logger.log(Level.INFO, "Calendar added successfully.");
        } else {
            logger.log(Level.INFO, "Failed to add calendar.");
        }
    }
}