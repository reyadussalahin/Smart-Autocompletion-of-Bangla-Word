import java.io.*;
import java.net.URL;
import java.awt.*;

/**
 * Created by Reyad on 8/6/2018.
 */


public class Main {
    public static void main(String[] args) {
        try {
            Driver driver = new Driver();
            driver.looseControl();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
