package com.jpaint;

import javax.swing.*;
import java.awt.*;

public class ToolEraser extends ToolPaintBrush {
    private boolean eraseToTransparent;

    ToolEraser(String name, ImageModel model, String iconSource, String selectedIconSource) {
        super(name, model, iconSource, selectedIconSource);
        //set up upper card
        upperCard.removeAll();
        upperCard.setLayout(new CardLayout());
        JLabel infoText = new JLabel("Click and drag to erase.");
        infoText.setHorizontalAlignment(SwingConstants.CENTER);
        infoText.setVerticalAlignment(SwingConstants.CENTER);
        upperCard.add(infoText,0);

        //set up as a normal eraser
        setEraseToTransparent(false);
    }

    private void setEraseToTransparent(boolean eraseToTransparent) {
        this.eraseToTransparent = eraseToTransparent;
    }

    //change how the eraser erases
    private int getEraserColor() {
        int value;
        if(eraseToTransparent) value = Color.MIN_VALUE; //erase to transparent
        else value = Color.MAX_VALUE; //erase to white
        return new Color(value,value,value,value).getARGB();
    }

    @Override
    protected void draw(int x, int y, int color) {
        eraseCircle(x,y,getEraserColor());
    }

    private void eraseCircle(int origX, int origY, int color) {
        for(int y = negrad; y <= radius; y++) {
            for(int x = negrad; x <= radius; x++) {
                if (x * x + y * y <= radius * radius) //draw if inside bounds of circle
                    model.setPixelWithoutBlending(origX + x, origY + y, color);
            }
        } model.refresh();
    }
}
