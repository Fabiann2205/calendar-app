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
        this.frame.setSize(700, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

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

        panel.add(headerPanel, BorderLayout.NORTH);

        calendarPanel = new JPanel();
        calendarPanel.setLayout(new GridLayout(7, 7)); // 7 days per week, 7 rows (1 for the month, 1 for the weekdays)
        panel.add(calendarPanel, BorderLayout.CENTER);

        // Add action listeners for buttons
        prevButton.addActionListener(e -> {
            currentDate = currentDate.minusMonths(1);
            updateCalendar(calendarPanel);
        });

        nextButton.addActionListener(e -> {
            currentDate = currentDate.plusMonths(1);
            updateCalendar(calendarPanel);
        });

        this.todayButton.addActionListener(e -> {
            currentDate = LocalDate.now(); // Set the current date
            updateCalendar(calendarPanel);
        });

        updateCalendar(calendarPanel);

        this.frame.add(panel);
        this.frame.setVisible(true);
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

        // Get current month and year
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();
        String monthName = currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag(this.language));

        this.monthLabel.setText(monthName + " " + currentYear);

        // Add weekdays
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String day : days) {
            calendarPanel.add(new JLabel(this.translations.getOrDefault(day, day), SwingConstants.CENTER));
        }

        // Get the first day of the month
        LocalDate firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1);
        int startDay = (firstDayOfMonth.getDayOfWeek().getValue() + 6) % 7; // Monday = 0, Tuesday = 1, ...

        // Add days of the month
        int daysInMonth = currentDate.lengthOfMonth();
        for (int i = 0; i < startDay; i++) {
            calendarPanel.add(new JLabel("")); // Empty labels for the days before the 1st of the month
        }
        for (int day = 1; day <= daysInMonth; day++) {
            JButton dayButton = createDayButton(day);
            calendarPanel.add(dayButton);
        }

        // Fill up to 49 elements in the calendarPanel (7 days per week, 7 rows)
        for (int i = 0; i < (49 - 7 - (startDay + daysInMonth)); i++) {
            calendarPanel.add(new JLabel("")); // Empty labels for the days before the 1st of the month
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