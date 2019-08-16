package com.jpaint;

import javax.swing.*;
import java.awt.image.BufferedImage;

//ColorPreset: a button for choosing a preset color in the UI
class ColorPreset extends JButton {
    private Color color;
    private int width;
    private int height;

    ColorPreset(Color color, int width, int height) {
        super();
        this.width = width;
        this.height = height;
        this.setOpaque(true);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setBorder(null);
        changeColor(color);
    }

    private ImageIcon generateColorIcon() {
        int pixelCount = width * height;
        int[] iconArray = new int[pixelCount];
        for(int i = 0; i < pixelCount; i++) {
            iconArray[i] = color.getARGB();
        }
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.setRGB(0,0, width, height,iconArray,0,1);
        return new ImageIcon(bufferedImage);
    }

    void changeColor(Color newColor) {
        color = newColor;
        this.setIcon(generateColorIcon());
    }
}