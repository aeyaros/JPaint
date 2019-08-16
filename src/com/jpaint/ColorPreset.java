package com.jpaint;

import javax.swing.*;
import java.awt.image.BufferedImage;

//ColorPreset: a button for choosing a preset color in the UI
class ColorPreset extends JButton {
    private Color color;

    ColorPreset(Color color) {
        super();
        this.setOpaque(true);
        this.setBorderPainted(false);
        changeColor(color);
    }

    private ImageIcon generateColorIcon() {
        int pixelCount = WindowSetup.COLOR_BUTTON_SIZE * WindowSetup.COLOR_BUTTON_SIZE;
        int[] iconArray = new int[pixelCount];
        for(int i = 0; i < pixelCount; i++) {
            iconArray[i] = color.getARGB();
        }
        BufferedImage bufferedImage = new BufferedImage(WindowSetup.COLOR_BUTTON_SIZE, WindowSetup.COLOR_BUTTON_SIZE, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.setRGB(0,0,WindowSetup.COLOR_BUTTON_SIZE, WindowSetup.COLOR_BUTTON_SIZE,iconArray,0,1);
        return new ImageIcon(bufferedImage);
    }

    void changeColor(Color newColor) {
        color = newColor;
        this.setIcon(generateColorIcon());
    }
}