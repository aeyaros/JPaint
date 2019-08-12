package com.jpaint;

import javax.swing.*;
import java.awt.image.BufferedImage;

//ColorPreset: a button for choosing a preset color in the UI
public class ColorPreset extends JButton {
    private Color _color;

    ColorPreset(Color color) {
        super();
        this.setOpaque(true);
        this.setBorderPainted(false);
        changeColor(color);
    }

    public ImageIcon generateColorIcon() {
        int pixelCount = Main.COLOR_BUTTON_SIZE * Main.COLOR_BUTTON_SIZE;
        int[] iconArray = new int[pixelCount];
        for(int i = 0; i < pixelCount; i++) {
            iconArray[i] = _color.getARGB();
        }
        BufferedImage bufferedImage = new BufferedImage(Main.COLOR_BUTTON_SIZE, Main.COLOR_BUTTON_SIZE, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.setRGB(0,0,Main.COLOR_BUTTON_SIZE, Main.COLOR_BUTTON_SIZE,iconArray,0,1);
        return new ImageIcon(bufferedImage);
    }

    public void changeColor(Color newColor) {
        _color = newColor;
        this.setIcon(generateColorIcon());
        this.setBackground(java.awt.Color.white);//_color.getAWT());
    }
}
