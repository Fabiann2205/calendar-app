package com.calendar;


import com.calendar.enums.Category;
import com.calendar.enums.Priority;
import com.calendar.enums.Status;
import com.calendar.interfaces.Database;
import com.calendar.interfaces.Frontend;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoreTest {

    private Database mockDatabase;
    private Frontend mockFrontend;
    private Core core;
    private Entry entry;
    private Calendar calendar;

    @BeforeEach
    void setup() {
        mockDatabase = mock();
        mockFrontend = mock();

        calendar = new Calendar("Test Calendar", "Desc");
        entry = new Entry("Test Entry", ZonedDateTime.now());

        when(mockDatabase.listCalendars()).thenReturn(new Calendar[0]);

        core = new Core(mockDatabase, mockFrontend);
    }

    @Test
    void testAddEntry_callsSaveAndReturnsTrue() {
        //Arrange
        core.calendars.add(calendar);

        // Act
        boolean result = core.addEntry(entry, calendar.getUuid());

        // Assert
        assertTrue(result);
        assertEquals(1, calendar.getEntries().length);
        verify(mockDatabase).save(calendar);
    }

    @Test
    void testAddEntry_invalidCalendar() {
        boolean result = core.addEntry(entry, UUID.randomUUID());
        assertFalse(result);
    }

    @Test
    void testRemoveEntry_success() {
        core.calendars.add(calendar);
        boolean result = core.addEntry(entry, calendar.getUuid());
        assertTrue(result);
        assertEquals(1, calendar.getEntries().length);

        Entry entry2 = new Entry("Test Entry2", ZonedDateTime.now());

        core.addEntry(entry2, calendar.getUuid());
        assertEquals(2, calendar.getEntries().length);
        verify(mockDatabase, times(2)).save(calendar);


        core.removeEntry(entry2, calendar.getUuid());
        assertEquals(1, calendar.getEntries().length);

        verify(mockDatabase, times(3)).save(calendar);

    }

    @Test
    void testRemoveEntry_failure_entryNotFound() {
        core.calendars.add(calendar);
        Entry entryForFailure = new Entry("Test entryForFailure", ZonedDateTime.now());
        boolean result = core.removeEntry(entryForFailure, calendar.getUuid());
        assertFalse(result);
    }

    @Test
    void testEditEntry_success() {
        core.calendars.add(calendar);
        core.addEntry(entry, calendar.getUuid());

        String oldTitle = entry.getTitle();
        String oldDescription = entry.getDescription();
        ZonedDateTime oldDateTime = entry.getDateAndTime();
        String oldLocation = entry.getLocation();
        Category oldCategory = entry.getCategory();
        Priority oldPriority = entry.getPriority();
        Status oldStatus = entry.getStatus();
        String oldNotes = entry.getNotes();

        String newTitle = oldTitle + " edited";
        String newDescription = oldDescription + " updated";
        ZonedDateTime newDateTime = oldDateTime.plusDays(1);
        String newLocation = oldLocation + " Room 2";
        Category newCategory = Category.WORK;
        Priority newPriority = Priority.HIGH;
        Status newStatus = Status.DONE;
        String newNotes = oldNotes + " extra note";

        entry.setTitle(newTitle);
        entry.setDescription(newDescription);
        entry.setDateAndTime(newDateTime);
        entry.setLocation(newLocation);
        entry.setCategory(newCategory);
        entry.setPriority(newPriority);
        entry.setStatus(newStatus);
        entry.setNotes(newNotes);

        boolean success = core.editEntry(entry, calendar.getUuid());
        assertTrue(success);

        Entry updated = calendar.getEntry(entry.getUuid());

        assertEquals(newTitle, updated.getTitle());
        assertEquals(newDescription, updated.getDescription());
        assertEquals(newDateTime, updated.getDateAndTime());
        assertEquals(newLocation, updated.getLocation());
        assertEquals(newCategory, updated.getCategory());
        assertEquals(newPriority, updated.getPriority());
        assertEquals(newStatus, updated.getStatus());
        assertEquals(newNotes, updated.getNotes());

        assertEquals(entry.getCreatedAt(), updated.getCreatedAt());
    }

}
