package com.jpaint;

import java.awt.event.MouseEvent;

//interface for editing the canvas
public interface ToolInput {
    //motion listener
    public void toolDragged(MouseEvent e);
    public void toolMoved(MouseEvent e);

    //regular listener
    public void toolClicked(MouseEvent e);
    public void toolPressed(MouseEvent e);
    public void toolReleased(MouseEvent e);
    public void toolEntered(MouseEvent e);
    public void toolExited(MouseEvent e);
}
