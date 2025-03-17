package core;

import core.commands.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CommandExecutor {
    private static final Logger logger = Logger.getLogger(CommandExecutor.class.getName());
    private final List<Command> commandQueue = new ArrayList<>();
    private final Core core;

    public CommandExecutor(Core core) {
        this.core = core;
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
