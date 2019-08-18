package com.jpaint;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

//manage selected colors
class ColorManager {
    //the currently selected colors
    private ColorButton[] selectedColors;

    //the preset colors shown in the panels
    private ColorButton[] presetColors;

    //tools that use the colors
    private Tool[] tools;

    ColorManager(Tool[] tools, JPanel presetPanel, Color[] colors, JPanel selectedColorsPanel) {
        if(colors.length < 1) throw new IllegalArgumentException("Must have at least one color.");
        selectedColors = new ColorButton[3];

        //set current colors and add to the panel showing currently selected colors
        int halfMiddleWidth = 8; //half of the width of the middle button color selector
        selectedColors[0] = new ColorButton(colors[0 % colors.length],
                ApplicationWindow.TOOL_BUTTON_SIZE - halfMiddleWidth, ApplicationWindow.TOOL_BUTTON_SIZE);
        selectedColors[1] = new ColorButton(colors[1 % colors.length],
                halfMiddleWidth + halfMiddleWidth, ApplicationWindow.TOOL_BUTTON_SIZE);
        selectedColors[2] = new ColorButton(colors[2 % colors.length],
                ApplicationWindow.TOOL_BUTTON_SIZE - halfMiddleWidth, ApplicationWindow.TOOL_BUTTON_SIZE);
        selectedColorsPanel.add(selectedColors[0],0);
        selectedColorsPanel.add(selectedColors[1],1);
        selectedColorsPanel.add(selectedColors[2],2);

        //keep track of the tools being used
        this.tools = tools;

        //build the list of color presets from input
        presetColors = new ColorButton[colors.length];
        for(int i = 0; i < presetColors.length; i++) {
            //create the button
            presetColors[i] = new ColorButton(colors[i],
                    ApplicationWindow.COLOR_BUTTON_SIZE, ApplicationWindow.COLOR_BUTTON_SIZE);

            //add event listener to the button
            //when it is clicked, the current color should be set
            presetColors[i].addMouseListener(new PresetColorsListener(i));

            //add the button to the panel
            presetPanel.add(presetColors[i]);
        }

        //notify tools of color changes
        notifyTools();
    }

    //when a color is changed, send new colors to observers
    private void notifyTools() {
        for (Tool observer : tools) {
            observer.updateColors(
                selectedColors[0].getColor(),
                selectedColors[1].getColor(),
                selectedColors[2].getColor()
            );
        }
    }

    //allows me to pass an index value through the action listener
    // so I can use it to set the mouse controllers
    //used for clicking the preset color buttons
    private class PresetColorsListener implements MouseListener {
        private int index;
        PresetColorsListener(int index) {
            this.index = index;
        }

        @Override
        public void mouseClicked(MouseEvent e) { //when a color preset is clicked
            if(e.getClickCount() > 1) { //if double click or more
                //double click; restore previous color to the corresponding button
                setButtonColor(accessButton(e.getButton()).getPreviousColor(), e.getButton());
                //open the window to edit the color
                    //INSERT CODE TO OPEN WINDOW HERE
                    //MAYBE HAVE FLAG FOR IS WINDOW OPEN OR NOT IDK WHATEV LOL ROFL LMAO

                System.out.println("doubleclick");
            } else { //if single click
                //set the current button color based on which button is clicked
                setButtonColor(presetColors[index].getColor(), e.getButton());
                System.out.println("singleclick");
            }
        }
        @Override public void mousePressed(MouseEvent e) { }
        @Override public void mouseReleased(MouseEvent e) { }
        @Override public void mouseEntered(MouseEvent e) { }
        @Override public void mouseExited(MouseEvent e) { }
    }

    //given a mouse event, return the color button indicated by the mouse event
    //either left, middle, or right button returns corresponding ColorButton object
    private ColorButton accessButton(int buttonCode) {
        switch(buttonCode) {
            case MouseEvent.BUTTON2: return selectedColors[1];
            case MouseEvent.BUTTON3: return selectedColors[2];
            default: return selectedColors[0]; //MouseEvent.BUTTON1
        }
    }

    //set a color - updates the tools when color is set
    public void setButtonColor(Color color, int buttonCode) {
        accessButton(buttonCode).setColor(color);
        notifyTools();
    }
}