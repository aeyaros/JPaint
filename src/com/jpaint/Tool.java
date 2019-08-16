package com.jpaint;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

//Tool: This is a tool button - subclasses contain specific tool functionality
public abstract class Tool implements ToolInput {
    protected ImageModel model; //the model class this tool acts on
    JPanel upperCard; //the upper panel shown when this tool is selected
    JButton button; //the tool button
    private String name;

    private int leftInt;
    private int middleInt;
    private int rightInt;

    //set button name and add the model
    Tool(String name, ImageModel model, String iconSource) {
        super();
        this.name = name;
        this.model = model;

        //panel with top controls
        upperCard = new JPanel();

        //button for toolbar
        button = new JButton();

        //sidebar button
        Dimension size = new Dimension();
        button.setSize(WindowSetup.TOOL_BUTTON_SIZE, WindowSetup.TOOL_BUTTON_SIZE);
        button.setBorderPainted(false);


        try { //get icon of button
            Image image = ImageIO.read(getClass().getResource(iconSource));
            image = image.getScaledInstance(WindowSetup.TOOL_BUTTON_SIZE, WindowSetup.TOOL_BUTTON_SIZE, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(image));
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Couldn't load icon for " + name + " button from location " + iconSource);
        }
    }

    void updateColors(Color left, Color middle, Color right) {
        leftInt = left.getARGB();
        middleInt = middle.getARGB();
        rightInt = right.getARGB();
    }

    protected Color getColorByButton(int buttonNumber) {
        switch (buttonNumber) {
            case MouseEvent.BUTTON1: return new Color(leftInt);
            case MouseEvent.BUTTON2: return new Color(middleInt);
            case MouseEvent.BUTTON3: return new Color(rightInt);
            default: return new Color(255,0,0,0);
        }
    }

    protected int getColorIntByButton(int buttonNumber) {
        switch (buttonNumber) {
            case MouseEvent.BUTTON1: return leftInt;
            case MouseEvent.BUTTON2: return middleInt;
            case MouseEvent.BUTTON3: return rightInt;
            default: return new Color(255,0,0,0).getARGB();
        }
    }

    String getName() {
        return name;
    }

    @Override public abstract void toolDragged(MouseEvent e);
    @Override public abstract void toolMoved(MouseEvent e);
    @Override public abstract void toolClicked(MouseEvent e);
    @Override public abstract void toolPressed(MouseEvent e);
    @Override public abstract void toolReleased(MouseEvent e);
    @Override public abstract void toolEntered(MouseEvent e);
    @Override public abstract void toolExited(MouseEvent e);
}
