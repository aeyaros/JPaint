package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

//ImageView: THIS IS THE MAIN VIEW CLASS
class ImageView extends JLabel {
private JLabel coordinatesLabel;
private JLabel sizeLabel;
private JLabel currentImage; //the main drawing
private JLabel imageOverlay; //the overlay on top of the drawing
private JLabel cursorOverlay; //the cursor being displayed
private int width;
private int height;
private boolean isInView; //used so that drag events which go off screen don't show out of bound coordinates

ImageView(int w, int h, JLabel coordinatesLabel, JLabel sizeLabel) {
	super();
	width = w;
	height = h;
	isInView = false;
	//set some properties
	this.setOpaque(true);
	this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	
	//initialize labels for bottom bar
	this.coordinatesLabel = coordinatesLabel;
	this.sizeLabel = sizeLabel;
	
	//initialize labels for canvases
	currentImage = new JLabel();
	imageOverlay = new JLabel();
	cursorOverlay = new JLabel();
	
	//update tileBG icon, and size label
	updateSize(w, h);
	
	//set up canvases
	currentImage.setLayout(new BoxLayout(currentImage, BoxLayout.X_AXIS));
	imageOverlay.setLayout(new BoxLayout(imageOverlay, BoxLayout.X_AXIS));
	
	this.add(currentImage);
	currentImage.add(imageOverlay);
	imageOverlay.add(cursorOverlay);
	
	//show mouse coordinates in a view
	this.addMouseMotionListener(new MouseMotionListener() {
		@Override
		public void mouseMoved(MouseEvent e) {
			refreshCoordinates(e.getX(), e.getY());
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			if (isInView) refreshCoordinates(e.getX(), e.getY());
		}
	});
	this.addMouseListener(new MouseListener() {
		@Override public void mouseEntered(MouseEvent e) {
			isInView = true;
		}
		
		//when pointer leaves canvas, display nothing
		@Override
		public void mouseExited(MouseEvent e) {
			isInView = false;
			clearCoordinates();
		}
		@Override public void mouseClicked(MouseEvent e) { }
		@Override public void mousePressed(MouseEvent e) { }
		@Override public void mouseReleased(MouseEvent e) { }
	});
}

//important - sets the base icon which needs to be a certain size
//otherwise the labels added to it wont show
void updateSize(int w, int h) {
	this.setIcon(new ImageIcon(Canvas.generateTileBG(w, h)));
	width = w;
	height = h;
	sizeLabel.setText("Width: " + w + ", Height: " + h);
}

//update the view
void refresh(ImageIcon baseIcon, ImageIcon overlayIcon) {
	//this is where I should overlay the canvas on the checkerboard
	currentImage.setIcon(baseIcon);
	imageOverlay.setIcon(overlayIcon);
	
	//if the image has new dimensions, update them
	if (baseIcon.getIconWidth() != width || baseIcon.getIconHeight() != height) {
		updateSize(baseIcon.getIconWidth(), baseIcon.getIconHeight());
	}
}

//set the Java/Swing cursor
void updateSwingCursor(Cursor cursor) {
	this.setCursor(cursor);
}

//refresh the canvas cursor
void refreshCanvasCursor(ImageIcon cursorOverlayIcon) {
	cursorOverlay.setIcon(cursorOverlayIcon);
}

private void refreshCoordinates(int x, int y) {
	coordinatesLabel.setText("x: " + x + "px, y: " + y + "px");
}

private void clearCoordinates() {
	coordinatesLabel.setText("");
}
}
