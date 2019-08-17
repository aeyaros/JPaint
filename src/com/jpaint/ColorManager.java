package com.jpaint;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

//manage selected colors
class ColorManager {
    //the currently selected colors
    private ColorButton left;
    private ColorButton middle;
    private ColorButton right;

    //the preset colors shown in the panels
    private ColorButton[] colorButtons;

    //tools that use the colors
    private Tool[] tools;

    ColorManager(Tool[] tools, JPanel presetPanel, Color[] colors, JPanel selectedColorsPanel) {
        if(colors.length < 1) throw new IllegalArgumentException("Must have at least one color.");

        //set current colors and add to the panel showing currently selected colors
        int halfMiddleWidth = 8; //half of the width of the middle button color selector
        left = new ColorButton(colors[0 % colors.length],ApplicationWindow.TOOL_BUTTON_SIZE - halfMiddleWidth, ApplicationWindow.TOOL_BUTTON_SIZE);
        middle = new ColorButton(colors[1 % colors.length], halfMiddleWidth + halfMiddleWidth, ApplicationWindow.TOOL_BUTTON_SIZE);
        right = new ColorButton(colors[2 % colors.length],ApplicationWindow.TOOL_BUTTON_SIZE - halfMiddleWidth, ApplicationWindow.TOOL_BUTTON_SIZE);
        selectedColorsPanel.add(left,0);
        selectedColorsPanel.add(middle,1);
        selectedColorsPanel.add(right,2);

        //keep track of the tools being used
        this.tools = tools;

        //build the list of color presets from input
        colorButtons = new ColorButton[colors.length];
        for(int i = 0; i < colorButtons.length; i++) {
            //create the button
            colorButtons[i] = new ColorButton(colors[i], ApplicationWindow.COLOR_BUTTON_SIZE, ApplicationWindow.COLOR_BUTTON_SIZE);

            //add event listener to the button
            //when it is clicked, the current color should be set
            colorButtons[i].addMouseListener(new ColorSelectListener(i));

            //add the button to the panel
            presetPanel.add(colorButtons[i]);
        }

        //notify tools of color changes
        notifyTools();
    }

    //when a color is changed, send new colors to observers
    private void notifyTools() {
        for (Tool observer : tools) {
            observer.updateColors(left.getColor(), middle.getColor(), right.getColor());
        }
    }

    //allows me to pass an index value through the action listener so I can use it to set the mouse controllers
    private class ColorSelectListener implements MouseListener {
        private int index;
        ColorSelectListener(int index) {
            this.index = index;
        }

        @Override
        public void mouseClicked(MouseEvent e) { //when a color preset is clicked
            if(e.getClickCount() > 1) { //if double click or more
                //open the window to edit the color
            } else { //if single click
                //set the current color based on which button is clicked
                setButtonColor(colorButtons[index].getColor(), e);
            }
        }
        @Override public void mousePressed(MouseEvent e) { }
        @Override public void mouseReleased(MouseEvent e) { }
        @Override public void mouseEntered(MouseEvent e) { }
        @Override public void mouseExited(MouseEvent e) { }
    }

    //set a color
    private void setButtonColor(Color color, MouseEvent e) {
        switch(e.getButton()) {
            case MouseEvent.BUTTON1: left.setColor(color); break;
            case MouseEvent.BUTTON2: middle.setColor(color); break;
            case MouseEvent.BUTTON3: right.setColor(color); break;
            default: break;
        } notifyTools();
    }
}