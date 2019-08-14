package com.jpaint;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

//Tool: This is a tool button - subclasses contain specific tool functionality
public abstract class Tool extends JButton implements ToolInput {
    protected ImageModel _model; //the model class this tool acts on
    protected JPanel _upperCard; //the upper panel shown when this tool is selected

    Color _left;
    Color _middle;
    Color _right;

    //set button name and add the model
    Tool(String name, ImageModel model, String iconSource) {
        super();

        //button stuff
        Dimension size = new Dimension();
        this.setSize(Main.TOOL_BUTTON_SIZE, Main.TOOL_BUTTON_SIZE);
        this.setBorderPainted(false);
        _model = model;

        try { //get icon of button
            Image image = ImageIO.read(getClass().getResource(iconSource));
            image = image.getScaledInstance(Main.TOOL_BUTTON_SIZE, Main.TOOL_BUTTON_SIZE, Image.SCALE_SMOOTH);
            this.setIcon(new ImageIcon(image));
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Couldn't load icon for " + name + " button from location " + iconSource);
        }
    }

    public void updateColors(Color left, Color middle, Color right) {
        _left = left;
        _middle = middle;
        _right = right;
    };

    protected Color getColorByButton(int buttonNumber) {
        switch (buttonNumber) {
            case MouseEvent.BUTTON1: return _left;
            case MouseEvent.BUTTON2: return _middle;
            case MouseEvent.BUTTON3: return _right;
            default: return new Color(255,0,0,0);
        }
    }

    @Override public abstract void toolDragged(MouseEvent e);
    @Override public abstract void toolMoved(MouseEvent e);

    @Override public abstract void toolClicked(MouseEvent e);
    @Override public abstract void toolPressed(MouseEvent e);
    @Override public abstract void toolReleased(MouseEvent e);
    @Override public abstract void toolEntered(MouseEvent e);
    @Override public abstract void toolExited(MouseEvent e);
}
