package com.calendar.frontend.swing.languages;

public enum de implements Translation {
    Mon("Montag"),
    Tue("Dienstag"),
    Wed("Mittwoch"),
    Thu("Donnerstag"),
    Fri("Freitag"),
    Sat("Samstag"),
    Sun("Sonntag"),
    Today("Heute"),
    Title("Kalender-App");

    private final String translation;

    de(String translation) {
        this.translation = translation;
    }

    @Override
    public String getTranslation() {
        return translation;
    }
}