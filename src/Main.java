import core.Frontend;
import plugins.frontend.FrontendMain;

public class Main {
    public static void main(String[] args) {
        // Start the plugins.frontend
        Frontend eins = new FrontendMain();
        eins.initialize("de");
        // eins.setLanguage("en");
    }
}
