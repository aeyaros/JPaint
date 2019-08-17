package com.jpaint;

import javax.swing.*;
import java.awt.image.BufferedImage;

//ColorPreset: a button for choosing a preset color in the UI
class ColorButton extends JButton {
    private Color color;
    private int width;
    private int height;

    ColorButton(Color color, int width, int height) {
        super();
        this.width = width;
        this.height = height;
        this.setOpaque(true);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setBorder(null);
        setColor(color);
    }

    private ImageIcon generateColorIcon() {
        int pixelCount = width * height; //number of pixels in icon
        int[] iconArray = new int[pixelCount]; //create array of pixels
        for(int i = 0; i < pixelCount; i++) iconArray[i] = color.getARGB(); //set colors of pixels
        //create a buffered image and then add the pixels to it
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.setRGB(0,0, width, height, iconArray,0,1);
        return new ImageIcon(bufferedImage); //return imageicon from the bufferedimage
    }

    Color getColor() {
        return color;
    }

    void setColor(Color newColor) {
        color = new Color(newColor);
        this.setIcon(generateColorIcon());
    }
}