package com.jpaint;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class ToolPencil extends Tool {
    ToolPencil(String name, ImageModel model, String iconSource) {
        super(name, model, iconSource);
        //set up upper card
        _upperCard.add(new JButton("pencil button"));
    }

    int pastX;
    int pastY;

    @Override
    public void toolDragged(MouseEvent e) {
        int curX = e.getX();
        int curY = e.getY();
        int pstX = pastX;
        int pstY = pastY;
        pastX = curX;
        pastY = curY;

        int color = getColorIntByButton(e.getButton());
        _model.setPixel(curX, curY, getColorByButton(e.getButton()));
        bresenham(pstX, pstY, curX, curY, color);
        _model.refresh();
    }

    @Override
    public void toolMoved(MouseEvent e) {
        
    }

    @Override
    public void toolClicked(MouseEvent e) {

    }

    @Override
    public void toolPressed(MouseEvent e) {
        pastX = e.getX();
        pastY = e.getY();
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

    //https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
    //version that accounts for x and y error; a Java implementation
    //only a limited amount of mouse events are actually captured
    //this results in a row of dots on the canvas
    //this is solved by drawing a line from the current dot to the previous dot
    private void bresenham(int x0, int y0, int x1, int y1, int color) {
        int dx = Math.abs(x1 - x0);
        int sx = -1;
        if(x0 < x1) sx = 1;

        int dy = -1 * Math.abs(y1 - y0);
        int sy = -1;
        if(y0 < y1) sy = 1;

        int err = dx + dy; //error value ex_y
        int e2;

        while (x0 != x1 || y0 != y1) {
            //calculate next point on line towards current point
            // and update past coordinates (x0, y0) to that point
            e2 = 2 * err;
            if (e2 >= dy) {
                err += dy; // e_xy + e_x > 0
                x0 += sx;
            }
            if (e2 <= dx) { // e_xy + e_y < 0
                err += dx;
                y0 += sy;
            } //then draw at that point
            _model.setPixel(x0, y0, color);
        }

    }
}
