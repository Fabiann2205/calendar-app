package com.calendar.interfaces;

import com.calendar.CommandExecutor;
import com.calendar.Core;

public interface Frontend extends Observer {
    // void initialize();

    void setLanguage(String language);

    void initialize(CommandExecutor commandExecutor, Core core);
}
