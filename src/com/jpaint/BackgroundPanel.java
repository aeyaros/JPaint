package com.jpaint;

import javax.swing.*;

public class BackgroundPanel extends JLabel {
    private int w;
    private int h;
    private final int squareSize = 8;

    Color[] squareColors = {
            new Color(255,255,255,255),
            new Color(255,200,200,200)
    };

    BackgroundPanel(int w, int h) {
        super();
        this.w = w;
        this.h = h;
        this.setSize(w,h);
    }

    /*@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //draw a checkerboard at a given size
        boolean use1 = false;
        for(int i = 0; i < w; i+= squareSize) {
            //every 8 pixels along the row, new color
            if (i % squareSize == 0) use1 = !use1;
            for (int j = 0; j < h; j+= squareSize) {
                //every 8 pixels in the column, new color
                if (j % squareSize == 0) use1 = !use1;
                if (use1) {
                    g.setColor(squareColors[1].getAWT());
                    g.fillRect(i, j, squareSize, squareSize);
                } else {
                    g.setColor(squareColors[0].getAWT());
                    g.fillRect(i, j, squareSize, squareSize);
                }
            }
        }
    }*/

    //taken from imagemodel and put here temporarily:

    //update the tile BG if image is resized
    private void updateTileBG() {
        //tileBG = generateTileBG(currentState.getWidth(), currentState.getHeight());
    }

    //draw a checkerboard at a given size
    private int[][] generateTileBG(int w, int h) {
        int[][] matrix = new int[w][h];
        int[] squareColors = {
                this.squareColors[0].getARGB(),
                this.squareColors[1].getARGB()
        }; boolean use1 = false;
        for(int i = 0; i < w; i++) {
            //every 8 pixels along the row, new color
            if (i % squareSize == 0) use1 = !use1;
            for (int j = 0; j < h; j++) {
                //every 8 pixels in the column, new color
                if (j % squareSize == 0) use1 = !use1;
                if (use1) matrix[i][j] = squareColors[1];
                else matrix[i][j] = squareColors[0];
            }
        } return matrix;
    }
}
