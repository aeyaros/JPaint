package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

//ColorPreset: is used as a label to show a color
//used as a button by the sidebar presets
//used by the color picker as a larger label
class ColorButton extends JLabel {
    private Color color;
    private Color previousColor;
    private int width;
    private int height;

    ColorButton(Color color, int width, int height) {
        super();
        this.width = width;
        this.height = height;
        this.setOpaque(true);
        this.setLayout(new GridBagLayout());
        this.color = color; //required so previous color can be initialized in setColor()
        setColor(color);
        resetBorder();
    }

    void resetBorder() {
        this.setBorder(BorderFactory.createLoweredBevelBorder());
    }

    private ImageIcon generateColorIcon(int w, int h, int c) {
        BufferedImage bufferedImage = Canvas.generateTileBG(w, h);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                bufferedImage.setRGB(i, j, Color.alphaBlend(c, bufferedImage.getRGB(i, j)));
            }
        }
        return new ImageIcon(bufferedImage);
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