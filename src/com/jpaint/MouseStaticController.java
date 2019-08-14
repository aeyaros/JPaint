package com.jpaint;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

//send mouse static events to tools
public class MouseStaticController extends MouseController implements MouseListener {
    MouseStaticController(Tool initialTool) { super(initialTool); }
    @Override public void mouseClicked(MouseEvent e) {
        _currentTool.toolClicked(e);
    }
    @Override public void mousePressed(MouseEvent e) {
        _currentTool.toolPressed(e);
    }
    @Override public void mouseReleased(MouseEvent e) {
        _currentTool.toolReleased(e);
    }
    @Override public void mouseEntered(MouseEvent e) {
        _currentTool.toolEntered(e);
    }
    @Override public void mouseExited(MouseEvent e) {
        _currentTool.toolExited(e);
    }
}