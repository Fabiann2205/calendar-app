package com.calendar;

import com.calendar.database.SQLiteDatabase;
import com.calendar.frontend.swing.FrontendMain;

public class Main {
    public static void main(String[] args) {
        // use SQLite as database
        // Get the user's home directory
        String homeDir = System.getenv("HOME");
        String dbUrl = "jdbc:sqlite:" + homeDir + "/Documents/calendar-app/data.db";

        Core core = new Core(new SQLiteDatabase(dbUrl), new FrontendMain("de"));

        // use JavaArrayDatabase as database
        //Core core = new Core(new JavaArrayDatabase(), new FrontendMain("de"));
    }
}
