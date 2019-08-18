package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ToolPencil extends Tool {
    ToolPencil(String name, ImageModel model, String iconSource) {
        super(name, model, iconSource);
        upperCard.setLayout(new CardLayout());
        //set up upper card
        JLabel infoText = new JLabel("Click and drag to draw.");
        infoText.setHorizontalAlignment(SwingConstants.CENTER);
        infoText.setVerticalAlignment(SwingConstants.CENTER);
        upperCard.add(infoText,0);

        pastY = -1;
        pastX = -1;
    }

    //store previous coordinates between drag events
    private int pastX;
    private int pastY;

    private boolean canDraw() {
        return (pastY != -1 && pastX != -1);
    }

    private void preventDrawing() {
        pastX = -1;
        pastY = -1;
    }

    //at start of drag, set past coordinates to current coordinates
    // to avoid drawing a line from the end of the previous stroke
    @Override
    public void toolPressed(MouseEvent e) {
        model.saveCurrentState();
        pastX = e.getX();
        pastY = e.getY();
        System.out.println("Start drag");
    }

    //during drag, draw points and lines
    //*************************************************
    //Initially, we dont allow drawing until the toolPressed event (start of the drag)
    //this is to ensure we save the current state before making any changes to the canvas
    //*************************************************
    @Override
    public void toolDragged(MouseEvent e) {
        if(canDraw()) {
            //copy to local variables for safety
            int oldX = pastX;
            int oldY = pastY;
            //get current variables
            int curX = e.getX();
            int curY = e.getY();
            //immediately save current variables for the next function call
            pastX = curX;
            pastY = curY;
            //get color to draw with
            int colorInt = getColorIntByButton(e.getButton());
            model.setPixel(curX, curY, colorInt); //draw at the point
            bresenham(oldX, oldY, curX, curY, colorInt); //draw line from past point to current point

            model.refresh(); //tell model to refresh view
        }
    }

    //at end of drag, prevent drawing again
    @Override public void toolReleased(MouseEvent e) {
        preventDrawing();
        System.out.println("End drag");
    }

    //if you click once it will draw a point
    @Override public void toolClicked(MouseEvent e) {
        model.saveCurrentState();
        model.setPixel(e.getX(), e.getY(), getColorIntByButton(e.getButton()));
        model.refresh();
    }
    
    //source of algorithm:
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

        while (x0 != x1 || y0 != y1) { //until we reach the current point
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
            }

            //then draw at that point
            model.setPixel(x0, y0, color);
        }
    }

    @Override public void toolMoved(MouseEvent e) {

    }
    @Override public void toolEntered(MouseEvent e) {

    }
    @Override public void toolExited(MouseEvent e) {

    }
}
