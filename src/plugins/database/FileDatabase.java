package plugins.database;

import core.Database;
import entities.Entry;
import entities.enums.Category;
import entities.enums.Priority;
import entities.enums.Status;

import java.io.*;
import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileDatabase implements Database {
    private static final Logger logger = Logger.getLogger(FileDatabase.class.getName());
    private final String path;
    private static int counter = 0;

    public FileDatabase(String path) {
        if (!isValidPath(path)) {
            throw new IllegalArgumentException("Invalid file path: " + path);
        }
        this.path = path;
        initializeCounter();
    }

    private boolean isValidPath(String path) {
        try {
            File file = new File(path);
            if (file.exists() || file.createNewFile()) {
                return file.canRead() && file.canWrite();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
    }

    private void initializeCounter() {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                int id = Integer.parseInt(parts[0]);
                if (id > counter) {
                    counter = id;
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public boolean saveEntry(Entry entry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, true))) {
            writer.write(entryToCSV(entry));
            writer.newLine();
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Entry readEntry(String title) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                Entry entry = csvToEntry(line);
                if (entry.getTitle().equals(title)) {
                    return entry;
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Entry readEntry(int id) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            reader.readLine(); // skip header

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (Integer.parseInt(parts[0]) == id) {
                    return new Entry(parts[1], parts[2], ZonedDateTime.parse(parts[3]), parts[4], Category.valueOf(parts[5]), Priority.valueOf(parts[6]), Status.valueOf(parts[7]), parts[8]);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean deleteEntry(Entry entry) {
        List<Entry> entries = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                Entry e = csvToEntry(line);
                if (!e.getTitle().equals(entry.getTitle())) {
                    entries.add(e);
                } else {
                    found = true;
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }

        if (found) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
                Class<Entry> entryClass = Entry.class;
                Field[] fields = entryClass.getDeclaredFields();
                StringBuilder header = new StringBuilder();
                header.append("ID;");
                for (Field field : fields) {
                    header.append(field.getName()).append(";");
                }
                // Entfernen des letzten Kommas
                if (!header.isEmpty()) {
                    header.setLength(header.length() - 1);
                }

                writer.write(header.toString());
                writer.newLine();
                for (Entry e : entries) {
                    writer.write(entryToCSV(e));
                    writer.newLine();
                }
                return true;
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean updateEntry(Entry entry) {
        List<Entry> entries = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                Entry e = csvToEntry(line);
                if (e.getTitle().equals(entry.getTitle())) {
                    entries.add(entry);
                    found = true;
                } else {
                    entries.add(e);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }

        if (found) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
                Class<Entry> entryClass = Entry.class;
                Field[] fields = entryClass.getDeclaredFields();
                StringBuilder header = new StringBuilder();
                header.append("ID;");
                for (Field field : fields) {
                    header.append(field.getName()).append(";");
                }
                // Entfernen des letzten Kommas
                if (!header.isEmpty()) {
                    header.setLength(header.length() - 1);
                }

                writer.write(header.toString());
                writer.newLine();
                for (Entry e : entries) {
                    writer.write(entryToCSV(e));
                    writer.newLine();
                }
                return true;
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                return false;
            }
        }
        return false;
    }

    @Override
    public Entry[] listEntries() {
        List<Entry> entries = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                entries.add(csvToEntry(line));
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return entries.toArray(new Entry[0]);
    }

    @Override
    public boolean createTables() {
        File file = new File(path);
        if (file.length() == 0) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
                Class<Entry> entryClass = Entry.class;
                Field[] fields = entryClass.getDeclaredFields();
                StringBuilder header = new StringBuilder();
                header.append("ID;");
                for (Field field : fields) {
                    header.append(field.getName()).append(";");
                }
                // Entfernen des letzten Kommas
                if (!header.isEmpty()) {
                    header.setLength(header.length() - 1);
                }

                writer.write(header.toString());
                writer.newLine();
                return true;
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                return false;
            }
        }
        return true;
    }

    private String entryToCSV(Entry entry) {
        counter++;
        return counter + ";" + entry.getTitle() + ";" + entry.getDescription() + ";" + entry.getDateAndTime() + ";" + entry.getLocation() + ";" + entry.getCategory() + ";" + entry.getPriority() + ";" + entry.getStatus() + ";" + entry.getNotes();
    }

    private Entry csvToEntry(String csv) {
        String[] parts = csv.split(";");
        Category category = (parts[5] != null && !parts[5].isEmpty() && !"null".equals(parts[5])) ? Category.valueOf(parts[5]) : null;
        Priority priority = (parts[6] != null && !parts[6].isEmpty() && !"null".equals(parts[6])) ? Priority.valueOf(parts[6]) : null;
        Status status = (parts[7] != null && !parts[7].isEmpty() && !"null".equals(parts[7])) ? Status.valueOf(parts[7]) : null;
        return new Entry(parts[1], parts[2], ZonedDateTime.parse(parts[3]), parts[4], category, priority, status, parts[8]);
    }
}