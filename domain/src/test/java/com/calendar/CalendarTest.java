package com.calendar;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CalendarTest {
    @Test
    void testConstructorWithNameAndDescription() {
        Calendar cal = new Calendar("name", "desc");

        assertNotNull(cal.getUuid());
        assertEquals("name", cal.getName());
        assertEquals("desc", cal.getDescription());
        assertNotNull(cal.getCreatedAt());
        assertEquals(0, cal.getEntryCount());
    }
    @Test
    void testSetNameAndDescription() {
        Calendar cal = new Calendar("a", "b");
        cal.setName("newName");
        cal.setDescription("newDesc");

        assertEquals("newName", cal.getName());
        assertEquals("newDesc", cal.getDescription());
    }
    @Test
    void testAddEntryIncreasesCount() {
        Calendar cal = new Calendar("name", "desc");
        Entry entry = new Entry("test", ZonedDateTime.now());
        assertTrue(cal.addEntry(entry));
        assertEquals(1, cal.getEntryCount());
        assertEquals(entry, cal.getEntries()[0]);
    }
    @Test
    void testRemoveEntryWorks() {
        Entry entry = new Entry("test", ZonedDateTime.now());
        Calendar cal = new Calendar("name", "desc");
        cal.addEntry(entry);

        assertTrue(cal.removeEntry(entry));
        assertEquals(0, cal.getEntryCount());
    }

    @Test
    void testRemoveNonExistingEntryReturnsFalse() {
        Entry entry1 = new Entry("a", ZonedDateTime.now());
        Entry entry2 = new Entry("b", ZonedDateTime.now());
        Calendar cal = new Calendar("name", "desc");
        cal.addEntry(entry1);

        assertFalse(cal.removeEntry(entry2));
        assertEquals(1, cal.getEntryCount());
    }
    @Test
    void testUpdateEntryReplacesEntry() {
        Entry original = new Entry("old", ZonedDateTime.now());
        Calendar cal = new Calendar("name", "desc");
        cal.addEntry(original);
        assertEquals("old", cal.getEntries()[0].getTitle());

        Entry updated = cal.getEntries()[0];
        updated.setTitle("new");
        assertTrue(cal.updateEntry(updated));
        assertEquals("new", cal.getEntries()[0].getTitle());
    }

    @Test
    void testUpdateNonExistingEntryReturnsFalse() {
        Entry notInCalendar = new Entry("x", ZonedDateTime.now());
        Calendar cal = new Calendar("name", "desc");

        assertFalse(cal.updateEntry(notInCalendar));
    }
    @Test
    void testGetEntryByUuid() {
        Entry entry = new Entry("findMe", ZonedDateTime.now());
        Calendar cal = new Calendar("name", "desc");
        cal.addEntry(entry);

        Entry result = cal.getEntry(entry.getUuid());
        assertNotNull(result);
        assertEquals("findMe", result.getTitle());
    }

    @Test
    void testGetEntryWithUnknownUuidReturnsNull() {
        Calendar cal = new Calendar("name", "desc");
        assertNull(cal.getEntry(UUID.randomUUID()));
    }
    @Test
    void testEqualsWithSameUuid() {
        UUID id = UUID.randomUUID();
        Calendar cal1 = new Calendar(id, "desc", "name", new Entry[]{}, ZonedDateTime.now());
        Calendar cal2 = new Calendar(id, "desc2", "name2", new Entry[]{}, ZonedDateTime.now());

        assertEquals(cal1, cal2);
    }

    @Test
    void testNotEqualsWithDifferentUuid() {
        Calendar cal1 = new Calendar("desc", "name");
        Calendar cal2 = new Calendar("desc", "name");

        assertNotEquals(cal1, cal2);
    }

}