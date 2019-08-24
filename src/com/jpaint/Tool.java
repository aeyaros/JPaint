package com.jpaint;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;

//Tool: This is a tool button - subclasses contain specific tool functionality
abstract class Tool implements ToolInput {
ImageModel model; //the model class this tool acts on
JPanel upperCard; //the upper panel shown when this tool is selected
JToggleButton button; //the tool button
private int[] colorsInts; //the colors available to draw with

//used by some tools
//boundaries of canvas
private final int lowX = 0;
private final int lowY = 0;
private int highX;
private int highY;

//set button name and add the model
Tool(ImageModel model, String iconSource) {
	super();
	this.model = model;
	colorsInts = new int[3];
	
	//panel with top controls - initialize
	//you can do whatever you want with this in the derived tool classes
	upperCard = new JPanel();
	
	//button for toolbar
	button = new JToggleButton();
	button.setOpaque(true);
	//button.setContentAreaFilled(false);
	button.setBorderPainted(true);
	button.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	button.setSize(WindowApplication.TOOL_BUTTON_SIZE, WindowApplication.TOOL_BUTTON_SIZE);
	
	try { //get icon of button
		Image image = ImageIO.read(getClass().getResource(iconSource));
		image = image.getScaledInstance(WindowApplication.TOOL_BUTTON_SIZE, WindowApplication.TOOL_BUTTON_SIZE,
		                                Image.SCALE_SMOOTH
		                               );
		button.setIcon(new ImageIcon(image));
		button.setBackground(SystemColor.window);
		//get the same image for the selected state, but invert the colors
		BufferedImage selectedImage = ImageIO.read(getClass().getResource(iconSource));
		for (int i = 0; i < selectedImage.getWidth(); i++) {
			for (int j = 0; j < selectedImage.getHeight(); j++) {
				Color c = new Color(selectedImage.getRGB(i, j));
				for (int k = 1; k < 4; k++) {
					c.setChannel(k, 255 - c.getChannel(k));
				}
				selectedImage.setRGB(i, j, c.getARGB());
			}
		}
		button.setSelectedIcon(new ImageIcon(
			  selectedImage.getScaledInstance(
					WindowApplication.TOOL_BUTTON_SIZE, WindowApplication.TOOL_BUTTON_SIZE, Image.SCALE_SMOOTH)));
	} catch (Exception e) {
		e.printStackTrace();
		System.err
			  .println("Couldn't load icon for " + this.getClass().getName() + " button from location " + iconSource);
	}
	
	button.addItemListener(e -> {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			//set border
			button.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, SystemColor.controlLtHighlight,
			                                                  SystemColor.controlShadow
			                                                 ));
			//set background
			button.setBackground(SystemColor.controlHighlight);
		} else if (e.getStateChange() == ItemEvent.DESELECTED) {
			//set border
			button.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			//set background
			button.setBackground(SystemColor.window);
			
		}
	});
}

void updateColors(Color left, Color middle, Color right) {
	colorsInts[0] = left.getARGB();
	colorsInts[1] = middle.getARGB();
	colorsInts[2] = right.getARGB();
}

int getColorIntByButton(int mouseEventButtonCode) {
	switch (mouseEventButtonCode) {
		case MouseEvent.BUTTON2:
			return colorsInts[1];
		case MouseEvent.BUTTON3:
			return colorsInts[2];
		default:
			return colorsInts[0]; //MouseEvent.BUTTON1
	}
}

/*====== DRAWING TOOLS ======*/

void fill(int x, int y, int buttonCode) {
	//Implementing a Flood-fill algorithm from Wikipedia
	//https://en.wikipedia.org/wiki/Flood_fill
	
	//get current width and height
	highX = model.getWidth();
	highY = model.getHeight();
	
	if (!model.isInBounds(x, y)) return; //cancel if point out of bounds of canvas
	
	int target; //color to try and fill in
	
	try {
		target = model.getColorAtPixel(x, y).getARGB(); //color of spot clicked
	} catch (IndexOutOfBoundsException exc) {
		System.err.println("Tried to access out of bounds pixel when filling w/ paint bucket");
		//exc.printStackTrace();
		return;
	}
	
	int replacement = getColorIntByButton(buttonCode); //color selected by user
	
	//no need to save state unless there's actually something for us to do
	if (target == replacement) return; //return if target = replacement
	
	//if we made it here, then we can do the fill; save state
	model.saveCurrentState();
	
	draw(x, y, target); //draw at coordinate
	ArrayDeque<Node> nodes = new ArrayDeque<>(); //create empty queue
	nodes.addLast(new Node(x, y)); //add initial node to queue
	
	while (nodes.size() > 0) { //while queue not empty
		Node n = new Node(
			  nodes.removeFirst());//pop n from queue
		//if color to north, south, east, or west of n is target color,
		//then set that node to replacement and add it to the queue
		char[] dirs = {'N', 'S', 'E', 'W'};
		for (char dir: dirs) {
			try {
				if (n.get(dir).c() == target) {
					n.get(dir).set(replacement);
					nodes.addLast(n.get(dir));
				}
			} catch (IndexOutOfBoundsException ignored) { }
		}
	} //loop until queue empty again
	model.refreshView();
}

//Node object for flood algorithm
private class Node {
	int x;
	int y;
	
	Node(int X, int Y) { x = X; y = Y; } //new node
	Node(Node n) { x = n.x; y = n.y; } //deep copy
	int c() { return model.getColorAtPixel(x, y).getARGB(); } //get color of node
	void set(int c) { draw(x, y, c); } //set color of node
	
	Node get(char c) { //get a node by direction character
		switch (c) { //return node if it is in bounds, otherwise break and throw
			case 'N':
				if (y + 1 < highY) return new Node(x, y + 1);
				else break;
			case 'S':
				if (y - 1 >= lowY) return new Node(x, y - 1);
				else break;
			case 'E':
				if (x - 1 >= lowX) return new Node(x - 1, y);
				else break;
			case 'W':
				if (x + 1 < highX) return new Node(x + 1, y);
				else break;
			default:
				break;//if unrecognized char, throw exception anyway
		} throw new IndexOutOfBoundsException(); //if here, out of bounds
	}
}

//source of algorithm:
//https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
//version that accounts for x and y error; a Java implementation
//only a limited amount of mouse events are actually captured
//this results in a row of dots on the canvas
//this is solved by drawing a line from the current dot to the previous dot
void bresenham(int x0, int y0, int x1, int y1, int color) {
	int dx = Math.abs(x1 - x0);
	int sx = -1;
	if (x0 < x1) sx = 1;
	
	int dy = -1 * Math.abs(y1 - y0);
	int sy = -1;
	if (y0 < y1) sy = 1;
	
	int err = dx + dy; //error value ex_y
	int e2;
	
	while (x0 != x1 || y0 != y1) { //until we reach the current point
		//calculate next point on line towards current point
		// and update past coordinates (x0, y0) to that point
		e2 = 2 * err;
		if (e2 >= dy) {
			err += dy; // e_xy + e_x > 0
			x0 += sx;
		}
		if (e2 <= dx) { // e_xy + e_y < 0
			err += dx;
			y0 += sy;
		}
		
		//then draw at that point
		draw(x0, y0, color);
	}
}

void makeCircle(int origX, int origY, int color, int radius, int negativeRadius, boolean blend, boolean useOverlay) {
	for (int y = negativeRadius; y <= radius; y++) {
		for (int x = negativeRadius; x <= radius; x++) {
			if (x * x + y * y <= radius * radius) //draw if inside bounds of circle
				model.setPixel(origX + x, origY + y, color, blend, useOverlay);
		}
	}
	model.refreshView();
}

void generateTriangle() {
	int[][] points = getPolyPoints(200, 200, 5, 10, -90);
	for (int i = 0; i < points.length; i++) {
		bresenham(
			  points[i][0],
			  points[i][1],
			  points[(i + 1) % points.length][0],
			  points[(i + 1) % points.length][1],
			  new Color(255, 0, 0, 0).getARGB()
		         );
		System.out.println("---" + points[i][0] + " " + points[i][1]);
	}
	
}

private int[][] getPolyPoints(int originX, int originY, int sides, int radius, int angleOffset) {
	int[][] points = new int[sides][2];//holds return values
	double initialAngle = angleOffset * Math.PI / 180d;
	double theta = (2d * Math.PI) / (double) sides;
	double currentAngle;
	for (int i = 0; i < sides; i++) {
		currentAngle = (theta * i) + initialAngle;
		points[i][0] = (int) (radius * Math.cos(currentAngle)) + originX;
		points[i][1] = (int) (radius * Math.sin(currentAngle)) + originY;
	} return points;
}


void setCursor() {

}


}
