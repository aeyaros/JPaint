package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

//ColorPreset: is used as a label to show a color
//used as a button by the sidebar presets
//used by the color picker as a larger label
class ColorButton extends JLabel {
    private Color color;
    private JLabel bg;
    private JLabel colorLabel;
    private int width;
    private int height;

    private Color previousColor;

    ColorButton(Color color, int width, int height) {
        super();
        this.width = width;
        this.height = height;
        this.setOpaque(true);
        this.setLayout(new GridBagLayout());
        this.color = color; //required so previous color can be initialized in setColor()

        colorLabel = new JLabel();
        this.add(colorLabel);

        this.setIcon(BackgroundPanel.generateTileBG(width,height));
        setColor(color);

        this.setBorder(BorderFactory.createLoweredBevelBorder());
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
        colorLabel.setIcon(generateColorIcon(width, height, color.getARGB()));
    }

    Color getPreviousColor() {
        return previousColor;
    }
}