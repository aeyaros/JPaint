package com.jpaint;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

//MouseController: This is a controller class for mouse stuff
public class MouseController implements MouseListener, MouseMotionListener {
    private boolean mousedown;

    MouseController() {
        mousedown = false;
    }

    //MouseListener
    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("Clicked");
    }
    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println("Pressed");
        mousedown = true;
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println("Released");
        mousedown = false;
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        System.out.println("Entered");
    }
    @Override
    public void mouseExited(MouseEvent e) {
        System.out.println("Exited");
    }

    //MouseMotionListener
    @Override
    public void mouseDragged(MouseEvent e) {
        System.out.print("Dragged");
        /*int currentX = e.getX();
        int currentY = e.getY();
        System.out.print(" drag: x: "); System.out.print(currentX);
        System.out.print(", y: "); System.out.print(currentY);
        System.out.print(", color: "); System.out.println(Main.image.getPixel(currentX,currentY).printValues());
        Main.image.draw(currentX, currentY, new Color(0,0,0,255));*/
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        System.out.print("Moved");
        int currentX = e.getX();
        int currentY = e.getY();
        System.out.print(" move: x: "); System.out.print(currentX);
        System.out.print(", y: "); System.out.print(currentY);
    }
}
