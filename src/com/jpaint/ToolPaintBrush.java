package com.jpaint;

import javax.swing.*;
import java.awt.*;

public class ToolPaintBrush extends ToolPencil {
    int radius;
    int negrad;

    ToolPaintBrush(String name, ImageModel model, String iconSource, String selectedIconSource) {
        super(name, model, iconSource, selectedIconSource);
        //set up upper card
        upperCard.removeAll();
        upperCard.setLayout(new CardLayout());
        JLabel infoText = new JLabel("Click and drag to paint.");
        infoText.setHorizontalAlignment(SwingConstants.CENTER);
        infoText.setVerticalAlignment(SwingConstants.CENTER);
        upperCard.add(infoText,0);

        pastY = -1;
        pastX = -1;

        setRadius(7);
    }

    void setRadius(int r) {
        radius = r;
        negrad = -r;
    }

    @Override
    protected void draw(int x, int y, int color) {
        makeCircle(x,y,color);
    }

    protected void makeCircle(int origX, int origY, int color) {
        for(int y = negrad; y <= radius; y++) {
            for(int x = negrad; x <= radius; x++) {
                if (x * x + y * y <= radius * radius) //draw if inside bounds of circle
                    model.setPixel(origX + x, origY + y, color);
            }
        } model.refresh();
    }
}