package com.jpaint;

import javax.swing.*;

public class ToolEraser extends ToolPaintBrush {
private boolean eraseToTransparent;
private final int ERASER_COLOR = new Color(255, 0, 0, 0).getARGB();

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

//eraser is unusual; it gets to draw to main without any alpha blending,
// and uses its own function for setting pixels
// it draws the cursor just like the paintbrush though, so we need this if statement
@Override public void draw(int x, int y, int color, Canvas.DrawMode drawMode) {
	if (drawMode == Canvas.DrawMode.USE_CURSOR) model.setPixel(x, y, color, drawMode);
	else model.erasePixel(x, y, eraseToTransparent);
}


@Override public void drawCursor(int x, int y, int color) {
	model.clearCanvasCursor();
	drawBrush(x, y, ERASER_COLOR, Canvas.DrawMode.USE_CURSOR);
	model.refreshCanvasCursor();
}

}
