package com.calendar;

import com.calendar.commands.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CommandExecutor {
    private static final Logger logger = Logger.getLogger(Core.class.getName());
    private static CommandExecutor instance;

    private final List<Command> commandQueue = new ArrayList<>();
    private final Core core;

    private CommandExecutor(Core core) {
        this.core = core;
    }

    public static synchronized CommandExecutor getInstance(Core core) {
        if (instance == null) {
            instance = new CommandExecutor(core);
        }
        return instance;
    }

    public static synchronized CommandExecutor getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CommandExecutor is not initialized. Call getInstance(Core) first.");
        }
        return instance;
    }

    public void addCommand(Command command) {
        commandQueue.add(command);
    }

    public void executeCommands() {
        for (Command command : commandQueue) {
            command.execute(this.core);
        }
        commandQueue.clear();
    }
}
