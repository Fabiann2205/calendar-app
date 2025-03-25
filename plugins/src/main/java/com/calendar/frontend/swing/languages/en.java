package com.calendar.frontend.swing.languages;

public enum en implements Translation {
    Mon("Monday"),
    Tue("Tuesday"),
    Wed("Wednesday"),
    Thu("Thursday"),
    Fri("Friday"),
    Sat("Saturday"),
    Sun("Sunday"),
    Today("Today"),
    Title("Calendar App");

    private final String translation;

    en(String translation) {
        this.translation = translation;
    }

    @Override
    public String getTranslation() {
        return translation;
    }
}