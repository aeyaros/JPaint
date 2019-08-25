package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class ToolPaintBrush extends ToolPencil {
private final int MIN_RADIUS = 0;
private final int DEFAULT_RADIUS = 8;
private final int MAX_RADIUS = 24;
private int radius;
private JLabel radiusLabel;
private JSlider radiusSlider;

ToolButton triangleButton;
ToolButton squareButton;

//this is here so eraser has ability to not use overlay when using the triangle or square brushes
boolean useOverlayForBrushFill;

private enum SelectedBrush {CIRCLE, TRIANGLE, SQUARE}

private SelectedBrush selectedBrush;

ToolPaintBrush(ImageModel model, String iconSource) {
	super(model, iconSource);
	useOverlayForBrushFill = true; //set to true for paintbrush
	
	//set up upper card
	upperCard.removeAll();
	upperCard.setLayout(new GridLayout(1, 0));
	
	//brush radius slider
	radiusSlider = new JSlider(SwingConstants.HORIZONTAL, MIN_RADIUS, MAX_RADIUS, DEFAULT_RADIUS);
	radiusLabel = new JLabel(Integer.toString(radiusSlider.getValue()), SwingConstants.CENTER);
	radiusSlider.addChangeListener(e -> setRadius(radiusSlider.getValue()));
	radiusSlider.setMinimumSize(new Dimension(64, 24));
	radiusSlider.setPreferredSize(radiusSlider.getMinimumSize());
	radiusSlider.setSize(radiusSlider.getMinimumSize());
	radiusSlider.setMinorTickSpacing(1);
	radiusSlider.setSnapToTicks(true);
	radiusSlider.setPaintTicks(true);
	
	JPanel radiusPanel = new JPanel();
	radiusPanel.setLayout(new BoxLayout(radiusPanel, BoxLayout.X_AXIS));
	radiusPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Brush Radius"));
	
	radiusPanel.add(radiusLabel);
	radiusPanel.add(radiusSlider);
	
	setRadius(DEFAULT_RADIUS);
	
	upperCard.add(radiusPanel);
	
	
	//brush shapes
	JPanel shapeButtons = new JPanel();
	shapeButtons.setLayout(new BoxLayout(shapeButtons, BoxLayout.X_AXIS));
	ButtonGroup shapeButtonGroup = new ButtonGroup();
	ToolButton circleButton = new ToolButton("icons/brush_tool_icons/brushcircle.png");
	circleButton.addItemListener(e -> {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			selectedBrush = SelectedBrush.CIRCLE;
			System.out.println("Circle brush");
		}
	});
	
	triangleButton = new ToolButton("icons/brush_tool_icons/brushtriangle.png");
	triangleButton.addItemListener(e -> {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			selectedBrush = SelectedBrush.TRIANGLE;
			System.out.println("Triangle brush");
		}
	});
	squareButton = new ToolButton("icons/brush_tool_icons/brushsquare.png");
	squareButton.addItemListener(e -> {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			selectedBrush = SelectedBrush.SQUARE;
			System.out.println("Square brush");
		}
	});
	
	shapeButtons.add(Box.createRigidArea(new Dimension(ToolButton.TOOL_BUTTON_GAP, ToolButton.TOOL_BUTTON_GAP)));
	shapeButtons.add(circleButton);
	shapeButtons.add(Box.createRigidArea(new Dimension(ToolButton.TOOL_BUTTON_GAP, ToolButton.TOOL_BUTTON_GAP)));
	shapeButtons.add(triangleButton);
	shapeButtons.add(Box.createRigidArea(new Dimension(ToolButton.TOOL_BUTTON_GAP, ToolButton.TOOL_BUTTON_GAP)));
	shapeButtons.add(squareButton);
	shapeButtons.add(Box.createRigidArea(new Dimension(ToolButton.TOOL_BUTTON_GAP, ToolButton.TOOL_BUTTON_GAP)));
	
	shapeButtonGroup.add(circleButton);
	shapeButtonGroup.add(triangleButton);
	shapeButtonGroup.add(squareButton);
	
	//initially, brush is circle
	selectedBrush = SelectedBrush.CIRCLE;
	circleButton.setSelected(true);
	
	upperCard.add(shapeButtons);
}

private void setRadius(int r) {
	radius = r;
	radiusLabel.setText(Double.toString((radius * 2 + 1) / 2d));
}

@Override public void drawBrush(int x, int y, int color) {
	switch (selectedBrush) {
		case CIRCLE:
			makeCircle(x, y, color, radius, false); break;
		case TRIANGLE:
			makeRegularPolygon(x, y, 3, radius, Math.PI / 2d, color, false);
			fill(x, y, color, useOverlayForBrushFill); break;
		case SQUARE:
			makeRegularPolygon(x, y, 4, radius, Math.PI / 2d, color, false);
			fill(x, y, color, useOverlayForBrushFill); break;
		default:
			break;
	}
	
}

}