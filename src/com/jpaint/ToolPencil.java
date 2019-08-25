package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class ToolPencil extends Tool {
//store previous coordinates between drag events
private int pastX;
private int pastY;

ToolPencil(ImageModel model, String iconSource) {
	super(model, iconSource);
	upperCard.setLayout(new CardLayout());
	//set up upper card
	JLabel infoText = new JLabel("Click and drag to draw.");
	infoText.setHorizontalAlignment(SwingConstants.CENTER);
	infoText.setVerticalAlignment(SwingConstants.CENTER);
	upperCard.add(infoText, 0);
	
	pastY = -1;
	pastX = -1;
}

private boolean canDraw() {
	return (pastY != -1 && pastX != -1);
}


public void draw(int x, int y, int color) {
	model.setPixel(x, y, color, true); //draw at the point
}

@Override public void drawBrush(int x, int y, int color) {
	draw(x, y, color); //only draw one pixel at a time
}

private void preventDrawing() {
	pastX = -1;
	pastY = -1;
}

//at start of drag, set past coordinates to current coordinates
// to avoid drawing a line from the end of the previous stroke
@Override
public void toolPressed(MouseEvent e) {
	pastX = e.getX();
	pastY = e.getY();
	model.saveCurrentState();
	//System.out.println("Start drag");
}

@Override
public void toolEntered(MouseEvent e) {
}

//during drag, draw points and lines
//*************************************************
//Initially, we dont allow drawing until the toolPressed event (start of the drag)
//this is to ensure we save the current state before making any changes to the canvas
//*************************************************
@Override
public void toolDragged(MouseEvent e) {
	if (canDraw()) {
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
		
		drawBrush(curX, curY, colorInt);
		bresenham(oldX, oldY, curX, curY, colorInt, true); //draw line from past point to current point
		
		model.refreshView(); //tell model to refresh view
	}
}

//at end of drag, prevent drawing again
@Override
public void toolReleased(MouseEvent e) {
	preventDrawing();
	model.mergeOverlay();
	//System.out.println("End drag");
}

//if you click once it will draw a point
@Override
public void toolClicked(MouseEvent e) {
	model.saveCurrentState();
	drawBrush(e.getX(), e.getY(), getColorIntByButton(e.getButton()));
	model.refreshView();
}

@Override public void toolExited(MouseEvent e) { }
@Override public void toolMoved(MouseEvent e) { }
@Override public void toolKeyPressed(KeyEvent e) { }
@Override public void toolKeyReleased(KeyEvent e) { }
@Override public void toolKeyTyped(KeyEvent e) { }
}
