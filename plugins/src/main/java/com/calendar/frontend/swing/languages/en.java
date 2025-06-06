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
    Title("Calendar App"),
    Create("Create"),
    Edit("Edit"),
    Delete("Delete"),
    Save("Save"),
    Cancel("Cancel"),
    Date("Due date"),
    EditTitle("Title"),
    DeleteConfirmText("Are you sure you want to delete the entry?"),
    Description("Description"),
    Location("Location"),
    Category("Category"),
    Priority("Priority"),
    Status("Status"),
    Notes("Notes"),
    CreationDate("Created on"),
    AddCalendar("Add Calendar"),
    Calendar("Calendar");
    private final String translation;

    en(String translation) {
        this.translation = translation;
    }

    @Override
    public String getTranslation() {
        return translation;
    }
}