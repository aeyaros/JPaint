package com.jpaint;

/* * * * * * * * *\
 * Andrew Yaros  *
 *    CS 338     *
 *    JPaint     *
\* * * * * * * * */

public class Main {
    static boolean IS_MAC;
    public static void main(String[] args) {
        String lcOSName = System.getProperty("os.name").toLowerCase();
        IS_MAC = lcOSName.startsWith("mac os x");

        ApplicationWindow window = new ApplicationWindow();
        window.WindowSetup(640,480);
    }
}
