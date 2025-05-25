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
    Title("Kalender-App"),
    Create("Erstellen"),
    Edit("Bearbeiten"),
    Delete("Löschen"),
    Save("Speichern"),
    Cancel("Abbrechen"),
    Date("Fälligkeitsdatum"),
    EditTitle("Title"),
    DeleteConfirmText("Sicher, dass du den Eintrag löschen willst?"),
    Description("Beschreibung"),
    Location("Standort"),
    Category("Kategorie"),
    Priority("Priorität"),
    Status("Status"),
    Notes("Notizen"),
    CreationDate("Erstellt am");

    private final String translation;

    de(String translation) {
        this.translation = translation;
    }

    @Override
    public String getTranslation() {
        return translation;
    }
}