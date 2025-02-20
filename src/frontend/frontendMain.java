package frontend;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

final public class frontendMain {
    private static final Logger logger = Logger.getLogger(frontendMain.class.getName());
    private static LocalDate currentDate = LocalDate.now();

    private frontendMain() {
        // Private constructor to prevent instantiation
    }

    public static void createAndShowGUI() {
        createAndShowGUI("en"); // Default language is English
    }

    public static void createAndShowGUI(String language) {
        // Load translations
        Map<String, String> translations = loadTranslations("resources/languages/" + language + ".txt");

        JFrame frame = new JFrame("Calendar App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
        JButton todayButton = new JButton(translations.getOrDefault("Today", "Today"));
        JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setPreferredSize(new Dimension(120, 30));

        headerPanel.add(prevButton);
        headerPanel.add(monthLabel);
        headerPanel.add(todayButton);
        headerPanel.add(nextButton);

        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel calendarPanel = new JPanel();
        calendarPanel.setLayout(new GridLayout(7, 7)); // 7 days per week, 7 rows (1 for the month, 1 for the weekdays)
        panel.add(calendarPanel, BorderLayout.CENTER);

        // Add action listeners for buttons
        prevButton.addActionListener(e -> {
            currentDate = currentDate.minusMonths(1);
            updateCalendar(calendarPanel, monthLabel, translations, language);
        });

        nextButton.addActionListener(e -> {
            currentDate = currentDate.plusMonths(1);
            updateCalendar(calendarPanel, monthLabel, translations, language);
        });

        todayButton.addActionListener(e -> {
            currentDate = LocalDate.now(); // Setze das aktuelle Datum
            updateCalendar(calendarPanel, monthLabel, translations, language);
        });

        updateCalendar(calendarPanel, monthLabel, translations, language);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void updateCalendar(JPanel calendarPanel, JLabel monthLabel, Map<String, String> translations, String language) {
        calendarPanel.removeAll();

        // Get current month and year
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();
        String monthName = currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag(language));

        monthLabel.setText(monthName + " " + currentYear);

        // Add weekdays
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String day : days) {
            calendarPanel.add(new JLabel(translations.getOrDefault(day, day), SwingConstants.CENTER));
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

    private static JButton createDayButton(int day) {
        JButton dayButton = new JButton(String.valueOf(day));
        // dayButton.setBorder(new LineBorder(Color.WHITE, 2, true)); // Rounded corners
        if (currentDate.getYear() == LocalDate.now().getYear() && currentDate.getMonth() == LocalDate.now().getMonth() && day == LocalDate.now().getDayOfMonth()) {
            //dayButton.setBackground(Color.LIGHT_GRAY);
            dayButton.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
        }
        return dayButton;
    }

    private static Map<String, String> loadTranslations(String filePath) {
        Map<String, String> translations = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            translations = lines.stream()
                    .map(line -> line.split("="))
                    .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load translations", e);
        }
        return translations;
    }
}