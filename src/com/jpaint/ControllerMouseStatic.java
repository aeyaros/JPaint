package com.jpaint;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

//send mouse static events to tools
class ControllerMouseStatic extends Controller implements MouseListener {
    ControllerMouseStatic(Tool[] tools) {
        super(tools);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        tools[index].toolClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        tools[index].toolPressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        tools[index].toolReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        tools[index].toolEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        tools[index].toolExited(e);
    }
}