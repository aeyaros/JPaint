package com.jpaint;

import java.awt.image.BufferedImage;

//Canvas: part of the model: contains an argb-integer image
class Canvas {
    private BufferedImage pixels;
    private int width;
    private int height;

    /*====== CONSTRUCTORS ======*/
    final int defaultColor = (new Color(Color.MAX_VALUE,Color.MAX_VALUE,Color.MAX_VALUE,Color.MAX_VALUE)).getARGB();

    //create a new canvas
    Canvas(int w, int h) {
        width = w;
        height = h;
        pixels = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                pixels.setRGB(i,j,defaultColor);
            }
        }
    }

    //deep copy constructor
    Canvas(Canvas oldCanvas) {
        width = oldCanvas.getWidth();
        height = oldCanvas.getHeight();
        pixels = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                pixels.setRGB(i,j,oldCanvas.pixels.getRGB(i,j));
            }
        }
    }

    /*====== MODIFIERS ======*/
    void setPixel(int x, int y, int color) {
        try { pixels.setRGB(x,y,Color.alphaBlend(color, pixels.getRGB(x,y))); }
        catch (Exception e) {}
    }

    /*====== ACCESSORS ======*/
    BufferedImage getPixels() {
        return pixels;
    }
    int getWidth() {
        return width;
    }
    int getHeight() {
        return height;
    }

    int getPixel(int w, int h) {
        return pixels.getRGB(w,h);
    }

    Color getColor(int w, int h) {
        return new Color(pixels.getRGB(w,h));
    }
}
