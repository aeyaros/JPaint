package com.jpaint;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

//Tool: This is a tool button - subclasses contain specific tool functionality
public abstract class Tool implements ToolInput {
    protected ImageModel model; //the model class this tool acts on
    protected JPanel upperCard; //the upper panel shown when this tool is selected
    JButton button; //the tool button
    private String name;
    private int[] colorsInts;

    //set button name and add the model
    Tool(String name, ImageModel model, String iconSource) {
        super();
        this.name = name;
        this.model = model;
        colorsInts = new int[3];

        //panel with top controls - initialize
        //you can do whatever you want with this in the derived tool classes
        upperCard = new JPanel();

        //button for toolbar
        button = new JButton();
        button.setOpaque(true);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setBorder(null);
        button.setSize(ApplicationWindow.TOOL_BUTTON_SIZE, ApplicationWindow.TOOL_BUTTON_SIZE);

        try { //get icon of button
            Image image = ImageIO.read(getClass().getResource(iconSource));
            image = image.getScaledInstance(ApplicationWindow.TOOL_BUTTON_SIZE, ApplicationWindow.TOOL_BUTTON_SIZE, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(image));
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Couldn't load icon for " + name + " button from location " + iconSource);
        }
    }

    void updateColors(Color left, Color middle, Color right) {
        colorsInts[0] = left.getARGB();
        colorsInts[1] = middle.getARGB();
        colorsInts[2] = right.getARGB();
    }

    String getName() {
        return name;
    }

    protected int getColorIntByButton(int mouseEventButtonCode) {
        switch (mouseEventButtonCode) {
            case MouseEvent.BUTTON2: return colorsInts[1];
            case MouseEvent.BUTTON3: return colorsInts[2];
            default: return colorsInts[0]; //MouseEvent.BUTTON1
        }
    }

    protected Color getColorByButton(int buttonNumber) {
        return new Color(getColorIntByButton(buttonNumber));
    }

    @Override public abstract void toolDragged(MouseEvent e);
    @Override public abstract void toolMoved(MouseEvent e);
    @Override public abstract void toolClicked(MouseEvent e);
    @Override public abstract void toolPressed(MouseEvent e);
    @Override public abstract void toolReleased(MouseEvent e);
    @Override public abstract void toolEntered(MouseEvent e);
    @Override public abstract void toolExited(MouseEvent e);
}
