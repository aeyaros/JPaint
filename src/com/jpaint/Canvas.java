package com.jpaint;

//Canvas: part of the model: contains an argb-integer image
class Canvas {
    private int[][] pixels; //the image
    private int width;
    private int height;

    /*====== CONSTRUCTORS ======*/

    //create a new canvas
    Canvas(int w, int h) {
        width = w;
        height = h;
        pixels = new int[width][height];
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                pixels[i][j] = new Color(255,255,255,255).getARGB();
            }
        }
    }

    //deep copy constructor
    Canvas(Canvas oldCanvas) {
        width = oldCanvas.getWidth();
        height = oldCanvas.getHeight();
        pixels = new int[width][height];
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                pixels[i][j] = oldCanvas.getPixel(i,j);
            }
        }
    }

    /*====== ACCESSORS ======*/
    int getPixel(int w, int h) {
        return pixels[w][h];
    }

    int[][] getPixels() {
        return pixels;
    }

    Color getColor(int w, int h) {
        return new Color(pixels[w][h]);
    }

    int getWidth() {
        return width;
    }
    int getHeight() {
        return height;
    }

    /*====== MODIFIERS ======*/
    void setPixel(int x, int y, int color) {
        try {
            pixels[x][y] = color;
        } catch (Exception e) {
            //System.out.println(e.getStackTrace());
        }
    }
}
