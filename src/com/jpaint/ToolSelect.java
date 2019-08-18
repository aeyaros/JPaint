package com.jpaint;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class ToolSelect extends Tool {
    ToolSelect(String name, ImageModel model, String iconSource, String selectedIconSource) {
        super(name, model, iconSource, selectedIconSource);
        //set up upper card
        upperCard.add(new JButton("select button"));
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
