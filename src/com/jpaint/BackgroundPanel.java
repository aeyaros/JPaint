package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


public class BackgroundPanel extends JPanel {
    private final int squareSize = 8;

    private Color[] squareColors = {
            new Color(255,255,255,255),
            new Color(255,200,200,200)
    };

    BackgroundPanel() {
        super();
        this.setBorder(null);
        this.setLayout(new GridBagLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //draw a checkerboard at a given size
        boolean startingColumnColor = false; //alternate starting color every 8 rows
        boolean use1; //alternate color used every 8 columns
        for(int i = 0; i < this.getWidth(); i++) {
            if(i % squareSize == 0) startingColumnColor = !startingColumnColor;
            use1 = startingColumnColor; //set use1 to starting color
            for (int j = 0; j < this.getHeight(); j++) {
                if(j % squareSize == 0) use1 = !use1;
                if(use1) g.setColor(squareColors[1].getAWT());
                else g.setColor(squareColors[0].getAWT());
                g.fillRect(i, j, squareSize, squareSize);
            }
        }
    }

    //sometimes I need to use a fixed-size jlabel
    static ImageIcon generateTileBG(int w, int h) {
        final int squareSize = 8;

        int[] squareColors = {
            new Color(255,255,255,255).getARGB(),
            new Color(255,200,200,200).getARGB()
        };

        BufferedImage tiled = new BufferedImage(w,h ,BufferedImage.TYPE_INT_ARGB);

        //draw a checkerboard at a given size
        boolean startingColumnColor = false; //alternate starting color every 8 rows
        boolean use1; //alternate color used every 8 columns
        for(int i = 0; i < w; i++) {
            if(i % squareSize == 0) startingColumnColor = !startingColumnColor;
            use1 = startingColumnColor; //set use1 to starting color
            for (int j = 0; j < h; j++) {
                if(j % squareSize == 0) use1 = !use1;
                if(use1) tiled.setRGB(i,j,squareColors[1]);
                else tiled.setRGB(i,j,squareColors[0]);
            }
        } return new ImageIcon(tiled);
    }
}
