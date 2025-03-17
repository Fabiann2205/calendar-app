import core.Core;
import plugins.database.FileDatabase;
import plugins.frontend.FrontendMain;

public class Main {
    public static void main(String[] args) {
        // use SQLite as database
        // Core core = new Core(new SQLiteDatabase("jdbc:sqlite:resources/data.db"), new FrontendMain("de"));

        // use file as database
        Core core = new Core(new FileDatabase("resources/filedb"), new FrontendMain("de"));

        System.out.println("test");
        core.addEntry();
        core.removeEntry();
    }
}
