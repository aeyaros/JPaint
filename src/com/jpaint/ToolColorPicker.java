package com.jpaint;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class ToolColorPicker extends Tool {

    ToolColorPicker(String name, ImageModel model, String iconSource) {
        super(name, model, iconSource);
        //set up upper card
        _upperCard.add(new JButton("picker button"));
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
