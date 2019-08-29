package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class ToolColorPicker extends Tool {
private ManageColors manageColors;
private CardLayout cards;
private JPanel coordinates;
private JLabel[] ARGB;
private String[] labelPrefixes = {"Opacity: ", "Red: ", "Green: ", "Blue: "};

ToolColorPicker(ImageModel model, String iconSource) {
	super(model, iconSource);
	//set up upper card
	cards = new CardLayout();
	upperCard = new JPanel(cards);
	
	//add coordinates panel with grid layout
	coordinates = new JPanel(new GridLayout(1, 4));
	upperCard.add(coordinates, 0);
	
	//add panel to be shown when not hovering over the image
	JLabel infoText = new JLabel("Hover over the image, then click to pick a color.");
	infoText.setOpaque(true);
	infoText.setBackground(SystemColor.window);
	infoText.setHorizontalAlignment(SwingConstants.CENTER);
	infoText.setVerticalAlignment(SwingConstants.CENTER);
	upperCard.add(infoText);
	
	//set up labels showing the coordinates
	ARGB = new JLabel[4];
	for (int i = 0; i < ARGB.length; i++) {
		ARGB[i] = new JLabel("");
		ARGB[i].setOpaque(true);
		ARGB[i].setHorizontalAlignment(SwingConstants.CENTER);
		ARGB[i].setVerticalAlignment(SwingConstants.CENTER);
		coordinates.add(ARGB[i]);
	}
	clearStatus();
}
@Override void onButtonSelect() {
	model.updateSwingCursor(getCustomCursor(
		  "Colorpicker cursor", "icons/cursor_icons/colorPickerCursor.png",
			"icons/cursor_icons/colorPickerCursor32.png",
			"icons/cursor_icons/colorPickerCursor64.png",
			0, 15, 0,31,0,63));
}

void addColorManager(ManageColors manageColors) {
	this.manageColors = manageColors;
}

private Color getColorAtPixel(int x, int y) {
	return new Color(model.getPixel(x, y, Canvas.DrawMode.USE_MAIN));
}

private void updateStatus(int x, int y) {
	if (model.isInBounds(x, y)) {
		//set background to selected color
		try {
			Color c = getColorAtPixel(x, y);
			for (int i = 0; i < ARGB.length; i++) {
				ARGB[i].setBackground(c.getAWT());
				//invert color of text if color is too dark
				if (Color.isDarkColor(c)) ARGB[i].setForeground(java.awt.Color.white);
				else ARGB[i].setForeground(java.awt.Color.black);
				ARGB[i].setText(labelPrefixes[i] + c.getChannel(i));
			}
			cards.first(upperCard);
			//System.out.println(x + ", " + y + " r: " + c.getChannel(1) + " g: " + c.getChannel(2) + " b: " + c
			// .getChannel(3) + " a: " + c.getChannel(0));
		} catch (Exception e) {
			System.err.println("Tried to access out of bounds pixel");
			e.printStackTrace();
		}
	}
}

private void setColor(int x, int y, int buttonCode) {
	if (model.isInBounds(x, y)) {
		Color c = getColorAtPixel(x, y);
		manageColors.setButtonColor(c, buttonCode);
	}
}

private void clearStatus() {
	for (JLabel l : ARGB) {
		l.setText("");
		l.setBackground(java.awt.Color.white);
	}
	cards.last(upperCard);
	//System.out.print("Cleared!");
}

@Override
public void toolDragged(MouseEvent e) {
	updateStatus(e.getX(), e.getY());
	setColor(e.getX(), e.getY(), e.getButton());
	model.clearCanvasCursor();
	drawCursor(e.getX(), e.getY(), getColorIntByButton(e.getButton()));
}

@Override
public void toolMoved(MouseEvent e) {
	updateStatus(e.getX(), e.getY());
	model.clearCanvasCursor();
	drawCursor(e.getX(), e.getY(), getColorIntByButton(e.getButton()));
}

@Override
public void toolClicked(MouseEvent e) {
	updateStatus(e.getX(), e.getY());
}

@Override
public void toolPressed(MouseEvent e) {
	updateStatus(e.getX(), e.getY());
	setColor(e.getX(), e.getY(), e.getButton());
}

@Override
public void toolReleased(MouseEvent e) {
	updateStatus(e.getX(), e.getY());
}

@Override
public void toolEntered(MouseEvent e) {
	updateStatus(e.getX(), e.getY());
	model.clearCanvasCursor();
	drawCursor(e.getX(), e.getY(), getColorIntByButton(e.getButton()));
}

@Override
public void toolExited(MouseEvent e) {
	clearStatus();
	model.clearCanvasCursor();
}
@Override public void draw(int x, int y, int color, Canvas.DrawMode drawMode) {

}
@Override public void drawBrush(int x, int y, int color, Canvas.DrawMode drawMode) {

}
@Override public void drawCursor(int x, int y, int color) {
	///////////////////////////todo: this method...
}

@Override public void toolKeyPressed(KeyEvent e) { }
@Override public void toolKeyReleased(KeyEvent e) { }
@Override public void toolKeyTyped(KeyEvent e) { }
}
