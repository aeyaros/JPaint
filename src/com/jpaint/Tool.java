package com.jpaint;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

//Tool: This is a tool button - subclasses contain specific tool functionality
public abstract class Tool implements ToolInput {
    protected ImageModel _model; //the model class this tool acts on
    JPanel _upperCard; //the upper panel shown when this tool is selected
    JButton _button; //the tool button
    private String _name;
    private Color _left;
    private Color _middle;
    private Color _right;
    private int _leftint;
    private int _middleint;
    private int _rightint;

    //set button name and add the model
    Tool(String name, ImageModel model, String iconSource) {
        super();
        _name = name;
        _model = model;

        //panel with top controls
        _upperCard = new JPanel();

        //button for toolbar
        _button = new JButton();

        //sidebar button
        Dimension size = new Dimension();
        _button.setSize(WindowSetup.TOOL_BUTTON_SIZE, WindowSetup.TOOL_BUTTON_SIZE);
        _button.setBorderPainted(false);


        try { //get icon of button
            Image image = ImageIO.read(getClass().getResource(iconSource));
            image = image.getScaledInstance(WindowSetup.TOOL_BUTTON_SIZE, WindowSetup.TOOL_BUTTON_SIZE, Image.SCALE_SMOOTH);
            _button.setIcon(new ImageIcon(image));
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Couldn't load icon for " + name + " button from location " + iconSource);
        }
    }

    public void updateColors(Color left, Color middle, Color right) {
        _left = left;
        _middle = middle;
        _right = right;
        _leftint = left.getARGB();
        _middleint = middle.getARGB();
        _rightint = right.getARGB();
    }

    protected Color getColorByButton(int buttonNumber) {
        switch (buttonNumber) {
            case MouseEvent.BUTTON1: return _left;
            case MouseEvent.BUTTON2: return _middle;
            case MouseEvent.BUTTON3: return _right;
            default: return new Color(255,0,0,0);
        }
    }

    protected int getColorIntByButton(int buttonNumber) {
        switch (buttonNumber) {
            case MouseEvent.BUTTON1: return _leftint;
            case MouseEvent.BUTTON2: return _middleint;
            case MouseEvent.BUTTON3: return _rightint;
            default: return new Color(255,0,0,0).getARGB();
        }
    }

    public String getName() {
        return _name;
    }

    @Override public abstract void toolDragged(MouseEvent e);
    @Override public abstract void toolMoved(MouseEvent e);
    @Override public abstract void toolClicked(MouseEvent e);
    @Override public abstract void toolPressed(MouseEvent e);
    @Override public abstract void toolReleased(MouseEvent e);
    @Override public abstract void toolEntered(MouseEvent e);
    @Override public abstract void toolExited(MouseEvent e);
}
