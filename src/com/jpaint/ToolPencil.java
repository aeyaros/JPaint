package com.jpaint;

import java.awt.event.MouseEvent;

public class ToolPencil extends Tool {
    ToolPencil(String name, ImageModel model, String iconSource) {
        super(name, model, iconSource);
    }

    @Override
    public void toolDragged(MouseEvent e) {
        _model.setPixel(e.getX(),e.getY(), getColorByButton(e.getButton()));
    }

    @Override
    public void toolMoved(MouseEvent e) {

    }

    @Override
    public void toolClicked(MouseEvent e) {

    }

    @Override
    public void toolPressed(MouseEvent e) {
        System.out.println("Start drag");
    }

    @Override
    public void toolReleased(MouseEvent e) {
        System.out.println("End drag");
    }

    @Override
    public void toolEntered(MouseEvent e) {

    }

    @Override
    public void toolExited(MouseEvent e) {

    }

}
