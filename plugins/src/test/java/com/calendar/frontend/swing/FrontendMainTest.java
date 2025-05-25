package com.calendar.frontend.swing;

import com.calendar.Calendar;
import com.calendar.CommandExecutor;
import com.calendar.Core;
import com.calendar.Entry;
import com.calendar.interfaces.Database;
import com.calendar.interfaces.Frontend;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JTextComponentMatcher;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.Robot;
import java.awt.event.InputEvent;

import static com.calendar.frontend.swing.languages.en.Title;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;

import java.awt.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

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
        entry = new Entry("Test Entry", ZonedDateTime.now());;

        // Arrange: Frontend im EDT erstellen
        frontend = GuiActionRunner.execute(() -> {
            FrontendMain app = new FrontendMain("de");
            app.initialize(core.commandExecutor, core);
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
    void testLanguageSwitchToEnglishUpdateVisibleElements() {
        // Setze Sprache auf Englisch

        frontend = GuiActionRunner.execute(() -> {
                    frontend.setLanguage("en");
                    return frontend;
        });

        assertEquals("Today", frontend.todayButton.getText());
        assertEquals("Create", frontend.createButton.getText());
        assertEquals("Edit", frontend.editButton.getText());
        assertEquals("Delete", frontend.deleteButton.getText());
        assertEquals("Calendar App", frontend.frame.getTitle());
        // Überprüfe Monatslabel (z.B. "May 2025" o. ä.)
        String expectedMonth = LocalDate.now()
                .getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        String monthLabelText = frontend.monthLabel.getText().toLowerCase(Locale.ROOT);
        assertTrue(monthLabelText.toLowerCase().contains(expectedMonth.toLowerCase()),
                "Month label should contain '" + expectedMonth + "', but was '" + monthLabelText + "'");

        // Überprüfe Wochentage in calendarPanel
        boolean weekdayLabelFound = false;
        for (java.awt.Component comp : frontend.calendarPanel.getComponents()) {
            if (comp instanceof JLabel label) {
                if (label.getText().equals("Monday") || label.getText().equals("Tuesday")) {
                    weekdayLabelFound = true;
                    break;
                }
            }
        }
        assertTrue(weekdayLabelFound, "Expected weekday label in English not found.");
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
