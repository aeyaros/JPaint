package com.jpaint;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

//send mouse motion events to tools
public class MouseMotionController extends MouseController implements MouseMotionListener {
    MouseMotionController(Tool initialTool) { super(initialTool); }
    @Override public void mouseDragged(MouseEvent e) {
        System.out.print(_currentTool.getName());
        _currentTool.toolDragged(e);
    }
    @Override public void mouseMoved(MouseEvent e) {
        _currentTool.toolMoved(e);
    }
}