package com.jpaint;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;

public class ToolShapes extends ToolLine {
//values
private final int SIDES_IN_A_CIRCLE = 64;
private final int MINIMUM_POSSIBLE_NUMBER_OF_SIDES = 3;
private final int MAX_ALLOWED_NUMBER_OF_SIDES = 8;
//colors
private int strokeColor;
private int fillColor;
//states
private boolean useStroke;
private boolean shouldFill;
private int numberOfSides;
//objects
private JSlider sidesChangerSlider;

ToolShapes(ImageModel model, String iconSource) {
	super(model, iconSource);
	
	upperCard.removeAll();
	upperCard.setLayout(new GridLayout(1, 0));
	
	
	//setting border mode/width
	JPanel borderOptions = new JPanel(new GridLayout(1, 0));
	ButtonGroup borderOptionButtons = new ButtonGroup();
	JToggleButton borderOnly = new JToggleButton("Border", true);
	borderOnly.addItemListener(e -> {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			setFillEnabled(false);
			setStrokeEnabled(true);
			System.out.println("Border only");
		}
	});
	
	//set values for first button; button event doesnt happen initially
	setFillEnabled(false);
	setStrokeEnabled(true);
	
	JToggleButton borderWithFill = new JToggleButton("Border+Fill", false);
	borderWithFill.addItemListener(e -> {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			setFillEnabled(true);
			setStrokeEnabled(true);
			System.out.println("Border and fill");
		}
	});
	JToggleButton fillOnly = new JToggleButton("Fill", false);
	fillOnly.addItemListener(e -> {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			setFillEnabled(true);
			setStrokeEnabled(false);
			System.out.println("Fill only");
		}
	});
	
	
	borderOptions.add(borderOnly);
	borderOptions.add(borderWithFill);
	borderOptions.add(fillOnly);
	
	borderOptionButtons.add(borderOnly);
	borderOptionButtons.add(borderWithFill);
	borderOptionButtons.add(fillOnly);
	
	JPanel borderPanel = new JPanel();
	borderPanel.setLayout(new BoxLayout(borderPanel, BoxLayout.X_AXIS));
	borderPanel.add(borderOptions, BorderLayout.WEST);
	borderPanel.add(widthSlider, BorderLayout.CENTER);
	borderPanel.add(widthLabel, BorderLayout.EAST);
	
	upperCard.add(borderPanel);
	
	
	//set number of sides
	sidesChangerSlider = new JSlider(SwingConstants.HORIZONTAL, MINIMUM_POSSIBLE_NUMBER_OF_SIDES,
	                                 MAX_ALLOWED_NUMBER_OF_SIDES, MINIMUM_POSSIBLE_NUMBER_OF_SIDES
	);
	setNumberOfSides(MINIMUM_POSSIBLE_NUMBER_OF_SIDES);
	
	sidesChangerSlider.setMajorTickSpacing(1);
	sidesChangerSlider.setPaintTicks(true);
	sidesChangerSlider.setPaintLabels(true);
	sidesChangerSlider.setSnapToTicks(true);
	sidesChangerSlider.addChangeListener(e -> setNumberOfSides(sidesChangerSlider.getValue()));
	JPanel sidesChangeerPanel = new JPanel();
	sidesChangeerPanel.add(sidesChangerSlider);
	
	upperCard.add(sidesChangeerPanel);
}

private void setStrokeEnabled(boolean isEnabled) {
	useStroke = isEnabled;
	if (isEnabled) {
		widthSlider.setEnabled(true);
		width = widthSlider.getValue();
	} else {
		widthSlider.setEnabled(false);
		width = 1;
	}
}

private void setFillEnabled(boolean enabled) {
	shouldFill = enabled;
}

//if not using stroke, then the width is as small as possible
private int getStrokeWidth() {
	if (useStroke) return width;
	else return 1;
}

//if no stroke, then color of thin border and fill should match
private int getFillColor() {
	if (useStroke) return fillColor;
	else return strokeColor;
}

//minimum of 3 sides
private void setNumberOfSides(int n) {
	if (n < MINIMUM_POSSIBLE_NUMBER_OF_SIDES) numberOfSides = MINIMUM_POSSIBLE_NUMBER_OF_SIDES;
	else numberOfSides = n;
}

@Override public void draw(int x, int y, int color) {
	model.setPixel(x, y, color, true);
}

@Override public void drawBrush(int x, int y, int color) {
	makeCircle(x, y, color, getStrokeWidth(), false);
}

private void drawShape(int originX, int originY, int currentX, int currentY, int type) {
	//calculate radius based on current point
	int w = currentX - originX;
	int h = currentY - originY;
	int radius = (int) Math.sqrt((w * w) + (h * h));
	double offset = Math.atan2(h, w); //calculate angle offset, so first point matches current location of pointer
	
	if (type == 0) { //draw circle
		//makeCircle(originX, originY, borderColor, radius,true);
		makeRegularPolygon(originX, originY, SIDES_IN_A_CIRCLE, radius, offset, strokeColor, true);
	} else if (type > 2) { //draw polygon
		//then draw polygon
		makeRegularPolygon(originX, originY, type, radius, offset, strokeColor, true);
	} else return; //else dont draw
	
	//fill in the shape; dont fill if radius is too small
	System.out.println("lol");
	if (shouldFill && radius > 1) {
		fill(originX, originY, getFillColor(), true);
		System.out.println("hiiii");
	}
}

//when mouse is moved
@Override void refreshPreview(int x1, int y1, int color) {
	model.clearOverlay();
	drawShape(x0, y0, x1, y1, numberOfSides);
	model.refreshView();
}

//on first click or mousedown
@Override void startDrawing(int startX, int startY, int color, MouseEvent e) {
	//dont save state until we know the line is getting drawn
	//get current points
	x0 = startX;
	y0 = startY;
	strokeColor = getColorIntByButton(e.getButton());
	fillColor = getAlternateColor(e.getButton());
}

//on second click, or mouse release
@Override void finishDrawing(int endX, int endY, int color, MouseEvent e) {
	//we know we are drawing the line now, and so we are going to save the old state
	model.saveCurrentState();
	model.clearOverlay();
	strokeColor = getColorIntByButton(e.getButton());
	fillColor = getAlternateColor(e.getButton());
	drawShape(x0, y0, endX, endY, numberOfSides);
	model.mergeOverlay();
	model.refreshView();
}

}
