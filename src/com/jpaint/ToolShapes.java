package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ToolShapes extends Tool {
    ToolShapes(String name, ImageModel model, String iconSource, String selectedIconSource) {
        super(name, model, iconSource, selectedIconSource);
        //set up upper card
        upperCard.setLayout(new CardLayout());
        //set up upper card
        JLabel infoText = new JLabel("Shapes tool - Not yet implemented");
        infoText.setHorizontalAlignment(SwingConstants.CENTER);
        infoText.setVerticalAlignment(SwingConstants.CENTER);
        upperCard.add(infoText,0);
    }

    @Override
    public void toolDragged(MouseEvent e) {

    }

    @Override
    public void toolMoved(MouseEvent e) {

    }

    @Override
    public void toolClicked(MouseEvent e) {

    }

    @Override
    public void toolPressed(MouseEvent e) {

    }

    @Override
    public void toolReleased(MouseEvent e) {

    }

    @Override
    public void toolEntered(MouseEvent e) {

    }

    @Override
    public void toolExited(MouseEvent e) {

    }

}
