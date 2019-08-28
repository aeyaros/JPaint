package com.jpaint;

import javax.swing.*;

public class ToolEraser extends ToolPaintBrush {
private boolean eraseToTransparent;
private final int BLACK_COLOR = new Color(255, 0, 0, 0).getARGB();
private final int WHITE_COLOR = new Color(255, 255, 255, 255).getARGB();
private final int MINIMUM_RADIUS_FOR_NICER_CURSOR = 4;

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
	
	mainPanel.add(eraserButtonPanel);
	
	//set up as a normal eraser
	setEraseToTransparent(false);
}

@Override void onButtonSelect() {
	if (radius < MINIMUM_RADIUS_FOR_NICER_CURSOR) model.updateSwingCursor(getCrossHairCursor());
	else model.updateSwingCursor(getBlankCursor());
}

private void setEraseToTransparent(boolean eraseToTransparent) {
	this.eraseToTransparent = eraseToTransparent;
}

//eraser is unusual; it gets to draw to main without any alpha blending,
// and uses its own function for setting pixels
// it draws the cursor just like the paintbrush though, so we need this if statement
@Override public void draw(int x, int y, int color, Canvas.DrawMode drawMode) {
	if (drawMode == Canvas.DrawMode.USE_CURSOR) model.setPixel(x, y, color, drawMode);
	else model.erasePixel(x, y, eraseToTransparent);
}


@Override public void drawCursor(int x, int y, int color) {
	model.clearCanvasCursor();
	drawEraserCursor(x, y);
	model.refreshCanvasCursor();
}

//special function for eraser; draws a black outline and a white filling
private void drawEraserCursor(int x, int y) {
	if (radius < MINIMUM_RADIUS_FOR_NICER_CURSOR) {
		drawBrush(x, y, BLACK_COLOR, Canvas.DrawMode.USE_CURSOR);
		model.updateSwingCursor(getCrossHairCursor());
	} else {
		model.updateSwingCursor(getBlankCursor());
		switch (selectedBrush) {
			case CIRCLE:
				makeRegularPolygon(
					  x, y, 32, radius, 3 * Math.PI / 2.0d, BLACK_COLOR, false, Canvas.DrawMode.USE_CURSOR);
				fill(x, y, WHITE_COLOR, Canvas.DrawMode.USE_CURSOR);
				break;
			case TRIANGLE:
				makeRegularPolygon(x, y, 3, radius, 3 * Math.PI / 2.0d, BLACK_COLOR, false,
				                   Canvas.DrawMode.USE_CURSOR
				                  );
				fill(x, y, WHITE_COLOR, Canvas.DrawMode.USE_CURSOR);
				break;
			case SQUARE:
				makeRegularPolygon(x, y, 4, radius, Math.PI / 4.0d, BLACK_COLOR, false, Canvas.DrawMode.USE_CURSOR);
				fill(x, y, WHITE_COLOR, Canvas.DrawMode.USE_CURSOR);
				break;
			default:
				break;
		}
	}
}

}
