package com.jpaint;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;

//ImageModel: THIS IS THE MAIN MODEL CLASS, contains canvas and state functionality
class ImageModel {
private final int MAX_UNDO = 50;
private final int MAX_REDO = 50;
private ImageView imageView; //a view for the model to update
private Canvas currentState; //current state of the drawing
private ArrayDeque<Canvas> pastStates; //previous states
private ArrayDeque<Canvas> undoneStates; //"future states" that were undone
//image save states
private boolean isUntouched; //if brand new
private boolean isSaved; //if file was saved to the disk (i.e. if file was opened, or if it was created and saved)

/*====== GENERAL ======*/

//create a new image model with a width and a height
ImageModel(int w, int h, ImageView imageView) {
	currentState = new Canvas(w, h, false);
	this.imageView = imageView;
	initializeModel(w, h);
}

//functionality shared by both constructors
private void initializeModel(int w, int h) {
	pastStates = new ArrayDeque<>();
	undoneStates = new ArrayDeque<>();
	startOverFromScratch(w, h, false);
}

/*====== STARTING OVER, AND SAVE STATUS ======*/

//you can start over from scratch or from an image

private void startOver(Canvas temp) {
	//set canvases to correct sizes
	resizeCanvases(temp.getWidth(), temp.getHeight());
	
	//clear canvases
	currentState.clearAll();
	pastStates.clear();
	undoneStates.clear();
	//set canvas
	currentState = temp;
	//update image view
	imageView.updateSize(temp.getWidth(), temp.getHeight());
	//save states and refresh
	saveCurrentState();
	refreshView();
	isUntouched = true; //initially, file not touched
	isSaved = false; //assuming it isn't saved unless this is set by the open or save commands
}

//if creating a new image
void startOverFromScratch(int w, int h, boolean transparent) {
	startOver(new Canvas(w, h, transparent));
	
	System.out.println("...from scratch");
}

//if opening an image
void startOverFromImage(BufferedImage image) {
	Canvas temp;
	try {
		temp = new Canvas(image);
	} catch (Exception e) {
		throw new IllegalArgumentException();
	}
	
	startOver(temp);
	
	System.out.println("...from another file");
}

void setSaved() {
	this.isSaved = true;
	System.out.println("File set as saved");
}

void setUntouched() {
	this.isUntouched = true;
	System.out.println("File set as untouched");
}

boolean isSaved() {
	return isSaved;
}

boolean isUntouched() {
	return isUntouched;
}

/* DISPLAYING COORDINATES */

/*====== VIEWING TOOLS ======*/

//send the current state of the model to the view
void refreshView() {
	imageView.refresh(
		  new ImageIcon(currentState.getPixels()),
		  new ImageIcon(currentState.getOverlay())
	                 );
}

//used for saving state
BufferedImage getImage() {
	return currentState.getPixels();
}

/*====== STATES, UNDOING AND REDOING ======*/

//get the current state of the drawing
//private Canvas getCurrentState() { return currentState; }

//before a tool is used, save state to past states
void saveCurrentState() {
	//save current state
	addToPastStates(currentState);
	
	//if too many undo'ed states then remove an action from the undo list
	if (pastStates.size() > MAX_UNDO) pastStates.removeLast();
	
	//we just did a new action; we cant keep the old undone actions as we are starting a new branch
	undoneStates.clear();
	System.out.println("Saved current state");
	printStates();
}

//update the current state (note: this is used for undo/redo)
private void updateCurrentState(Canvas canvas) {
	currentState = new Canvas(canvas);
}

//returns true if number of past states > 0
private boolean canUndo() {
	return pastStates.size() > 1;
}

//returns true if number of undone states > 0
private boolean canRedo() {
	return undoneStates.size() > 0;
}

//add to past states - this happens if we take any action to change the canvas, or if we redo
private void addToPastStates(Canvas canvas) {
	pastStates.addFirst(new Canvas(canvas)); //push state to deque
	isUntouched = false; //the image is no longer untouched, because we have done something
	System.out.println("Model is not untouched anymore");
	if (pastStates.size() > MAX_REDO) pastStates.removeLast(); //remove excess states to prevent overflow
}

//add a state to undone states when undoing
private void addToUndoneStates(Canvas canvas) {
	undoneStates.addFirst(new Canvas(canvas)); //push state to deque
	isUntouched = false;
	if (undoneStates.size() > MAX_UNDO) undoneStates.removeLast(); //remove excess states to prevent overflow
}

//undo the most recently made change
void undo() {
	if (this.canUndo()) { //if we can undo
		addToUndoneStates(currentState); //push current state to beginning of undone states
		updateCurrentState(pastStates.removeFirst()); //pop past state to current state
		this.refreshView();
		System.out.println("Undone");
		printStates();
	} //else cant undo (nothing can be popped from the previous state
	else System.out.println("Can't undo");
}

//redo most recently undone state
void redo() {
	if (this.canRedo()) { //if we can redo
		addToPastStates(currentState); //push current state to past states
		updateCurrentState(undoneStates.removeFirst()); //pop undone state to current state
		this.refreshView();
		System.out.println("Redone");
		printStates();
	} //else cant redo (nothing was previously undone
	else System.out.println("Can't redo");
}

private void printStates() {
	if (pastStates.size() > 0) for (int i = 0; i < pastStates.size(); i++) System.out.print("[p]");
	System.out.print("[c]"); //for current state
	if (undoneStates.size() > 0) for (int i = 0; i < undoneStates.size(); i++) System.out.print("[f]");
	System.out.print('\n');
}

/*====== EDITING CANVAS ======*/

void erasePixel(int x, int y, boolean transparent) {
	if (isInBounds(x, y)) {
		if (transparent) currentState.setPixelWithoutBlending(x, y, Canvas.TRANSPARENT_INT);
		else currentState.setPixelWithoutBlending(x, y, Canvas.WHITE_INT);
	}
}

void setPixel(int x, int y, int argb, boolean useOverlay) {
	if (isInBounds(x, y)) { //important; throwing tons of exceptions is time consuming
		if (useOverlay) currentState.setOverlayPixelWithoutBlending(x, y, argb);
		else currentState.setPixel(x, y, argb);
	}
}

/* FUNCTIONS USED BY MENUS */

//this can be used directly by a menu
void resize(int newX, int newY) {
	saveCurrentState(); //save current state
	resizeCanvases(newX, newY);
	refreshView();
}

//used by the resize and startover functions
private void resizeCanvases(int newX, int newY) {
	currentState.resize(newX, newY);
}

//used directly by a menu
void flip(int option) { //0 = horizontal, else vertical
	saveCurrentState();
	currentState.flip(option % 2);
	refreshView();
}

//used directly by a menu
void rotate(int option) { //0 = left, 1 = right, else 180
	saveCurrentState();
	currentState.rotateOrtho(option % 3);
	refreshView();
}


/*====== ACCESSING CANVAS ======*/

int getWidth() {
	return currentState.getWidth();
}

int getHeight() {
	return currentState.getHeight();
}

//return true if coordinate is inside the bounds of the canvas
boolean isInBounds(int x, int y) {
	return (x >= 0 && y >= 0 && x < currentState.getWidth() && y < currentState.getHeight());
}

int getPixel(int x, int y) {
	if (isInBounds(x, y)) return currentState.getPixel(x, y);
	else throw new IndexOutOfBoundsException();
}

int getOverlayPixel(int x, int y) {
	if (isInBounds(x, y)) return currentState.getOverlayPixel(x,y);
	else throw new IndexOutOfBoundsException();
}


/* TEMPORARY OVERLAY */

void mergeOverlay() {
	currentState.merge();
	refreshView();
}

void clearOverlay() {
	currentState.clearOverlay();
}

}
