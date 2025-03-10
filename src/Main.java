import core.Core;
import plugins.database.FileDatabase;
import plugins.frontend.FrontendMain;

public class Main {
    public static void main(String[] args) {
        // use Textfile as database
        Core core = new Core(new FileDatabase("resources/data.csv"), new FrontendMain("de"));

        // use SQLite as database
        //Core core = new Core(new SQLiteDatabase("jdbc:sqlite:resources/data.db"), new FrontendMain("de"));

        System.out.println("test");
        core.addEntry();
    }
}
