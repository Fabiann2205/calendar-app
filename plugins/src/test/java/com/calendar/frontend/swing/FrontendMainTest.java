package com.calendar.frontend.swing;

import com.calendar.Calendar;
import com.calendar.CommandExecutor;
import com.calendar.Core;
import com.calendar.Entry;
import com.calendar.interfaces.Database;
import com.calendar.interfaces.Frontend;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FrontendMainTest {

    private FrameFixture window;
    private FrontendMain frontend;

    private Database mockDatabase;
    private Frontend mockFrontend;
    private Core core;
    private Calendar calendar;
    private Entry entry;


    @BeforeAll
    public static void setUpOnce() {
        // Stellt sicher, dass alle Swing-Komponenten im EDT erstellt werden
        FailOnThreadViolationRepaintManager.install();

    }

    @BeforeEach
    public void setUp() {
        mockDatabase = mock();
        mockFrontend = mock();
        when(mockDatabase.listCalendars()).thenReturn(new Calendar[0]);

        core = new Core(mockDatabase, mockFrontend);

        calendar = new Calendar("Test Calendar", "Desc");
        entry = new Entry("Test Entry", ZonedDateTime.now());
        ;

        // Arrange: Frontend im EDT erstellen
        frontend = GuiActionRunner.execute(() -> {
            FrontendMain app = new FrontendMain("en");
            app.initialize(CommandExecutor.getInstance(), core);
            return app;
        });


        window = new FrameFixture(frontend.frame);
    }

    @AfterEach
    public void tearDown() {
        if (window != null) {
            window.cleanUp();
        }
    }

    @Test
    void testUIShows() {
        window.show();
        assertTrue(window.target().isVisible());
    }


    @Test
    void testButtonLabelsAreInEnglish() {
        assertEquals("Today", frontend.todayButton.getText(), "Today button label incorrect");
        assertEquals("Create", frontend.createButton.getText(), "Create button label incorrect");
        assertEquals("Edit", frontend.editButton.getText(), "Edit button label incorrect");
        assertEquals("Delete", frontend.deleteButton.getText(), "Delete button label incorrect");
    }

    @Test
    void testFrameTitleIsInEnglish() {
        assertEquals("Calendar App", frontend.frame.getTitle(), "Frame title should be 'Calendar App'");
    }

    @Test
    void testMonthLabelIsCurrentEnglishMonth() {
        String expectedMonth = LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toLowerCase(Locale.ROOT);
        String actualLabel = frontend.monthLabel.getText().toLowerCase(Locale.ROOT);

        assertTrue(actualLabel.contains(expectedMonth),
                () -> "Month label should contain '" + expectedMonth + "', but was '" + actualLabel + "'");
    }

    @Test
    void testCalendarDisplaysWeekdayLabelsInEnglish() {
        List<String> expectedDays = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

        List<String> actualLabels = getWeekdayLabelsFromCalendar();

        for (String day : expectedDays) {
            assertTrue(actualLabels.contains(day),
                    () -> "Expected weekday '" + day + "' not found in calendar panel. Found: " + actualLabels);
        }
    }

    private List<String> getWeekdayLabelsFromCalendar() {
        return java.util.Arrays.stream(frontend.calendarPanels.get(0).getComponents())
                .filter(JLabel.class::isInstance)
                .map(c -> ((JLabel) c).getText())
                .toList();
    }

    @Test
    void testCreateEntryAndDisplayInUI() {

        GuiActionRunner.execute(() -> {
            calendar.addEntry(entry);
            frontend.calendars = new ArrayList<>();
            frontend.calendars.add(calendar);
            frontend.showEntriesForDay(ZonedDateTime.now().getDayOfMonth());
            return null;
        });

        boolean found = false;
        for (Component comp : frontend.entryPanel.getComponents()) {
            if (comp instanceof JPanel panel) {
                for (Component inner : panel.getComponents()) {
                    if (inner instanceof JPanel content) {
                        for (Component label : content.getComponents()) {
                            if (label instanceof JLabel jLabel && jLabel.getText().contains("Test Entry")) {
                                found = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        assertTrue(found, "Der Eintrag 'Test Entry' sollte im UI erscheinen.");

    }
}
