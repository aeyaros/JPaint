package com.jpaint;

import javax.swing.*;
import java.awt.*;

public class ToolPaintBrush extends ToolPencil {
private final int MIN_RADIUS = 0;
private final int DEFAULT_RADIUS = 8;
private final int MAX_RADIUS = 24;
private int radius;
private JLabel radiusLabel;
private JSlider radiusSlider;

ToolPaintBrush(ImageModel model, String iconSource) {
	super(model, iconSource);
	
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
	ToolButton triangleButton = new ToolButton("icons/brush_tool_icons/brushtriangle.png");
	ToolButton squareButton = new ToolButton("icons/brush_tool_icons/brushsquare.png");
	
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
	
	upperCard.add(shapeButtons);
}

private void setRadius(int r) {
	radius = r;
	radiusLabel.setText(Double.toString((radius * 2 + 1) / 2d));
}

@Override public void drawBrush(int x, int y, int color) {
	makeCircle(x, y, color, radius, false);
}

}