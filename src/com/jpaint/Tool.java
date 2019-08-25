package com.jpaint;

import javax.swing.*;
import java.awt.event.MouseEvent;
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
	button = new ToolButton(iconSource);
	
	
	//button = new JToggleButton();
	//button.setOpaque(true);
	//button.setBorderPainted(true);
	//button.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	//button.setSize(WindowApplication.TOOL_BUTTON_SIZE, WindowApplication.TOOL_BUTTON_SIZE);
	
	/*
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
	*/
	/*
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
	*/
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

int getAlternateColor(int currentMouseEventCode) {
	switch (currentMouseEventCode) {
		case MouseEvent.BUTTON2: //if middle
		case MouseEvent.BUTTON3: //or if right
			return colorsInts[0]; //use left
		default: //if left
			return colorsInts[2]; //use right
	}
}

/*====== DRAWING TOOLS ======*/

//fill an area with a replacement color
void fill(int x, int y, int replacement, boolean useOverlay) {
	//Implementing a Flood-fill algorithm from Wikipedia
	//https://en.wikipedia.org/wiki/Flood_fill
	
	//get current width and height
	highX = model.getWidth();
	highY = model.getHeight();
	
	if (!model.isInBounds(x, y)) return; //cancel if point out of bounds of canvas
	
	int target; //color to try and fill in
	try { //color of spot clicked
		if (useOverlay) target = model.getOverlayPixel(x, y);
		else target = model.getPixel(x, y);
	} catch (IndexOutOfBoundsException exc) {
		System.err.println("Tried to access out of bounds pixel when filling w/ paint bucket");
		//exc.printStackTrace();
		return;
	}
	
	if (target == replacement) return; //return if target = replacement
	
	draw(x, y, target); //draw at coordinate
	ArrayDeque<Node> nodes = new ArrayDeque<>(); //create empty queue
	nodes.addLast(new Node(x, y)); //add initial node to queue
	
	while (!nodes.isEmpty()) { //while queue not empty
		Node n = new Node(
			  nodes.removeFirst());//pop n from queue
		//if color to north, south, east, or west of n is target color,
		//then set that node to replacement and add it to the queue
		for (int i = 0; i < 4; i++) {
			try {
				if (n.get(i).c(useOverlay) == target) {
					n.get(i).set(replacement);
					nodes.addLast(n.get(i));
				}
			} catch (IndexOutOfBoundsException ignored) { }
		}
	} //loop until queue empty again
}

//Node object for flood algorithm
private class Node {
	private int x;
	private int y;
	
	Node(int X, int Y) {
		x = X;
		y = Y;
	} //new node
	
	Node(Node n) {
		x = n.x();
		y = n.y();
	} //deep copy
	
	int x() { return x; }
	
	int y() { return y; }
	
	int c(boolean useOverlay) { //get color of node
		if (useOverlay) return model.getOverlayPixel(x, y);
		else return model.getPixel(x, y);
	}
	
	void set(int c) { draw(x, y, c); } //set color of node
	
	Node get(int d) { //get a node by direction
		switch (d) { //return node if it is in bounds, otherwise break and throw
			case 0: //NORTH
				if (y + 1 < highY) return new Node(x, y + 1);
				else break;
			case 1: //SOUTH
				if (y - 1 >= lowY) return new Node(x, y - 1);
				else break;
			case 2: //EAST
				if (x - 1 >= lowX) return new Node(x - 1, y);
				else break;
			case 3: //WEST
				if (x + 1 < highX) return new Node(x + 1, y);
				else break;
			default:
				break;//if unrecognized char, throw exception anyway
		}
		throw new IndexOutOfBoundsException(); //if here, out of bounds
	}
}

/* ====== GEOMETRIC DRAWING TOOLS ======
 * Draw a line, a circle, or a polygon. At each point on the line or curve,
 * we can use the draw function (draw a single pixel) or the drawbrush function
 * (to draw, at that point, a shape, such as a circle or triangle)
 *
 * So basically, if inside the drawBrush function, set useBrush to false
 * since in that function we are deciding what we want to draw
 * directly to the canvas at a given point
 */


//source of algorithm:
//https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
//version that accounts for x and y error; a Java implementation
//only a limited amount of mouse events are actually captured
//this results in a row of dots on the canvas
//this is solved by drawing a line from the current dot to the previous dot
void bresenham(int x0, int y0, int x1, int y1, int color, boolean useBrush) {
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
		if (useBrush) drawBrush(x0, y0, color);
		else draw(x0, y0, color);
	}
}


//"brush" functions
void makeCircle(int origX, int origY, int color, int radius, boolean useBrush) {
	for (int y = -radius; y <= radius; y++) {
		for (int x = -radius; x <= radius; x++) {
			if (x * x + y * y <= radius * radius) //draw if inside bounds of circle
				if (useBrush) drawBrush(origX + x, origY + y, color);
				else draw(origX + x, origY + y, color);
		}
	}
	model.refreshView();
}

void makeRegularPolygon(int x, int y, int sides, int radius, double offset, int color, boolean useBrush) {
	int[][] points = getPolyPoints(x, y, sides, radius, offset);
	//draw several lines, using either a brush or not
	for (int i = 0; i < points.length; i++) {
		bresenham(points[i][0], points[i][1],
		          points[(i + 1) % points.length][0],
		          points[(i + 1) % points.length][1],
		          color, useBrush
		         );
	}
	
}

//get points of a regular polygon; lines will be drawn from point to point
private int[][] getPolyPoints(int originX, int originY, int sides, int radius, double angleOffset) {
	int[][] points = new int[sides][2];//holds return values
	double theta = (2d * Math.PI) / (double) sides;
	double currentAngle;
	for (int i = 0; i < sides; i++) {
		currentAngle = (theta * i) + angleOffset;
		points[i][0] = (int) (radius * Math.cos(currentAngle)) + originX;
		points[i][1] = (int) (radius * Math.sin(currentAngle)) + originY;
	}
	return points;
}


void setCursor() {

}


}
