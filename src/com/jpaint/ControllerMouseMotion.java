package com.jpaint;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

//send mouse motion events to tools
class ControllerMouseMotion extends Controller implements MouseMotionListener {
    ControllerMouseMotion(Tool[] tools) {
        super(tools);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        tools[index].toolDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        tools[index].toolMoved(e);
    }
}