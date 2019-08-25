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
	
	radiusSlider = new JSlider(SwingConstants.HORIZONTAL, MIN_RADIUS, MAX_RADIUS, DEFAULT_RADIUS);
	radiusLabel = new JLabel(Integer.toString(radiusSlider.getValue()), SwingConstants.CENTER);
	radiusSlider.addChangeListener(e -> setRadius(radiusSlider.getValue()));
	
	JPanel radiusPanel = new JPanel();
	radiusPanel.setLayout(new BoxLayout(radiusPanel, BoxLayout.X_AXIS));
	radiusPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Brush Radius"));
	
	radiusPanel.add(radiusLabel);
	radiusPanel.add(radiusSlider);
	upperCard.add(radiusPanel);
	
	setRadius(DEFAULT_RADIUS);
}

private void setRadius(int r) {
	radius = r;
	radiusLabel.setText(Double.toString((radius * 2 + 1) / 2d));
}

@Override public void drawBrush(int x, int y, int color) {
	makeCircle(x, y, color, radius, false);
}

}