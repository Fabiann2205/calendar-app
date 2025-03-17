package core.interfaces;

import core.CommandExecutor;
import core.Core;

public interface Frontend extends Observer {
    // void initialize();

    void setLanguage(String language);

    void initialize(CommandExecutor commandExecutor, Core core);
}
