package com.calendar.frontend.swing;

import com.calendar.Calendar;
import com.calendar.CommandExecutor;
import com.calendar.Core;
import com.calendar.Entry;
import com.calendar.commands.AddEntryCommand;
import com.calendar.commands.DeleteEntryCommand;
import com.calendar.commands.EditEntryCommand;
import com.calendar.frontend.swing.languages.Translation;
import com.calendar.frontend.swing.languages.de;
import com.calendar.frontend.swing.languages.en;
import com.calendar.interfaces.Frontend;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The FrontendMain class implements the Frontend interface and provides
 * methods to initialize and display a calendar application GUI.
 */
final public class FrontendMain implements Frontend {
    private static final Logger logger = Logger.getLogger(FrontendMain.class.getName());
    private static ZonedDateTime currentDate = ZonedDateTime.now();
    private Map<String, String> translations;
    private String language;
    List<Calendar> calendars;
    private CommandExecutor commandExecutor;
    private final String translationsPath = "/languages/";
    private int selectedDay = currentDate.getDayOfMonth();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    // GUI components
    JFrame frame;
    JButton todayButton;
    JLabel monthLabel;
    JPanel calendarPanel;
    private JTextArea entryTextArea;
    JPanel entryPanel;

    JButton createButton;
    JButton editButton;
    JButton deleteButton;

    private List<JCheckBox> entryCheckBoxes;

    /**
     * Default constructor for FrontendMain.
     */
    public FrontendMain(String language) {
        this.language = language;
    }

    /**
     * Initializes the plugins.frontend with the specified language.
     */
    // @Override
    public void initialize(CommandExecutor commandExecutor, Core core) {
        this.commandExecutor = commandExecutor;
        core.addObserver(this);

        this.translations = loadTranslations();

        this.frame = new JFrame(getTranslation("Title", "Calendar App"));
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(700, 500); // Höhe erhöht, um Platz für das Textfeld zu schaffen

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
        this.todayButton = new JButton(getTranslation("Today", "Today"));
        this.monthLabel = new JLabel("", SwingConstants.CENTER);
        this.monthLabel.setPreferredSize(new Dimension(120, 30));

        // Dropdown zur Sprachauswahl
        String[] languages = {"Deutsch", "English"};
        JComboBox<String> languageComboBox = new JComboBox<>(languages);
        languageComboBox.setSelectedItem(this.language.equals("de") ? "Deutsch" : "English");

        headerPanel.add(prevButton);
        headerPanel.add(this.monthLabel);
        headerPanel.add(this.todayButton);
        headerPanel.add(nextButton);
        headerPanel.add(languageComboBox);

        panel.add(headerPanel);

        calendarPanel = new JPanel();
        calendarPanel.setLayout(new GridLayout(7, 7)); // 7 Tage pro Woche, 7 Zeilen (1 für den Monat, 1 für die Wochentage)
        panel.add(calendarPanel);

        entryPanel = new JPanel();
        entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(entryPanel);
        scrollPane.setPreferredSize(new Dimension(700, 100));

        // Panel für die Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        createButton = new JButton(getTranslation("Create", "Create"));
        editButton = new JButton(getTranslation("Edit", "Edit"));
        deleteButton = new JButton(getTranslation("Delete", "Delete"));

        buttonPanel.add(createButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        createButton.addActionListener(e -> {
            showEntryPopup("Erstellen", null);
            showEntriesForDay(selectedDay);
        });
        editButton.addActionListener(e -> {
            Entry selectedEntry = getSelectedEntry();
            if (selectedEntry != null) {
                showEntryPopup("Bearbeiten", selectedEntry);
                showEntriesForDay(selectedDay);
            }
        });
        deleteButton.addActionListener(e -> {
            Entry selectedEntry = getSelectedEntry();
            if (selectedEntry != null) {
                showDeleteConfirmationPopup(selectedEntry);
                this.commandExecutor.executeCommands();
                showEntriesForDay(selectedDay);
            }
        });

        panel.add(scrollPane);
        panel.add(buttonPanel);

        // Action Listener für die Buttons
        prevButton.addActionListener(e -> {
            currentDate = currentDate.minusMonths(1);
            updateCalendar(calendarPanel);
        });

        nextButton.addActionListener(e -> {
            currentDate = currentDate.plusMonths(1);
            updateCalendar(calendarPanel);
        });

        this.todayButton.addActionListener(e -> {
            currentDate = ZonedDateTime.now(); // Setzt das aktuelle Datum
            updateCalendar(calendarPanel);
        });

        languageComboBox.addActionListener(e -> {
            String selected = (String) languageComboBox.getSelectedItem();
            this.language = selected.equals("Deutsch") ? "de" : "en";
            this.translations = loadTranslations(); // Re-lade Übersetzungen
            setLanguage(this.language);

            updateCalendar(this.calendarPanel);     // Re-render Kalender


        });


        updateCalendar(calendarPanel);

        this.frame.add(panel);
        this.frame.setVisible(true);
    }


    private void showDeleteConfirmationPopup(Entry entry) {
        JDialog dialog = new JDialog(frame, getTranslation("Delete","Delete"), true);
        dialog.setSize(300, 150);
        dialog.setLayout(new BorderLayout());

        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        messagePanel.add(new JLabel(getTranslation("DeleteConfirmText", "Are you sure you want to delete the entry?")));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton deleteButton = new JButton(getTranslation("Delete", "Delete"));
        JButton cancelButton = new JButton(getTranslation("Cancel", "Cancel"));

        deleteButton.addActionListener(e -> {
            UUID calendarId = getCalendarIdForEntry(entry);
            if (calendarId != null) {
                this.commandExecutor.addCommand(new DeleteEntryCommand(entry, calendarId));
            }
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);

        dialog.add(messagePanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private Entry getSelectedEntry() {
        for (JCheckBox checkBox : entryCheckBoxes) {
            if (checkBox.isSelected()) {
                Object prop = checkBox.getClientProperty("entry");
                if (prop instanceof Entry) {
                    return (Entry) prop;
                }
            }
        }
        return null;
    }

    private void showEntryPopup(String action, Entry entry) {
        JDialog dialog = new JDialog(frame, action, true);
        dialog.setSize(300, 200);
        dialog.setLayout(new BorderLayout());

        JPanel entryPanel = new JPanel(new GridLayout(0, 1));
        JTextField titleField = new JTextField(entry != null ? entry.getTitle() : "");
        JTextField dateField = new JTextField(entry != null ? entry.getDateAndTime().toString() : LocalDate.of(currentDate.getYear(), currentDate.getMonth(), selectedDay).atStartOfDay(currentDate.getZone()).toString());
        entryPanel.add(new JLabel(getTranslation("EditTitle", "Title")));
        entryPanel.add(titleField);
        entryPanel.add(new JLabel(getTranslation("Date", "Date")));
        entryPanel.add(dateField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton(getTranslation("Save", "Save"));
        JButton cancelButton = new JButton(getTranslation("Cancel", "Cancel"));

        saveButton.addActionListener(e -> {
            switch (action) {
                case "Erstellen" -> {
                    UUID calendarId = this.calendars.getFirst().getUuid();
                    Entry newEntry = new Entry(titleField.getText(), ZonedDateTime.parse(dateField.getText()));
                    commandExecutor.addCommand(new AddEntryCommand(newEntry, calendarId));
                    commandExecutor.executeCommands();
                }
                case "Bearbeiten" -> {
                    UUID calendarId = getCalendarIdForEntry(entry);
                    if (calendarId != null) {
                        entry.setTitle(titleField.getText());
                        entry.setDateAndTime(ZonedDateTime.parse(dateField.getText()));
                        commandExecutor.addCommand(new EditEntryCommand(entry, calendarId));
                        commandExecutor.executeCommands();
                    }
                }
            }
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(entryPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private UUID getCalendarIdForEntry(Entry entry) {
        if (entry == null) {
            return null;
        }
        for (Calendar calendar : this.calendars) {
            for (Entry e : calendar.getEntries()) {
                if (e.equals(entry)) {
                    return calendar.getUuid();
                }
            }
        }
        return null;
    }

    void showEntriesForDay(int day) {
        this.selectedDay = day;
        LocalDate selectedDate = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), day);
        entryPanel.removeAll();
        entryCheckBoxes = new ArrayList<>();

        for (Calendar calendar : calendars) {
            for (Entry entry : calendar.getEntries()) {
                if (entry.getDateAndTime().toLocalDate().equals(selectedDate)) {

                    // Hauptpanel für diesen Eintrag
                    JPanel entryBox = new JPanel(new BorderLayout(10, 10));
                    entryBox.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(8, 8, 8, 8),
                            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)
                    ));

                    JCheckBox checkBox = new JCheckBox();
                    checkBox.putClientProperty("entry", entry); //Merke das Entry-Objekt; Funktion von Swing
                    entryCheckBoxes.add(checkBox);

                    JPanel contentPanel = new JPanel();
                    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
                    contentPanel.setOpaque(false);

                    // Helper zum Hinzufügen eines Labels nur wenn Feld belegt ist
                    BiConsumer<String, String> addField = (label, value) -> {
                        if (value != null && !value.isBlank() && !value.toString().equals("null")) {
                            contentPanel.add(new JLabel("<html><b>" + label + ":</b> " + value + "</html>"));
                        }
                    };

                    // Felder hinzufügen (nur wenn belegt)
                    addField.accept("Titel", entry.getTitle());
                    addField.accept("Beschreibung", entry.getDescription());
                    if (entry.getDateAndTime() != null) {
                        addField.accept("Uhrzeit", entry.getDateAndTime().toLocalTime().toString());
                    }
                    addField.accept("Ort", entry.getLocation());
                    addField.accept("Kategorie", String.valueOf(entry.getCategory()));
                    addField.accept("Priorität", String.valueOf(entry.getPriority()));
                    addField.accept("Status", String.valueOf(entry.getStatus()));
                    addField.accept("Notizen", entry.getNotes());

                    if (entry.getCreatedAt() != null) {
                        addField.accept("Erstellt am", entry.getCreatedAt().toLocalDateTime().format(formatter));
                    }


                    entryBox.add(contentPanel, BorderLayout.CENTER);
                    entryBox.add(checkBox, BorderLayout.EAST);

                    entryPanel.add(entryBox);
                }
            }
        }

        entryPanel.revalidate();
        entryPanel.repaint();



        // Aktualisiere die Kalenderansicht, um die Markierung zu aktualisieren
        updateCalendar(calendarPanel);
    }

    /**
     * Sets the language for the plugins.frontend and updates the GUI components.
     *
     * @param language the new language to set
     */
    @Override
    public void setLanguage(String language) {
        this.language = language;
        this.translations = loadTranslations();
        this.todayButton.setText(translations.getOrDefault("Today", "Today"));
        this.frame.setTitle(translations.getOrDefault("Title", "Calendar App"));
        this.todayButton.setText(getTranslation("Today", "Today"));
        this.createButton.setText(getTranslation("Create", "Create"));
        this.editButton.setText(getTranslation("Edit", "Edit"));
        this.deleteButton.setText(getTranslation("Delete", "Delete"));
        this.frame.setTitle(getTranslation("Title", "Calendar App"));

        this.updateCalendar(this.calendarPanel);
    }

    /**
     * Updates the calendar panel with the current month and year.
     *
     * @param calendarPanel the panel to update
     */
    private void updateCalendar(JPanel calendarPanel) {
        calendarPanel.removeAll();

        // Aktuellen Monat und Jahr abrufen
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();
        String monthName = currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag(this.language));

        this.monthLabel.setText(monthName + " " + currentYear);

        // Wochentage hinzufügen
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String day : days) {
            calendarPanel.add(new JLabel(getTranslation(day, day), SwingConstants.CENTER));
        }


        // Ersten Tag des Monats abrufen
        LocalDate firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1);
        int startDay = (firstDayOfMonth.getDayOfWeek().getValue() + 6) % 7; // Montag = 0, Dienstag = 1, ...

        // Tage des Monats hinzufügen
        int daysInMonth = currentDate.toLocalDate().lengthOfMonth();
        for (int i = 0; i < startDay; i++) {
            calendarPanel.add(new JLabel("")); // Leere Labels für die Tage vor dem 1. des Monats
        }
        for (int day = 1; day <= daysInMonth; day++) {
            JButton dayButton = createDayButton(day);
            calendarPanel.add(dayButton);
        }

        // Auffüllen bis zu 49 Elemente im calendarPanel (7 Tage pro Woche, 7 Zeilen)
        for (int i = 0; i < (49 - 7 - (startDay + daysInMonth)); i++) {
            calendarPanel.add(new JLabel("")); // Leere Labels für die Tage nach dem letzten Tag des Monats
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    /**
     * Creates a button for a specific day.
     *
     * @param day the day to create the button for
     * @return the created JButton
     */
    private JButton createDayButton(int day) {
        JButton dayButton = new JButton(String.valueOf(day));

        // Markiere den heutigen Tag
        if (currentDate.getYear() == LocalDate.now().getYear() &&
                currentDate.getMonth() == LocalDate.now().getMonth() &&
                day == LocalDate.now().getDayOfMonth()) {
            dayButton.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
        }

        // Markiere den ausgewählten Tag
        if (day == selectedDay) {
            dayButton.setBackground(Color.YELLOW); // Hintergrundfarbe für den ausgewählten Tag
            dayButton.setOpaque(true);
        }

        dayButton.addActionListener(e -> showEntriesForDay(day));
        return dayButton;
    }

    /**
     * Loads translations from a file.
     *
     * @return a map of translations
     */
    private Map<String, String> loadTranslations() {
        Map<String, String> translations = new HashMap<>();
        Translation[] translationsArray = switch (this.language) {
            case "de" -> de.values();
            default -> en.values();
        };

        for (Translation translation : translationsArray) {
            translations.put(((Enum<?>) translation).name(), translation.getTranslation());
        }

        return translations;
    }

    @Override
    public void update(Calendar[] calendars) {
        if (calendars == null) {
            logger.log(Level.WARNING, "Received null calendars array in update method.");
            this.calendars = new ArrayList<>(); // Leere Liste initialisieren
        } else {
            this.calendars = new ArrayList<>(Arrays.asList(calendars));
        }
        updateCalendar(this.calendarPanel);
    }


    public String getTranslation(String key, String fallback) {
        return this.translations.getOrDefault(key, fallback);
    }

}