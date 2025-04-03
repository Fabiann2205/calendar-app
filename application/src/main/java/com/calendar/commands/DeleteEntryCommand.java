package com.calendar.commands;

import com.calendar.Core;
import com.calendar.Entry;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeleteEntryCommand implements Command {
    private static final Logger logger = Logger.getLogger(DeleteEntryCommand.class.getName());
    private final Entry entry;
    private final UUID calendarId;

    public DeleteEntryCommand(Entry entry, UUID calendarId) {
        this.entry = entry;
        this.calendarId = calendarId;
    }

    @Override
    public void execute(Core core) {
        if (core.removeEntry(entry, calendarId)) {
            logger.log(Level.INFO, "Entry deleted successfully.");
        } else {
            logger.log(Level.INFO, "Failed to delete entry.");
        }
    }
}