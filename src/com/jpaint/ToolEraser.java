package com.jpaint;

import javax.swing.*;

public class ToolEraser extends ToolPaintBrush {
private boolean eraseToTransparent;

ToolEraser(ImageModel model, String iconSource) {
	super(model, iconSource);
	//issue with fill algorithm, so need to disable buttons
	triangleButton.setEnabled(false);
	squareButton.setEnabled(false);
	
	JRadioButton eraseWhite = new JRadioButton("White");
	eraseWhite.setSelected(true);
	JRadioButton eraseTrans = new JRadioButton("Transparent");
	eraseTrans.setSelected(false);
	
	eraseWhite.addActionListener(e -> setEraseToTransparent(false));
	eraseTrans.addActionListener(e -> setEraseToTransparent(true));
	
	ButtonGroup eraserButtons = new ButtonGroup();
	eraserButtons.add(eraseWhite);
	eraserButtons.add(eraseTrans);
	
	JPanel eraserButtonPanel = new JPanel();
	eraserButtonPanel.setLayout(new BoxLayout(eraserButtonPanel, BoxLayout.X_AXIS));
	eraserButtonPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Eraser type"));
	
	eraserButtonPanel.add(eraseWhite);
	eraserButtonPanel.add(eraseTrans);
	
	upperCard.add(eraserButtonPanel);
	
	//set up as a normal eraser
	setEraseToTransparent(false);
}

private void setEraseToTransparent(boolean eraseToTransparent) {
	this.eraseToTransparent = eraseToTransparent;
}

//@Override public void drawCursor(int x, int y, int color) { }

@Override public void draw(int x, int y, int color, Canvas.DrawMode drawMode) {
	model.erasePixel(x, y, eraseToTransparent);
	//eraser is unusual; it gets to draw to main without any alpha blending,
	// and uses its own function for setting pixels
}

@Override public void drawBrush(int x, int y, int color, Canvas.DrawMode drawMode) {
	switch (selectedBrush) {
		case CIRCLE:
			makeCircle(x, y, color, radius, false, Canvas.DrawMode.USE_MAIN);
			break;
		case TRIANGLE:
			makeRegularPolygon(x, y, 3, radius, 3 * Math.PI / 2d, color, false, Canvas.DrawMode.USE_MAIN);
			fill(x, y, color, drawMode);
			break;
		case SQUARE:
			makeRegularPolygon(x, y, 4, radius, Math.PI / 4d, color, false, Canvas.DrawMode.USE_MAIN);
			fill(x, y, color, drawMode);
			break;
		default:
			break;
	}
	
}

}
