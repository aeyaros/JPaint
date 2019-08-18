package com.jpaint;

import javax.swing.*;
import java.awt.image.BufferedImage;

//ColorPreset: a button for choosing a preset color in the UI
class ColorButton extends JButton {
    private Color color;
    private int width;
    private int height;

    private Color previousColor;

    ColorButton(Color color, int width, int height) {
        super();
        this.width = width;
        this.height = height;
        this.setOpaque(true);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setBorder(null);
        this.color = color; //required so previous color can be initialized in setColor()
        setColor(color);
    }

    static ImageIcon generateColorIcon(int w, int h, int c) {
        int pixelCount = w * h; //number of pixels in icon
        int[] iconArray = new int[pixelCount]; //create array of pixels
        for(int i = 0; i < pixelCount; i++) iconArray[i] = c; //set colors of pixels
        //create a buffered image and then add the pixels to it
        BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.setRGB(0,0, w, h, iconArray,0,1);
        return new ImageIcon(bufferedImage); //return imageicon from the bufferedimage
    }

    Color getColor() {
        return color;
    }

    void setColor(Color newColor) {
        previousColor = new Color(color);
        color = new Color(newColor);
        this.setIcon(generateColorIcon(width, height, color.getARGB()));
    }

    Color getPreviousColor() {
        return previousColor;
    }
}