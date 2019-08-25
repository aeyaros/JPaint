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

void addColorManager(ManageColors manageColors) {
	this.manageColors = manageColors;
}

private Color getColorAtPixel(int x, int y) {
	return new Color(model.getPixel(x, y));
}

private void updateStatus(int x, int y) {
	if (model.isInBounds(x, y)) {
		//set background to selected color
		try {
			Color c = getColorAtPixel(x, y);
			for (int i = 0; i < ARGB.length; i++) {
				ARGB[i].setBackground(c.getAWT());
				//invert color of text if color is too dark
				if (isDarkColor(c)) ARGB[i].setForeground(java.awt.Color.white);
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

//WC3 Brightness formula: ((Red value X 299) + (Green value X 587) + (Blue value X 114)) / 1000
private boolean isDarkColor(Color c) {
	//if low opacity, then it's a light color
	if (c.getChannel(0) < 128) return false;
	double r = c.getChannel(1) / Color.MAX_VALUE_FLOAT;
	double g = c.getChannel(2) / Color.MAX_VALUE_FLOAT;
	double b = c.getChannel(3) / Color.MAX_VALUE_FLOAT;
	return ((((r * 299d) + (g * 587d) + (b * 114d)) / 1000.0d) < 0.4);
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
}

@Override
public void toolMoved(MouseEvent e) {
	updateStatus(e.getX(), e.getY());
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
}

@Override
public void toolExited(MouseEvent e) {
	clearStatus();
}

@Override public void draw(int x, int y, int color) { }
@Override public void drawBrush(int x, int y, int color) { }
@Override public void toolKeyPressed(KeyEvent e) { }
@Override public void toolKeyReleased(KeyEvent e) { }
@Override public void toolKeyTyped(KeyEvent e) { }
}
