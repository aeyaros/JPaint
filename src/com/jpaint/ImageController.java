package com.jpaint;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

//MouseController: This is a controller class for mouse stuff
public class ImageController implements MouseListener, MouseMotionListener {
    private ImageModel _model;
    private boolean _mouseDown;

    ImageController(ImageModel model) {
        _model = model;
        _mouseDown = false;
    }

    //MouseListener
    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("Clicked");
        int x = e.getX();
        int y = e.getY();
        _model.setPixel(x,y,new Color(255,0,0,0));
        _model.refresh();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //System.out.println("Pressed");
        _mouseDown = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //System.out.println("Released");
        _mouseDown = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //System.out.println("Entered");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //System.out.println("Exited");
    }

    //MouseMotionListener
    @Override
    public void mouseDragged(MouseEvent e) {
        System.out.print("Dragged");
        int currentX = e.getX();
        int currentY = e.getY();
        _model.setPixel(currentX,currentY,new Color(255,0,0,0));
        _model.refresh();
        /*
        System.out.print(" drag: x: "); System.out.print(currentX);
        System.out.print(", y: "); System.out.print(currentY);
        System.out.print(", color: "); System.out.println(Main.image.getPixel(currentX,currentY).printValues());

         */
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //System.out.print("Moved");
        /*int currentX = e.getX();
        int currentY = e.getY();
        System.out.print(" move: x: "); System.out.print(currentX);
        System.out.print(", y: "); System.out.print(currentY);*/
    }
}
