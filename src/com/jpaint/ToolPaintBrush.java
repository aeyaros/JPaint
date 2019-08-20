package com.jpaint;

import javax.swing.*;
import java.awt.*;

public class ToolPaintBrush extends ToolPencil {
    private final int MIN_RADIUS = 2;
    private final int DEFAULT_RADIUS = 8;
    private final int MAX_RADIUS = 24;
    private JLabel radiusLabel;
    private JSlider radiusSlider;

    int radius;
    int negrad;

    ToolPaintBrush(String name, ImageModel model, String iconSource, String selectedIconSource) {
        super(name, model, iconSource, selectedIconSource);
        //set up upper card
        upperCard.removeAll();
        upperCard.setLayout(new GridLayout(1,0));

        radiusLabel = new JLabel("", SwingConstants.RIGHT);
        radiusSlider = new JSlider(SwingConstants.HORIZONTAL,MIN_RADIUS,MAX_RADIUS,DEFAULT_RADIUS);
        radiusSlider.addChangeListener(e -> setRadius(radiusSlider.getValue()));

        upperCard.add(radiusLabel);
        upperCard.add(radiusSlider);

        setRadius(DEFAULT_RADIUS);
    }

    void setRadius(int r) {
        radius = r;
        negrad = -r;
        radiusLabel.setText("Radius: " + radius);
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