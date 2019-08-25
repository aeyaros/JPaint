package com.jpaint;

import java.awt.image.BufferedImage;

//Canvas: part of the model: contains an argb-integer image
class Canvas {

static final int WHITE_INT = (new Color(Color.MAX_VALUE, Color.MAX_VALUE, Color.MAX_VALUE, Color.MAX_VALUE)).getARGB();
static final int TRANSPARENT_INT =
	  (new Color(Color.MIN_VALUE, Color.MIN_VALUE, Color.MIN_VALUE, Color.MIN_VALUE)).getARGB();
private BufferedImage pixels;
private BufferedImage overlay;
private int width;
private int height;
private int defaultColor; //either white or transparent

/*====== CONSTRUCTORS ======*/
//create a new canvas
Canvas(int w, int h, boolean transparent) {
	if (transparent) defaultColor = TRANSPARENT_INT;
	else defaultColor = WHITE_INT;
	
	width = w;
	height = h;
	pixels = newBlankImage(w, h, defaultColor);
	overlay = newBlankImage(w, h, TRANSPARENT_INT);
}

//deep copy constructor
Canvas(Canvas oldCanvas) {
	defaultColor = TRANSPARENT_INT;
	width = oldCanvas.getWidth();
	height = oldCanvas.getHeight();
	pixels = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	for (int i = 0; i < width; i++) {
		for (int j = 0; j < height; j++) {
			pixels.setRGB(i, j, oldCanvas.pixels.getRGB(i, j));
		}
	}
	overlay = newBlankImage(width, height, TRANSPARENT_INT);
}

//canvas from a bufferedImage
Canvas(BufferedImage sourceImage) {
	defaultColor = TRANSPARENT_INT;
	width = sourceImage.getWidth();
	height = sourceImage.getHeight();
	pixels = sourceImage;
	overlay = newBlankImage(width, height, TRANSPARENT_INT);
}

//used for generating tiled backgrounds for translucent color
static BufferedImage generateTileBG(int w, int h) {
	final int squareSize = 4;
	
	int[] squareColors = {
		  new Color(255, 255, 255, 255).getARGB(),
		  new Color(255, 200, 200, 200).getARGB()
	};
	
	BufferedImage tiled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	
	//draw a checkerboard at a given size
	boolean startingColumnColor = false; //alternate starting color every 8 rows
	boolean use1; //alternate color used every 8 columns
	for (int i = 0; i < w; i++) {
		if (i % squareSize == 0) startingColumnColor = !startingColumnColor;
		use1 = startingColumnColor; //set use1 to starting color
		for (int j = 0; j < h; j++) {
			if (j % squareSize == 0) use1 = !use1;
			if (use1) tiled.setRGB(i, j, squareColors[1]);
			else tiled.setRGB(i, j, squareColors[0]);
		}
	}
	return tiled;
}

private BufferedImage newBlankImage(int w, int h, int baseColor) {
	BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	for (int i = 0; i < w; i++) {
		for (int j = 0; j < h; j++) {
			newImage.setRGB(i, j, baseColor);
		}
	}
	return newImage;
}

void clearAll() {
	pixels = newBlankImage(width, height, defaultColor);
	clearOverlay();
}

void clearOverlay() {
	overlay = newBlankImage(width, height, TRANSPARENT_INT);
}

/*====== MODIFIERS ======*/
//set a pixel on the main canvas
void setPixel(int x, int y, int color) {
	try { pixels.setRGB(x, y, Color.alphaBlend(color, pixels.getRGB(x, y))); } catch (Exception ignored) { }
}

//set overlay pixel without blending; primary method for overlays
//this is the most used method
void setOverlayPixelWithoutBlending(int x, int y, int exactColor) {
	try { overlay.setRGB(x, y, exactColor); } catch (Exception ignored) { }
}

//set a pixel on the main canvas without blending, used by erasers
void setPixelWithoutBlending(int x, int y, int exactColor) {
	try { pixels.setRGB(x, y, exactColor); } catch (Exception ignored) { }
}

//set a pixel on the overlay with blending
void setOverlayPixel(int x, int y, int color) {
	try { overlay.setRGB(x, y, Color.alphaBlend(color, overlay.getRGB(x, y))); } catch (Exception ignored) { }
}

//currently resizes by adding or removing from origin
//no fancy centered-resizing yet
void resize(int newX, int newY) {
	if (newX <= 0 || newY <= 0)
		throw new IllegalArgumentException("Canvas must be greater than 0 in both dimensions.");
	
	//draw a canvas that is newX by newY
	BufferedImage newPixels = newBlankImage(newX, newY, defaultColor);
	
	//and then copy the current canvas on top of the new one
	for (int i = 0; i < width; i++) {
		for (int j = 0; j < height; j++) {
			//try to write to the pixel on the new canvas at that coordinate
			//ignore exceptions when we are out of bounds
			try {
				newPixels.setRGB(i, j, pixels.getRGB(i, j));
			} catch (Exception ignored) {
			}
		}
	}
	
	//change values to the new data
	pixels = newPixels;
	width = newX;
	height = newY;
	overlay = newBlankImage(width, height, TRANSPARENT_INT);
}

/*====== ACCESSORS ======*/
BufferedImage getPixels() {
	return pixels;
}

int getPixel(int x, int y) {
	return pixels.getRGB(x, y);
}

int getOverlayPixel(int x, int y) {
	return overlay.getRGB(x, y);
}

BufferedImage getOverlay() {
	return overlay;
}

int getWidth() {
	return width;
}

int getHeight() {
	return height;
}

//overlay an image ontop of the canvas, with blending
//assuming they are the same size
void merge() {
	for (int i = 0; i < width; i++)
		for (int j = 0; j < height; j++)
			this.pixels.setRGB(i, j, Color.alphaBlend(
				  overlay.getRGB(i, j),
				  this.pixels.getRGB(i, j)
			                                         ));
	overlay = newBlankImage(width, height, TRANSPARENT_INT);
}

void rotateOrtho(Transform option) {
	BufferedImage newPixels;
	if (option == Transform.ROTATE_LEFT || option == Transform.ROTATE_RIGHT) {
		newPixels = newBlankImage(height, width, defaultColor);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (option == Transform.ROTATE_LEFT) newPixels.setRGB(j, (width - 1) - i, pixels.getRGB(i, j));
				else newPixels.setRGB((height - 1) - j, i, pixels.getRGB(i, j));
                /* //normal
                [0][1][2][3][4]
                [5][6][7][8][9]
                [A][B][C][D][E]

                //rotate left
                //access width in reverse order
                //access height in normal order
                [4][9][E]
                [3][8][D]
                [2][7][C]
                [1][6][B]
                [0][5][A]

                //rotate right
                //access width in normal order
                //access height in reverse order
                [A][5][0]
                [B][6][1]
                [C][7][2]
                [D][8][3]
                [E][9][4]
              */
			}
		}
	} else {
		newPixels = newBlankImage(width, height, defaultColor);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				newPixels.setRGB((width - 1) - i, (height - 1) - j, pixels.getRGB(i, j));
			}
		}
	}
	pixels = newPixels;
	if (option == Transform.ROTATE_LEFT || option == Transform.ROTATE_RIGHT) {
		System.out.println("old: " + width + " " + height);
		int temp = width; //old width is new height
		//noinspection SuspiciousNameCombination
		width = height; //set width to old height
		height = temp; //set height to new height
		System.out.println("new: " + width + " " + height);
	}
	overlay = newBlankImage(width, height, TRANSPARENT_INT);
	
}

void flip(int option) {
	BufferedImage newPixels = newBlankImage(width, height, defaultColor);
	for (int i = 0; i < width; i++) {
		for (int j = 0; j < height; j++) {
			if (option == 0) newPixels.setRGB((width - 1) - i, j, pixels.getRGB(i, j));
			else newPixels.setRGB(i, (height - 1) - j, pixels.getRGB(i, j));
		}
	}
	pixels = newPixels;
}

static enum Transform {ROTATE_LEFT, ROTATE_RIGHT, ROTATE_180}
}

