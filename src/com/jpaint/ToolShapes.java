package com.jpaint;


import java.awt.event.MouseEvent;

public class ToolShapes extends ToolLine {
ToolShapes(ImageModel model, String iconSource) {
	super(model, iconSource);
}
private final int SIDES_IN_A_CIRCLE = 64;
private int borderColor;
private int fillColor;

@Override public void draw(int x, int y, int color) {
	model.setPixel(x,y,color,true);
}

@Override public void drawBrush(int x, int y, int color) {
	draw(x,y,color);
}

private void drawShape(int originX, int originY, int currentX, int currentY, int type, boolean fill) {
	//calculate radius based on current point
	int w = currentX - originX;
	int h = currentY - originY;
	int radius = (int)Math.sqrt((w * w) + (h * h));
	double offset = Math.atan2(h, w); //calculate angle offset, so first point matches current location of pointer
	//System.out.print("start " + originX + " " + originY + "  end " + currentX + " " + currentY + "   radius " + radius + "   type " + type + "    offset " + offset * Math.PI / 180d);
	
	if(type == 0) { //draw circle
		//makeCircle(originX, originY, borderColor, radius,true);
		makeRegularPolygon(originX, originY,SIDES_IN_A_CIRCLE,radius,offset,borderColor,true);
	} else if(type > 2) { //draw polygon
		//then draw polygon
		makeRegularPolygon(originX, originY,type,radius,offset,borderColor,true);
		//int halfWidth = (int)Math.ceil((double)width/2d);
		//for(int r = radius - halfWidth; r < radius + halfWidth; r++) {
		//	makeRegularPolygon(originX, originY,type,r,offset,borderColor,false);
		//}
	} else return; //else dont draw
	
	//fill in the shape; dont fill if radius is too small
	if(fill && radius > 1)
		fill(originX,originY, fillColor,true);
	//System.out.print("\n");
}

//when mouse is moved
@Override void refreshPreview(int x1, int y1, int color) {
	model.clearOverlay();
	drawShape(x0, y0, x1, y1, 4, true);
	model.refreshView();
}

//on first click or mousedown
@Override void startDrawing(int startX, int startY, int color, MouseEvent e) {
	//dont save state until we know the line is getting drawn
	//get current points
	x0 = startX;
	y0 = startY;
	borderColor = getColorIntByButton(e.getButton());
	fillColor = getAlternateColor(e.getButton());
}

//on second click, or mouse release
@Override void finishDrawing(int endX, int endY, int color, MouseEvent e) {
	//we know we are drawing the line now, and so we are going to save the old state
	model.saveCurrentState();
	model.clearOverlay();
	borderColor = getColorIntByButton(e.getButton());
	fillColor = getAlternateColor(e.getButton());
	drawShape(x0, y0, endX, endY, 4, true);
	model.mergeOverlay();
	model.refreshView();
}

}
