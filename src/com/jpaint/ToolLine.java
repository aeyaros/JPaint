package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class ToolLine extends Tool {
    private final int MIN_WIDTH = 0; //technically width is 2n+1
    private final int MAX_WIDTH = 11;
    private final int DEFALUT_WIDTH = 1;
    private boolean twoClickMode;//if we are drawing line using two clicks
    private boolean dragMode; //if we are dragging the line instead of two clicks
    private boolean useOverlay; //true if we are previewing, false if drawing for real
    private int width;
    private int negativeWidth; //for optimization
    private int currentColor;
    private JSlider widthSlider;
    private JLabel widthLabel;

    //current original point
    private int x0;
    private int y0;


    ToolLine(String name, ImageModel model, String iconSource, String selectedIconSource) {
        super(name, model, iconSource, selectedIconSource);
        upperCard.setLayout(new GridLayout(1,0));

        widthSlider = new JSlider(SwingConstants.HORIZONTAL,MIN_WIDTH,MAX_WIDTH,DEFALUT_WIDTH);
        widthLabel = new JLabel(Integer.toString(widthSlider.getValue()), SwingConstants.CENTER);
        widthSlider.addChangeListener(e -> setWidth(widthSlider.getValue()));

        JPanel widthPanel = new JPanel();
        widthPanel.setLayout(new BoxLayout(widthPanel,BoxLayout.X_AXIS));
        widthPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Line Width"));

        widthPanel.add(widthLabel);
        widthPanel.add(widthSlider);
        upperCard.add(widthPanel);

        setWidth(DEFALUT_WIDTH);

        currentColor = new Color(255,0,0,0).getARGB();
        resetPoints();
        resetStates();

        //cancel the operation if we select a different tool
        button.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.DESELECTED) {
                cancelLineDrawing();
            }
        });
    }

    private void setWidth(int w) {
        width = w;
        negativeWidth = -w;
        widthLabel.setText(Integer.toString(width * 2 + 1)); //because radius
    }

    //draw the line from the starting point to the current (ending) point
    private void drawLine(int endX, int endY, int color) {
        bresenham(x0,y0,endX,endY,color);
    }

    //draw command used by draw function
    public void draw(int x, int y, int color) {
        makeCircle(x,y,color,width, negativeWidth, true, useOverlay);
    }

    private void resetStates() {
        dragMode = false;
        twoClickMode = false;
        useOverlay = true; //more often than not we are using overlay
    }

    private void resetPoints() {
        x0 = -1;
        y0 = -1;
    }

    //on first click or mousedown
    private void startLineDrawing(int startX, int startY, int color) {
        //dont save state until we know the line is getting drawn
        //get current points
        x0 = startX;
        y0 = startY;
        currentColor = color;
        refreshLinePreview(x0,y0, color);
    }

    //when mouse is moved
    private void refreshLinePreview(int x1, int y1, int color) {
        model.clearOverlay();
        if(!useOverlay) useOverlay = true;
        bresenham(x0,y0,x1,y1,color);
    }

    //on second click, or mouse release
    private void finishLineDrawing(int endX, int endY) {
        //we know we are drawing the line now, and so we are going to save the old state
        model.saveCurrentState();
        model.clearOverlay();
        useOverlay = false;
        drawLine(endX,endY,currentColor);
        useOverlay = true;
        model.refreshView();
    }

    //cancel the drawing
    private void cancelLineDrawing() {
        resetStates();
        resetPoints();
        model.clearOverlay();
        model.refreshView();
    }

    //refresh preview if we are doing two click mode
    @Override
    public void toolMoved(MouseEvent e) {
        //if the current tool isnt selected, then we should cancel; doenst work
        if(!this.button.isSelected()) cancelLineDrawing();

        else if(twoClickMode) refreshLinePreview(e.getX(), e.getY(), currentColor);
    }

    //refresh preview if we are doing drag mode
    @Override
    public void toolDragged(MouseEvent e) {
        if(dragMode) refreshLinePreview(e.getX(), e.getY(), currentColor);
    }

    //handle mouseclicks for two click mode
    @Override
    public void toolClicked(MouseEvent e) {
        //make sure we arent in drag mode before doing anything
        if(!dragMode) {
            if(!twoClickMode) { //first click
                twoClickMode = true;
                startLineDrawing(e.getX(), e.getY(),getColorIntByButton(e.getButton()));
            } else { //second click
                //do stuff to finish line
                finishLineDrawing(e.getX(), e.getY());
                twoClickMode = false;
            }
        }
    }

    //handle dragging:

    //start of drag
    @Override
    public void toolPressed(MouseEvent e) {
        //start of drag if neither mode is enabled
        if(!dragMode && !twoClickMode) {
            dragMode = true; //set as drag mode
            startLineDrawing(e.getX(), e.getY(), getColorIntByButton(e.getButton()));
        }
    }

    //end of drag
    @Override
    public void toolReleased(MouseEvent e) {
        //end of drag
        if(dragMode && !twoClickMode) {
            //finish the drag stuff
            finishLineDrawing(e.getX(),e.getY());
            //then set drag as false
            dragMode = false;
        }
    }

    @Override public void toolEntered(MouseEvent e) { }
    @Override public void toolExited(MouseEvent e) { }

    //if a user presses escape, then cancel line drawing
    @Override
    public void toolKeyPressed(KeyEvent e) {
        System.out.println(e.getExtendedKeyCode() + "!");
        if(e.getExtendedKeyCode() == KeyEvent.VK_ESCAPE ||
                e.getExtendedKeyCode() == KeyEvent.VK_CLEAR ||
                e.getExtendedKeyCode() == KeyEvent.VK_CANCEL ||
                e.getExtendedKeyCode() == KeyEvent.VK_BACK_SPACE ||
                e.getExtendedKeyCode() == KeyEvent.VK_STOP ||
                e.getExtendedKeyCode() == KeyEvent.VK_END
        ) {
            cancelLineDrawing();
        }
    }

    @Override public void toolKeyTyped(KeyEvent e) { }
    @Override public void toolKeyReleased(KeyEvent e) { }
}
