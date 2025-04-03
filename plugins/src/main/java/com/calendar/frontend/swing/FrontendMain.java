package com.calendar.frontend.swing;

import com.calendar.Calendar;
import com.calendar.CommandExecutor;
import com.calendar.Core;
import com.calendar.Entry;
import com.calendar.frontend.swing.languages.Translation;
import com.calendar.frontend.swing.languages.de;
import com.calendar.frontend.swing.languages.en;
import com.calendar.interfaces.Frontend;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The FrontendMain class implements the Frontend interface and provides
 * methods to initialize and display a calendar application GUI.
 */
final public class FrontendMain implements Frontend {
    private static final Logger logger = Logger.getLogger(FrontendMain.class.getName());
    private static LocalDate currentDate = LocalDate.now();
    private Map<String, String> translations;
    private String language;
    private List<Calendar> calendars;
    private CommandExecutor commandExecutor;
    private final String translationsPath = "/languages/";

    // GUI components
    private JFrame frame;
    private JButton todayButton;
    private JLabel monthLabel;
    private JPanel calendarPanel;
    private JTextArea entryTextArea;
    private JPanel entryPanel;

    private JButton createButton;
    private JButton editButton;
    private JButton deleteButton;

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

        this.frame = new JFrame(this.translations.getOrDefault("Title", "Calendar App"));
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(700, 500); // Höhe erhöht, um Platz für das Textfeld zu schaffen

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
        this.todayButton = new JButton(this.translations.getOrDefault("Today", "Today"));
        this.monthLabel = new JLabel("", SwingConstants.CENTER);
        this.monthLabel.setPreferredSize(new Dimension(120, 30));

        headerPanel.add(prevButton);
        headerPanel.add(this.monthLabel);
        headerPanel.add(this.todayButton);
        headerPanel.add(nextButton);

        panel.add(headerPanel);

        calendarPanel = new JPanel();
        calendarPanel.setLayout(new GridLayout(7, 7)); // 7 Tage pro Woche, 7 Zeilen (1 für den Monat, 1 für die Wochentage)
        panel.add(calendarPanel);

        // Neues Panel für die Einträge und Buttons
        entryPanel = new JPanel();
        entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(entryPanel);
        scrollPane.setPreferredSize(new Dimension(700, 100));

        // Panel für die Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        createButton = new JButton("Erstellen");
        editButton = new JButton("Bearbeiten");
        deleteButton = new JButton("Löschen");

        buttonPanel.add(createButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

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
            currentDate = LocalDate.now(); // Setzt das aktuelle Datum
            updateCalendar(calendarPanel);
        });

        updateCalendar(calendarPanel);

        this.frame.add(panel);
        this.frame.setVisible(true);
    }

    private void showEntriesForDay(int day) {
        LocalDate selectedDate = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), day);
        entryPanel.removeAll();
        entryCheckBoxes = new ArrayList<>();

        for (Calendar calendar : calendars) {
            for (Entry entry : calendar.getEntries()) {
                if (entry.getDateAndTime().toLocalDate().equals(selectedDate)) {
                    JCheckBox checkBox = new JCheckBox(entry.toString());
                    entryCheckBoxes.add(checkBox);
                    entryPanel.add(checkBox);
                }
            }
        }

        entryPanel.revalidate();
        entryPanel.repaint();
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
            calendarPanel.add(new JLabel(this.translations.getOrDefault(day, day), SwingConstants.CENTER));
        }

        // Ersten Tag des Monats abrufen
        LocalDate firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1);
        int startDay = (firstDayOfMonth.getDayOfWeek().getValue() + 6) % 7; // Montag = 0, Dienstag = 1, ...

        // Tage des Monats hinzufügen
        int daysInMonth = currentDate.lengthOfMonth();
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
        if (currentDate.getYear() == LocalDate.now().getYear() && currentDate.getMonth() == LocalDate.now().getMonth() && day == LocalDate.now().getDayOfMonth()) {
            dayButton.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
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
        this.calendars = new ArrayList<>(Arrays.asList(calendars));
        updateCalendar(this.calendarPanel);

        // show popup with entries
        for (Calendar calendar : calendars) {
            for (Entry entry : calendar.getEntries()) {
                logger.log(Level.INFO, entry.toString());
            }
        }
    }
}