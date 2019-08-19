package com.jpaint;

import javax.swing.*;
import java.awt.*;

public class ToolEraser extends ToolPaintBrush {
    private int colorToEraseTo;


    ToolEraser(String name, ImageModel model, String iconSource, String selectedIconSource) {
        super(name, model, iconSource, selectedIconSource);
        //set up upper card
        upperCard.removeAll();
        upperCard.setLayout(new CardLayout());
        JLabel infoText = new JLabel("Click and drag to erase.");
        infoText.setHorizontalAlignment(SwingConstants.CENTER);
        infoText.setVerticalAlignment(SwingConstants.CENTER);
        upperCard.add(infoText,0);

        setColorToEraseTo(new Color(Color.MAX_VALUE,Color.MAX_VALUE,Color.MAX_VALUE,Color.MAX_VALUE));
    }

    void setColorToEraseTo(Color color) {
        colorToEraseTo = color.getARGB();
    }

    @Override
    protected void draw(int x, int y, int color) {
        makeCircle(x,y,colorToEraseTo);
    }
}
