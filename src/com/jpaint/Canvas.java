package com.jpaint;

import java.awt.image.BufferedImage;

//Canvas: part of the model: contains an argb-integer image
class Canvas {
    private BufferedImage bufPixels;
    //int[][] pixels; //the image
    private int width;
    private int height;

    /*====== CONSTRUCTORS ======*/
    final int defaultColor = (new Color(255,255,255,255)).getARGB();

    //create a new canvas
    Canvas(int w, int h) {
        width = w;
        height = h;
       //pixels = new int[width][height];

        bufPixels = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);

        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
            //    pixels[i][j] = defaultColor;

                bufPixels.setRGB(i,j,defaultColor);

            }
        }



    }

    //deep copy constructor
    Canvas(Canvas oldCanvas) {
        width = oldCanvas.getWidth();
        height = oldCanvas.getHeight();
     //   pixels = new int[width][height];

        bufPixels = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);


        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
            //    pixels[i][j] = oldCanvas.getPixel(i,j);

                bufPixels.setRGB(i,j,oldCanvas.bufPixels.getRGB(i,j));
            }
        }

        //bufPixels.setRGB(0,0,width,height,
          //      oldCanvas.getBufPixels().getRGB(0,0,width,height,null,0,1),
            //    0,1);
    }

    /*====== ACCESSORS ======*/
    int getPixel(int w, int h) {
        //return pixels[w][h];
        return bufPixels.getRGB(w,h);
    }

    /*int[][] getPixels() {
        return pixels;
    }*/

    BufferedImage getBufPixels() {
        return bufPixels;
    }

    Color getColor(int w, int h) {
        //return new Color(pixels[w][h]);
        return new Color(bufPixels.getRGB(w,h));
    }

    int getWidth() {
        return width;
    }
    int getHeight() {
        return height;
    }

    /*====== MODIFIERS ======*/
    /*void setPixel(int x, int y, int color)  {//}, boolean blend) {
        try {
             pixels[x][y] = Color.alphaBlend(color, pixels[x][y]);
            //else pixels[x][y] = color;
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }*/


}
