import core.Frontend;
import frontend.FrontendMain;

public class Main {
    public static void main(String[] args) {
        // Start the frontend
        Frontend eins = new FrontendMain();
        eins.initialize("de");
        eins.setLanguage("en");

    }
}
