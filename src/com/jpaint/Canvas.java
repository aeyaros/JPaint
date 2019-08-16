package com.jpaint;

import javax.swing.*;
import java.awt.image.BufferedImage;

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
                pixels[i][j] = new Color(128,0,0,255).getARGB();
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

    Color getColor(int w, int h) {
        return new Color(pixels[w][h]);
    }

    int getWidth() {
        return width;
    }
    int getHeight() {
        return height;
    }

    //export a buffered image for the view
    BufferedImage getImage() {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                //set the current pixel in the buffered image to argb int from the pixels array
                bufferedImage.setRGB(i, j, pixels[i][j]);
            }
        } return bufferedImage;
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
