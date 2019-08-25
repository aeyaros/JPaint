package com.jpaint;

import javax.swing.*;

public class ToolEraser extends ToolPaintBrush {
private boolean eraseToTransparent;

ToolEraser(ImageModel model, String iconSource) {
	super(model, iconSource);
	//set this to false to override the paintbrush setting
	useOverlayForBrushFill = false; //when erasing with square or triangle brushes, write directly to canvas
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

@Override public void draw(int x, int y, int color) {
	model.erasePixel(x, y, eraseToTransparent);
}

}
