package com.jpaint;

/* * * * * * * * *\
 * Andrew Yaros  *
 *    CS 338     *
 *    JPaint     *
\* * * * * * * * */

class Main {
final static int DEFAULT_WINDOW_WIDTH = 640;
final static int DEFAULT_WINDOW_HEIGHT = 480;
static boolean IS_MAC;

public static void main(String[] args) {
	String lcOSName = System.getProperty("os.name").toLowerCase();
	IS_MAC = lcOSName.startsWith("mac os x");
	
	WindowApplication window = new WindowApplication();
	window.WindowSetup(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
}
}
