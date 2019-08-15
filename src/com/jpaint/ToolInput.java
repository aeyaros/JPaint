package com.jpaint;

import java.awt.event.MouseEvent;

//interface for editing the canvas
public interface ToolInput {
    //motion listener
    void toolDragged(MouseEvent e);
    void toolMoved(MouseEvent e);

    //regular listener
    void toolClicked(MouseEvent e);
    void toolPressed(MouseEvent e);
    void toolReleased(MouseEvent e);
    void toolEntered(MouseEvent e);
    void toolExited(MouseEvent e);
}
