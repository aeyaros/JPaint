package com.jpaint;

import javax.swing.*;
import java.awt.image.BufferedImage;

//Canvas: part of the model: contains an argb-integer image
public class Canvas {
    private int[][] _pixels; //the image
    private int _width;
    private int _height;

    /*====== CONSTRUCTORS ======*/

    //create a new canvas
    Canvas(int w, int h) {
        _width = w;
        _height = h;
        _pixels = new int[_width][_height];
        for(int i = 0; i < _width; i++) {
            for(int j = 0; j < _height; j++) {
                _pixels[i][j] = new Color(255,255,255,255).getARGB();
            }
        }
    }

    //deep copy constructor
    Canvas(Canvas oldCanvas) {
        _width = oldCanvas.getWidth();
        _height = oldCanvas.getHeight();
        _pixels = new int[_width][_height];
        for(int i = 0; i < _width; i++) {
            for(int j = 0; j < _height; j++) {
                _pixels[i][j] = oldCanvas.getPixel(i,j);
            }
        }
    }

    /*====== ACCESSORS ======*/
    int getPixel(int w, int h) {
        return _pixels[w][h];
    }

    Color getColor(int w, int h) {
        return new Color(_pixels[w][h]);
    }

    int getWidth() {
        return _width;
    }

    int getHeight() {
        return _height;
    }

    //export a buffered image for the view
    ImageIcon getImage() {
        BufferedImage bufferedImage = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < _width; i++) {
            for(int j = 0; j < _height; j++) {
                //set the current pixel in the buffered image to argb int from the pixels array
                bufferedImage.setRGB(i, j, _pixels[i][j]);
            }
        } return new ImageIcon(bufferedImage);
    }

    /*====== MODIFIERS ======*/
    public void setPixel(int x, int y, int color) {
        try {
            _pixels[x][y] = color;
        } catch (Exception e) {
            //System.out.println(e.getStackTrace());
        }
    }
    //for performance reasons I really shouldn't use this
    //should just convert to argb in the image model
    /*public void setPixel(int x, int y, Color color) {
        try {
            _pixels[x][y] = color.getARGB();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }*/

}
