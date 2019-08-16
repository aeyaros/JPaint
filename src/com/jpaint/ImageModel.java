package com.jpaint;

import java.util.ArrayDeque;

//ImageModel: THIS IS THE MAIN MODEL CLASS, contains canvas and state functionality
public class ImageModel {
    private ImageView imageView; //a view for the model to update
    private Canvas currentState; //current state of the drawing
    private ArrayDeque<Canvas> pastStates; //previous states
    private ArrayDeque<Canvas> undoneStates; //"future states" that were undone

    //TO-DO - MAKE THESE BIGGER WHEN I KNOW THEY ARE WORKING!!!
    private final int MAX_UNDO = 10;
    private final int MAX_REDO = 10;

    /*====== GENERAL ======*/

    //create a new image model with a width and a height
    ImageModel(int w, int h, ImageView imageView) {
        currentState = new Canvas(w, h);
        pastStates = new ArrayDeque<>();
        undoneStates = new ArrayDeque<>();

        this.imageView = imageView;
        refresh(); //dont remove this from here!
    }

    //refresh the view with the current state
    void refresh() {
        imageView.refresh(currentState.getImage());
    }

    //get the current state of the drawing
    private Canvas getCurrentState() {
        return currentState;
    }

    //before a tool is used, save state to past states
    void saveCurrentState() {
        pastStates.addFirst(new Canvas(currentState));
    }


    /*====== UNDOING AND REDOING ======*/

    //update the current state (note: is used for undo/redo)
    private void updateCurrentState(Canvas canvas) {
        currentState = new Canvas(canvas);
    }

    boolean canUndo() {
        //no past states to revert to
        return pastStates.size() > 0; //returns true if number of past states > 0
    }

    boolean canRedo() {
        return undoneStates.size() > 0; //returns true if number of undoed states > 0
    }

    //add to past states - this happens if we take any action to change the canvas, or if we redo
    private void addToPastStates(Canvas canvas) {
        pastStates.addFirst(new Canvas(canvas)); //push state to deque
        if(pastStates.size() > MAX_REDO) pastStates.removeLast(); //remove excess states to prevent overflow
    }

    //add a state to undone states when undoing
    private void addToUndoneStates(Canvas canvas) {
        undoneStates.addFirst(new Canvas(canvas)); //push state to deque
        if(undoneStates.size() > MAX_UNDO) undoneStates.removeLast(); //remove excess states to prevent overflow
    }

    //undo the most recently made change
    public void undo() {
        if(this.canUndo()) { //if we can undo
            addToUndoneStates(currentState); //push current state to beginning of undonestates
            updateCurrentState(pastStates.removeFirst()); //pop past state to current state
        } //else cant undo (nothing can be popped from the previous state
    }

    //redo most recently undone state
    public void redo() {
        if(this.canRedo()) { //if we can redo
            addToPastStates(currentState); //push current state to past states
            updateCurrentState(undoneStates.removeFirst()); //pop undone state to current state
        } //else cant redo (nothing was previously undone
    }

    /*====== ACCESSING CANVAS ======*/
    int getWidth() {
        return currentState.getWidth();
    }

    int getHeight() {
        return currentState.getHeight();
    }

    Color getPixel(int x, int y) {
        return getCurrentState().getColor(x,y);
    }

    void setPixel(int x, int y, int argb) {
        currentState.setPixel(x,y,argb);
    }

    void setPixel(int x, int y, Color color) {
        currentState.setPixel(x,y,color.getARGB());
    }
}
