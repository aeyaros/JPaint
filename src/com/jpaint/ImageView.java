package com.jpaint;

import javax.swing.*;
import java.awt.image.BufferedImage;

//ImageView: THIS IS THE MAIN VIEW CLASS
class ImageView extends JLabel {
    //private ImageIcon _icon;

    ImageView() {
        super();
        //set label background to opaque white
        this.setOpaque(true);
        this.setBackground(java.awt.Color.white);
    }

    //update the view
    void refresh(BufferedImage bufferedImage) {
        //_icon = icon;
        this.setIcon(blendScreens(bufferedImage,
                tileBackground(bufferedImage.getWidth(), bufferedImage.getHeight()))); //was _icon
    }

    ImageIcon blendScreens(BufferedImage top, BufferedImage bottom) {
        if(top.getWidth() == bottom.getWidth() && top.getHeight() == bottom.getHeight()) {
            BufferedImage output = new BufferedImage(top.getWidth(), top.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for(int i = 0; i < top.getWidth(); i++) {
                for(int j = 0; j < top.getHeight(); j++) {
                    output.setRGB(i, j, Color.alphaBlend(
                            new Color(top.getRGB(i, j)),
                            new Color(bottom.getRGB(i, j))
                    ).getARGB());
                }
            }

            return new ImageIcon(output);
        } else throw new IllegalArgumentException("screens must be same size");
    }

    //draw a checkerboard at a given size
    BufferedImage tileBackground(int w, int h) {
        //pixel matrix
        int[][] matrix = new int[w][h];
        //square colors
        int[] squareColors = {
                new Color(255,255,255,255).getARGB(),
                new Color(255,200,200,200).getARGB()
        };
        int curColor = squareColors[0];
        //size of each square
        int squareSize = 8;
        for(int i = 0; i < w; i++) {
            if(i % (squareSize*2) < squareSize) {
                curColor = squareColors[0];
            } else curColor = squareColors[1];

            for(int j = 0; j < h; j++) {
                matrix[i][j] = curColor;
            }
        }

        BufferedImage bufImg = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < w; i++) {
            for(int j = 0; j < h; j++) {
                bufImg.setRGB(i,j,matrix[i][j]);
            }
        }
        return bufImg;
    }
}
