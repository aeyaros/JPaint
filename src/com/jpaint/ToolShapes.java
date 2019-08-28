package com.jpaint;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

public class ToolShapes extends ToolLine {
//values
private final int SIDES_IN_A_CIRCLE = 64;
private final int MINIMUM_POSSIBLE_NUMBER_OF_SIDES = 3;
private final int MAX_ALLOWED_NUMBER_OF_SIDES = 9; //really 8; at max number setting we will do a circle
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
	
	//panel holding everything
	JPanel mainPanel = new JPanel();
	mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
	
	
	//setting border mode/width
	JPanel borderOptions = new JPanel();
	borderOptions.setLayout(new BoxLayout(borderOptions, BoxLayout.X_AXIS));
	ButtonGroup borderOptionButtons = new ButtonGroup();
	
	//first button
	ToolbarButton borderOnly = new ToolbarButton("icons/shape_tool_icons/onlystroke.png"); //stroke only
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
	borderOnly.setSelected(true);
	
	//second button
	ToolbarButton borderWithFill = new ToolbarButton("icons/shape_tool_icons/strokeandfill.png"); //border + fill
	borderWithFill.addItemListener(e -> {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			setFillEnabled(true);
			setStrokeEnabled(true);
			System.out.println("Border and fill");
		}
	});
	
	//third button
	ToolbarButton fillOnly = new ToolbarButton("icons/shape_tool_icons/onlyfill.png"); //fill only
	fillOnly.addItemListener(e -> {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			setFillEnabled(true);
			setStrokeEnabled(false);
			System.out.println("Fill only");
		}
	});
	//add buttons to panel
	borderOptions.add(Box.createRigidArea(new Dimension(ToolbarButton.TOOL_BUTTON_GAP,
	                                                    ToolbarButton.TOOL_BUTTON_GAP)));
	borderOptions.add(borderOnly);
	borderOptions.add(Box.createRigidArea(new Dimension(ToolbarButton.TOOL_BUTTON_GAP, ToolbarButton.TOOL_BUTTON_GAP)));
	borderOptions.add(borderWithFill);
	borderOptions.add(Box.createRigidArea(new Dimension(ToolbarButton.TOOL_BUTTON_GAP, ToolbarButton.TOOL_BUTTON_GAP)));
	borderOptions.add(fillOnly);
	borderOptions.add(Box.createRigidArea(new Dimension(ToolbarButton.TOOL_BUTTON_GAP, ToolbarButton.TOOL_BUTTON_GAP)));
	
	//add to button group
	borderOptionButtons.add(borderOnly);
	borderOptionButtons.add(borderWithFill);
	borderOptionButtons.add(fillOnly);
	
	mainPanel.add(borderOptions, BorderLayout.WEST);
	JPanel widthPanel = new JPanel();
	widthPanel.setLayout(new BoxLayout(widthPanel, BoxLayout.X_AXIS));
	widthPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Border Width"));
	widthPanel.add(widthLabel);
	widthPanel.add(widthSlider);
	mainPanel.add(widthPanel);
	
	mainPanel.add(Box.createRigidArea(new Dimension(ToolbarButton.TOOL_BUTTON_GAP, ToolbarButton.TOOL_BUTTON_GAP)));
	
	
	//set number of sides
	sidesChangerSlider = new JSlider(SwingConstants.HORIZONTAL, MINIMUM_POSSIBLE_NUMBER_OF_SIDES,
	                                 MAX_ALLOWED_NUMBER_OF_SIDES, MINIMUM_POSSIBLE_NUMBER_OF_SIDES
	);
	setNumberOfSides(MINIMUM_POSSIBLE_NUMBER_OF_SIDES);
	
	sidesChangerSlider.setMajorTickSpacing(1);
	sidesChangerSlider.setPaintTicks(false);
	sidesChangerSlider.setPaintLabels(true);
	sidesChangerSlider.setSnapToTicks(true);
	
	//labe max value with text "circle"
	Hashtable<Integer, JLabel> sideLengthLabels = new Hashtable<>();
	//add labels for numerical values
	for (int i = MINIMUM_POSSIBLE_NUMBER_OF_SIDES; i < MAX_ALLOWED_NUMBER_OF_SIDES; i++)
		sideLengthLabels.put(i, new JLabel(Integer.toString(i)));
	sideLengthLabels.put(MAX_ALLOWED_NUMBER_OF_SIDES, new JLabel("    Circle"));
	sidesChangerSlider.setLabelTable(sideLengthLabels);
	//add listener
	sidesChangerSlider.addChangeListener(e -> setNumberOfSides(sidesChangerSlider.getValue()));
	JPanel sidesChangerPanel = new JPanel();
	sidesChangerPanel.setLayout(new BoxLayout(sidesChangerPanel, BoxLayout.X_AXIS));
	sidesChangerPanel.setBorder(BorderFactory.createEtchedBorder());
	JLabel sidesLabel = new JLabel("Sides:");
	sidesLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
	sidesChangerPanel.add(sidesLabel);
	sidesChangerPanel.add(sidesChangerSlider);
	mainPanel.add(sidesChangerPanel);
	
	
	upperCard.add(mainPanel);
	
	//upperCard.add(sidesChangerPanel);
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
	numberOfSides = n;
	if (numberOfSides < MINIMUM_POSSIBLE_NUMBER_OF_SIDES)
		numberOfSides = MINIMUM_POSSIBLE_NUMBER_OF_SIDES;
}

@Override public void draw(int x, int y, int color, Canvas.DrawMode drawMode) {
	model.setPixel(x, y, color, drawMode);
}

@Override public void drawBrush(int x, int y, int color, Canvas.DrawMode drawMode) {
	makeCircle(x, y, color, getStrokeWidth(), false, drawMode);
}

private void drawShape(int originX, int originY, int currentX, int currentY, int type) {
	//calculate values based on current and past mouse positions
	int w = currentX - originX; //relative x coordinate to originX
	int h = currentY - originY; //relative y coordinate to originY
	int radius = (int) Math.sqrt((w * w) + (h * h)); //distance between points
	//calculate angle offset from horizontal line so first point of polygon matches current location of pointer
	double offset = Math.atan2(h, w);
	
	if (type == MAX_ALLOWED_NUMBER_OF_SIDES) { //draw circle if slide is at this position
		//draw a circle - use approximation with large number of sidess
		makeRegularPolygon(
			  originX, originY, SIDES_IN_A_CIRCLE, radius, offset, strokeColor, true, Canvas.DrawMode.USE_OVERLAY);
	} else if (type >= MINIMUM_POSSIBLE_NUMBER_OF_SIDES && type < MAX_ALLOWED_NUMBER_OF_SIDES) {
		//draw a polygon
		makeRegularPolygon(originX, originY, type, radius, offset, strokeColor, true, Canvas.DrawMode.USE_OVERLAY);
	} else return; //else dont draw because input value is too high or too low
	
	//fill in the shape; dont fill if radius is too small
	if (shouldFill && radius > 1) {
		fill(originX, originY, getFillColor(), Canvas.DrawMode.USE_OVERLAY);
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
