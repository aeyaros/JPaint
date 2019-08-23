package com.jpaint;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

//Canvas: part of the model: contains an argb-integer image
class Canvas {
    private BufferedImage pixels;
    private int width;
    private int height;

    /*====== CONSTRUCTORS ======*/
    private final int whiteInt = (new Color(Color.MAX_VALUE,Color.MAX_VALUE,Color.MAX_VALUE,Color.MAX_VALUE)).getARGB();
    private final int transparentInt = (new Color(Color.MIN_VALUE,Color.MIN_VALUE,Color.MIN_VALUE,Color.MIN_VALUE)).getARGB();

    private int defaultColor; //either white or transparent

    //create a new canvas
    Canvas(int w, int h, boolean transparent) {
        if(transparent) defaultColor = transparentInt;
        else defaultColor = whiteInt;

        width = w;
        height = h;
        pixels = newBlankImage(w,h, defaultColor);
    }

    //deep copy constructor
    Canvas(Canvas oldCanvas) {
        defaultColor = transparentInt;
        width = oldCanvas.getWidth();
        height = oldCanvas.getHeight();
        pixels = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                pixels.setRGB(i,j,oldCanvas.pixels.getRGB(i,j));
            }
        }
    }

    //canvas from a bufferedImage
    Canvas(BufferedImage sourceImage) {
        defaultColor = transparentInt;
        width = sourceImage.getWidth();
        height = sourceImage.getHeight();
        pixels = sourceImage;
    }

    private BufferedImage newBlankImage(int w, int h, int baseColor) {
        BufferedImage newImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < w; i++) {
            for(int j = 0; j < h; j++) {
                newImage.setRGB(i,j, baseColor);
            }
        } return newImage;
    }

    void clear() {
        pixels = newBlankImage(width, height, defaultColor);
    }

    /*====== MODIFIERS ======*/
    void setPixel(int x, int y, int color) {
        try { pixels.setRGB(x,y,Color.alphaBlend(color, pixels.getRGB(x,y))); }
        catch (Exception ignored) {}
    }

    void setPixelWithoutBlending(int x, int y, int exactColor) {
        try { pixels.setRGB(x,y,exactColor); }
        catch (Exception ignored) {}
    }

    //currently resizes by adding or removing from origin
    //no fancy centered-resizing yet
    void resize(int newX, int newY) {
        if(newX <= 0 || newY <= 0) throw new IllegalArgumentException("Canvas must be greater than 0 in both dimensions.");

        //draw a canvas that is newX by newY
        BufferedImage newPixels = newBlankImage(newX, newY, defaultColor);

        //and then copy the current canvas on top of the new one
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                //try to write to the pixel on the new canvas at that coordinate
                //ignore exceptions when we are out of bounds
                try { newPixels.setRGB(i, j, pixels.getRGB(i, j)); }
                catch (Exception ignored) {}
            }
        }

        //change values to the new data
        pixels = newPixels;
        width = newX;
        height = newY;
    }

    /*====== ACCESSORS ======*/
    BufferedImage getPixels() {
        return pixels;
    }
    int getWidth() {
        return width;
    }
    int getHeight() {
        return height;
    }

    //overlay an image ontop of the canvas, with blending
    //assuming they are the same size
    void overlayImage(BufferedImage top) {
        int w = top.getWidth(); int h = top.getHeight();
        if(w != this.getWidth() || h != this.getHeight()) {
            throw new IllegalArgumentException("Cannot overlay canvases of different sizes");
        }

        //BufferedImage output = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < w; i++)
            for(int j = 0; j < h; j++)
                this.pixels.setRGB(i,j, Color.alphaBlend(top.getRGB(i,j), this.pixels.getRGB(i,j)));
    }

    void rotateOrtho(int option) {
        BufferedImageOp rotateLeft = new AffineTransformOp(AffineTransform.getRotateInstance((Math.PI) / 2, (double)width/2, (double)height/2),AffineTransformOp.TYPE_BICUBIC);
        BufferedImageOp rotate180 = new AffineTransformOp(AffineTransform.getRotateInstance(Math.PI, (double)width/2, (double)height/2),AffineTransformOp.TYPE_BICUBIC);
        BufferedImageOp rotateRight = new AffineTransformOp(AffineTransform.getRotateInstance((3 * Math.PI) / 2, (double)width/2, (double)height/2),AffineTransformOp.TYPE_BICUBIC);
        BufferedImageOp selectedOp;

        switch (option) {
            case 0 :selectedOp = rotateLeft; break;
            case 1: selectedOp = rotateRight; break;
            default: selectedOp = rotate180; break;
        }

        System.out.println(pixels.getWidth() + " " + pixels.getHeight());
        BufferedImage newPixels = selectedOp.createCompatibleDestImage(pixels, pixels.getColorModel());
        newPixels = selectedOp.filter(pixels, newPixels);
        pixels = newPixels;
    }

    void flip(int option) {
        BufferedImageOp flipHorizontal = new AffineTransformOp(AffineTransform.getScaleInstance(-1.0,1.0),AffineTransformOp.TYPE_BICUBIC);
        BufferedImageOp flipVertical = new AffineTransformOp(AffineTransform.getScaleInstance(1.0,-1.0),AffineTransformOp.TYPE_BICUBIC);
        BufferedImageOp selectedOp;

        if (option == 0) selectedOp = flipHorizontal;
        else selectedOp = flipVertical;

        BufferedImage newPixels = newBlankImage(width, height, defaultColor);
                //selectedOp.createCompatibleDestImage(pixels, pixels.getColorModel());
        System.out.println(newPixels.getWidth());
        newPixels = selectedOp.filter(pixels, newPixels);
        pixels = newPixels;
    }

    //used for generating tiled backgrounds for translucent color
    static BufferedImage generateTileBG(int w, int h) {
        final int squareSize = 4;

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
        } return tiled;
    }
}

