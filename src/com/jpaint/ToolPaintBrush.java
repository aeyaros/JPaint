package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class ToolPaintBrush extends ToolPencil {
private final int MIN_RADIUS = 0;
private final int DEFAULT_RADIUS = 8;
final int MAX_RADIUS = 64;
JPanel mainPanel;
ToolbarButton triangleButton;
ToolbarButton squareButton;
int radius;
private JLabel radiusLabel;
JSlider radiusSlider;
SelectedBrush selectedBrush;

ToolPaintBrush(ImageModel model, String iconSource) {
	super(model, iconSource);
	
	//set up upper card
	upperCard.removeAll();
	upperCard.setLayout(new GridLayout(1, 0));
	
	//panel holding everything
	mainPanel = new JPanel();
	mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
	
	//brush radius slider
	radiusSlider = new JSlider(SwingConstants.HORIZONTAL, MIN_RADIUS, MAX_RADIUS, DEFAULT_RADIUS);
	radiusSlider.addChangeListener(e -> setRadius(radiusSlider.getValue()));
	radiusSlider.setMinimumSize(new Dimension(96, 24));
	radiusSlider.setPreferredSize(radiusSlider.getMinimumSize());
	radiusSlider.setSize(radiusSlider.getMinimumSize());
	radiusSlider.setMinorTickSpacing(1);
	radiusSlider.setMajorTickSpacing(10);
	radiusSlider.setSnapToTicks(true);
	radiusSlider.setPaintTicks(true);
	
	radiusLabel = new JLabel(Integer.toString(radiusSlider.getValue()), SwingConstants.CENTER);
	radiusLabel.setPreferredSize(new Dimension(48, 24));
	radiusLabel.setMinimumSize(radiusLabel.getPreferredSize());
	radiusLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 3));
	
	
	JPanel radiusPanel = new JPanel();
	radiusPanel.setLayout(new BoxLayout(radiusPanel, BoxLayout.X_AXIS));
	radiusPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Brush width"));
	
	radiusPanel.add(radiusLabel);
	radiusPanel.add(radiusSlider);
	
	setRadius(DEFAULT_RADIUS);
	
	
	//brush shapes
	JPanel shapeButtons = new JPanel();
	shapeButtons.setLayout(new BoxLayout(shapeButtons, BoxLayout.X_AXIS));
	ButtonGroup shapeButtonGroup = new ButtonGroup();
	ToolbarButton circleButton = new ToolbarButton("icons/brush_tool_icons/brushcircle.png");
	circleButton.addItemListener(e -> {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			selectedBrush = SelectedBrush.CIRCLE;
			System.out.println("Circle brush");
		}
	});
	
	triangleButton = new ToolbarButton("icons/brush_tool_icons/brushtriangle.png");
	triangleButton.addItemListener(e -> {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			selectedBrush = SelectedBrush.TRIANGLE;
			System.out.println("Triangle brush");
		}
	});
	
	squareButton = new ToolbarButton("icons/brush_tool_icons/brushsquare.png");
	squareButton.addItemListener(e -> {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			selectedBrush = SelectedBrush.SQUARE;
			System.out.println("Square brush");
		}
	});
	
	shapeButtons.add(Box.createRigidArea(new Dimension(ToolbarButton.TOOL_BUTTON_GAP, ToolbarButton.TOOL_BUTTON_GAP)));
	shapeButtons.add(circleButton);
	shapeButtons.add(Box.createRigidArea(new Dimension(ToolbarButton.TOOL_BUTTON_GAP, ToolbarButton.TOOL_BUTTON_GAP)));
	shapeButtons.add(triangleButton);
	shapeButtons.add(Box.createRigidArea(new Dimension(ToolbarButton.TOOL_BUTTON_GAP, ToolbarButton.TOOL_BUTTON_GAP)));
	shapeButtons.add(squareButton);
	shapeButtons.add(Box.createRigidArea(new Dimension(ToolbarButton.TOOL_BUTTON_GAP, ToolbarButton.TOOL_BUTTON_GAP)));
	
	shapeButtonGroup.add(circleButton);
	shapeButtonGroup.add(triangleButton);
	shapeButtonGroup.add(squareButton);
	
	//initially, brush is circle
	selectedBrush = SelectedBrush.CIRCLE;
	circleButton.setSelected(true);
	
	//add stuff to main panel, and add main panel to upper card
	mainPanel.add(shapeButtons);
	mainPanel.add(radiusPanel);
	upperCard.add(mainPanel);
}

@Override void onButtonSelect() {
	model.updateSwingCursor(getBlankCursor());
}
private void setRadius(int r) {
	radius = r;
	radiusLabel.setText(Integer.toString((radius * 2 + 1)));
}

@Override public void drawBrush(int x, int y, int color, Canvas.DrawMode drawMode) {
	switch (selectedBrush) {
		case CIRCLE:
			makeCircle(x, y, color, radius, false, drawMode);
			break;
		case TRIANGLE:
			makeRegularPolygon(x, y, 3, radius, 3 * Math.PI / 2.0d, color, false, drawMode);
			fill(x, y, color, drawMode);
			break;
		case SQUARE:
			makeRegularPolygon(x, y, 4, radius, Math.PI / 4.0d, color, false, drawMode);
			fill(x, y, color, drawMode);
			break;
		default:
			break;
	}
	
}

enum SelectedBrush {CIRCLE, TRIANGLE, SQUARE}

}