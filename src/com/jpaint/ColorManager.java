package com.jpaint;

import javax.swing.*;
import java.awt.*;

//manager selected colors
//add this to anything that needs to access a color
//will be used to show current colors
public class ColorManager {
    private Color left;
    private Color middle;
    private Color right;
    private Tool[] tools;
    private Color[] colorPresets;
    private JPanel presetPanel;
    private JPanel selectedColorsPanel;

    ColorManager(Tool[] tools, JPanel presetPanel, Color[] colorPresets, JPanel selectedColorsPanel) {
        if(colorPresets.length < 1) throw new IllegalArgumentException("Must have at least one color.");

        //set current colors
        left = colorPresets[0 % colorPresets.length];
        middle = colorPresets[1 % colorPresets.length];
        right = colorPresets[2 % colorPresets.length];

        this.tools = tools;
        this.colorPresets = colorPresets;
        this.presetPanel = presetPanel;
        this.selectedColorsPanel = selectedColorsPanel;

        //add colors to preset pane;
        for(int i = 0; i < colorPresets.length; i++) {
            presetPanel.add(new ColorPreset(colorPresets[i], ApplicationWindow.COLOR_BUTTON_SIZE, ApplicationWindow.COLOR_BUTTON_SIZE), i);
        }

        //add current colors to selected colors panel
        selectedColorsPanel.add(new ColorPreset(left, ApplicationWindow.COLOR_BUTTON_SIZE*2, ApplicationWindow.SELECTED_COLOR_BUTTON_HEIGHT),0);
        selectedColorsPanel.add(new ColorPreset(middle, ApplicationWindow.COLOR_BUTTON_SIZE/2, ApplicationWindow.SELECTED_COLOR_BUTTON_HEIGHT),1);
        selectedColorsPanel.add(new ColorPreset(right, ApplicationWindow.COLOR_BUTTON_SIZE/2, ApplicationWindow.SELECTED_COLOR_BUTTON_HEIGHT),2);

        notifyTools();
    }

    //when a color is changed, send new colors to observers
    private void notifyTools() {
        for (Tool observer : tools) {
            observer.updateColors(left, middle, right);
        }
    }

    //getters
    public Color getLeft() {
        return left;
    }
    public Color getMiddle() {
        return middle;
    }
    public Color getRight() {
        return right;
    }

    //setters
    public void setLeft(Color leftClickColor) {
        left = leftClickColor;
        notifyTools();
    }

    public void setMiddle(Color middleClickColor) {
        middle = middleClickColor;
        notifyTools();
    }

    public void setRight(Color rightClickColor) {
        right = rightClickColor;
        notifyTools();
    }
}