package com.jpaint;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

//send mouse motion events to tools
public class MouseMotionController extends MouseController implements MouseMotionListener {
    MouseMotionController(Tool[] tools) { super(tools); }
    @Override public void mouseDragged(MouseEvent e) {
        tools[index].toolDragged(e);
    }
    @Override public void mouseMoved(MouseEvent e) {
        tools[index].toolMoved(e);
    }
}