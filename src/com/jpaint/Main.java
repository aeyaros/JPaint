package com.jpaint;

/* * * * * * * * *\
 * Andrew Yaros  *
 *    CS 338     *
 *    JPaint     *
\* * * * * * * * */

public class Main {
    static boolean IS_MAC;
    final static int DEFAULT_WINDOW_WIDTH = 640;
    final static int DEFAULT_WINDOW_HEIGHT = 480;

    public static void main(String[] args) {
        String lcOSName = System.getProperty("os.name").toLowerCase();
        IS_MAC = lcOSName.startsWith("mac os x");

        ApplicationWindow window = new ApplicationWindow();
        window.WindowSetup(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
    }
}
