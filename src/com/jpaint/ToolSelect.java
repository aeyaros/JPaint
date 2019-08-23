package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class ToolSelect extends Tool {
    ToolSelect(ImageModel model, String iconSource) {
        super(model, iconSource);
        //set up upper card
        upperCard.setLayout(new CardLayout());
        //set up upper card
        JLabel infoText = new JLabel("Select tool - Not yet implemented");
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

    @Override
    public void draw(int x, int y, int color) {

    }

    @Override
    public void toolKeyPressed(KeyEvent e) {

    }

    @Override
    public void toolKeyReleased(KeyEvent e) {

    }

    @Override
    public void toolKeyTyped(KeyEvent e) {

    }
}
