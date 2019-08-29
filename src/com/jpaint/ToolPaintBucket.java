package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class ToolPaintBucket extends Tool {

ToolPaintBucket(ImageModel model, String iconSource) {
	super(model, iconSource);
	//set up upper card
	upperCard.setLayout(new CardLayout());
	//set up upper card
	JLabel infoText = new JLabel("Click to fill part of the image with a selected color.");
	infoText.setHorizontalAlignment(SwingConstants.CENTER);
	infoText.setVerticalAlignment(SwingConstants.CENTER);
	upperCard.add(infoText, 0);
}
@Override void onButtonSelect() {
	model.updateSwingCursor(getCustomCursor(
		  "Paintbucket", "icons/cursor_icons/paintBucketCursor.png",
			"icons/cursor_icons/paintBucketCursor32.png",
			"icons/cursor_icons/paintBucketCursor64.png",
			13, 13, 28,28, 56,58));
}

@Override
public void draw(int x, int y, int color, Canvas.DrawMode drawMode) {
	model.setPixel(x, y, color, Canvas.DrawMode.USE_MAIN);
}

@Override public void drawBrush(int x, int y, int color, Canvas.DrawMode drawMode) {
	model.setPixel(x, y, color, Canvas.DrawMode.USE_MAIN);
}

@Override public void toolReleased(MouseEvent e) {
	model.saveCurrentState();
	fill(e.getX(), e.getY(), getColorIntByButton(e.getButton()), Canvas.DrawMode.USE_MAIN);
	model.refreshView();
}

@Override public void drawCursor(int x, int y, int color) { }
@Override public void toolClicked(MouseEvent e) { }
@Override public void toolPressed(MouseEvent e) { }
@Override public void toolDragged(MouseEvent e) { }
@Override public void toolMoved(MouseEvent e) { }
@Override public void toolEntered(MouseEvent e) { }
@Override public void toolExited(MouseEvent e) { }
@Override public void toolKeyPressed(KeyEvent e) { }
@Override public void toolKeyReleased(KeyEvent e) { }
@Override public void toolKeyTyped(KeyEvent e) { }
}