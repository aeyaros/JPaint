package com.jpaint;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class ToolLine extends Tool {
private final int MIN_WIDTH = 0; //technically width is 2n+1
private final int MAX_WIDTH = 31;
private final int DEFAULT_WIDTH = 1;
JSlider widthSlider;
JLabel widthLabel;
int width;
//current original point
int x0;
int y0;
private boolean twoClickMode;//if we are drawing line using two clicks
private boolean dragMode; //if we are dragging the line instead of two clicks


ToolLine(ImageModel model, String iconSource) {
	super(model, iconSource);
	upperCard.setLayout(new GridLayout(1, 0));
	
	widthSlider = new JSlider(SwingConstants.HORIZONTAL, MIN_WIDTH, MAX_WIDTH, DEFAULT_WIDTH);
	widthSlider.setMinorTickSpacing(1);
	widthSlider.setSnapToTicks(true);
	widthSlider.setPaintTicks(true);
    widthSlider.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            setWidth(widthSlider.getValue());
        }
    });
	widthSlider.setPreferredSize(new Dimension(96, 24));
	widthSlider.setMinimumSize(widthSlider.getPreferredSize());
	
	widthLabel = new JLabel(Integer.toString(widthSlider.getValue()), SwingConstants.CENTER);
	widthLabel = new JLabel(Integer.toString(widthSlider.getValue()), SwingConstants.CENTER);
	widthLabel.setPreferredSize(new Dimension(32, 24));
	widthLabel.setMinimumSize(widthLabel.getPreferredSize());
	widthLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
	
	JPanel widthPanel = new JPanel();
	widthPanel.setLayout(new BoxLayout(widthPanel, BoxLayout.X_AXIS));
	widthPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Stroke Width"));
	
	widthPanel.add(widthLabel);
	widthPanel.add(widthSlider);
	
	upperCard.add(widthPanel);
	
	setWidth(DEFAULT_WIDTH);
	
	resetPoints();
	resetStates();
	
	//cancel the operation if we select a different tool
    button.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                cancelDrawing();
            }
		}
	});
}
@Override void onButtonSelect() {
	model.updateSwingCursor(getCrossHairCursor());
}

private void setWidth(int w) {
	width = w;
	widthLabel.setText(Integer.toString(width * 2 + 1)); //because radius
}

//draw the line from the starting point to the current (ending) point
private void drawLine(int endX, int endY, int color, Canvas.DrawMode drawMode) {
	bresenham(x0, y0, endX, endY, color, true, drawMode);
}

@Override public void draw(int x, int y, int color, Canvas.DrawMode drawMode) {
	model.setPixel(x, y, color, drawMode);
}

@Override public void drawBrush(int x, int y, int color, Canvas.DrawMode drawMode) {
	makeCircle(x, y, color, width, false, drawMode);
}
@Override public void drawCursor(int x, int y, int color) {
	model.clearCanvasCursor();
	drawBrush(x, y, color, Canvas.DrawMode.USE_CURSOR);
	model.refreshCanvasCursor();
}

private void resetStates() {
	dragMode = false;
	twoClickMode = false;
}

private void resetPoints() {
	x0 = -1;
	y0 = -1;
}

//on first click or mousedown
void startDrawing(int startX, int startY, int color, MouseEvent e) {
	//dont save state until we know the line is getting drawn
	//get current points
	x0 = startX;
	y0 = startY;
	refreshPreview(x0, y0, color);
}

//when mouse is moved
void refreshPreview(int x1, int y1, int color) {
	model.clearOverlay();
	bresenham(x0, y0, x1, y1, color, true, Canvas.DrawMode.USE_OVERLAY);
}

//on second click, or mouse release
void finishDrawing(int endX, int endY, int color, MouseEvent e) {
	//we know we are drawing the line now, and so we are going to save the old state
	model.saveCurrentState();
	model.clearOverlay();
	drawLine(endX, endY, color, Canvas.DrawMode.USE_OVERLAY);
	model.mergeOverlay();
	model.refreshView();
}

//cancel the drawing
private void cancelDrawing() {
	resetStates();
	resetPoints();
	model.clearOverlay();
	model.refreshView();
}

//refresh preview if we are doing two click mode
@Override
public void toolMoved(MouseEvent e) {
	//if the current tool isn't selected, then we should cancel; doesn't work
	if (!this.button.isSelected()) cancelDrawing();
	
	else if (twoClickMode) refreshPreview(e.getX(), e.getY(), getColorIntByButton(e.getButton()));
	
	drawCursor(e.getX(), e.getY(), getColorIntByButton(e.getButton()));
}

//refresh preview if we are doing drag mode
@Override
public void toolDragged(MouseEvent e) {
	if (dragMode) refreshPreview(e.getX(), e.getY(), getColorIntByButton(e.getButton()));
	
	drawCursor(e.getX(), e.getY(), getColorIntByButton(e.getButton()));
}

//handle mouseclicks for two click mode
@Override
public void toolClicked(MouseEvent e) {
	//make sure we aren't in drag mode before doing anything
	if (!dragMode) {
		if (!twoClickMode) { //first click
			twoClickMode = true;
			startDrawing(e.getX(), e.getY(), getColorIntByButton(e.getButton()), e);
		} else { //second click
			//do stuff to finish line
			finishDrawing(e.getX(), e.getY(), getColorIntByButton(e.getButton()), e);
			twoClickMode = false;
		}
	}
}

//handle dragging:

//start of drag
@Override
public void toolPressed(MouseEvent e) {
	//start of drag if neither mode is enabled
	if (!dragMode && !twoClickMode) {
		dragMode = true; //set as drag mode
		startDrawing(e.getX(), e.getY(), getColorIntByButton(e.getButton()), e);
	}
}

//end of drag
@Override
public void toolReleased(MouseEvent e) {
	//end of drag
	if (dragMode && !twoClickMode) {
		//finish the drag stuff
		finishDrawing(e.getX(), e.getY(), getColorIntByButton(e.getButton()), e);
		//then set drag as false
		dragMode = false;
	}
}

@Override
public void toolEntered(MouseEvent e) {
	drawCursor(e.getX(), e.getY(), getColorIntByButton(e.getButton()));
}

@Override
public void toolExited(MouseEvent e) {
	model.clearCanvasCursor();
	model.refreshCanvasCursor();
}

//if a user presses escape, then cancel line drawing
@Override
public void toolKeyPressed(KeyEvent e) {
	System.out.println(e.getExtendedKeyCode() + "!");
	if (e.getExtendedKeyCode() == KeyEvent.VK_ESCAPE ||
	    e.getExtendedKeyCode() == KeyEvent.VK_CLEAR ||
	    e.getExtendedKeyCode() == KeyEvent.VK_CANCEL ||
	    e.getExtendedKeyCode() == KeyEvent.VK_BACK_SPACE ||
	    e.getExtendedKeyCode() == KeyEvent.VK_STOP ||
	    e.getExtendedKeyCode() == KeyEvent.VK_END
	) {
		cancelDrawing();
	}
}

@Override
public void toolKeyTyped(KeyEvent e) {
}

@Override
public void toolKeyReleased(KeyEvent e) {
}
}
