import core.Core;
import plugins.database.FileDatabase;
import plugins.frontend.FrontendMain;

public class Main {
    public static void main(String[] args) {
        Core core = new Core(new FileDatabase("data.txt"), new FrontendMain("de"));
        System.out.println("test");
    }
}
