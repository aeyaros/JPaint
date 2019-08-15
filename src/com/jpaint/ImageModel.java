package com.jpaint;

import java.util.ArrayDeque;

//ImageModel: THIS IS THE MAIN MODEL CLASS, contains canvas and state functionality
public class ImageModel {
    private ArrayDeque<Canvas> _pastStates; //previous states
    private ArrayDeque<Canvas> _undoneStates; //"future states" that were undone
    private Canvas _currentState; //current state of the drawing
    private ImageView _imageView; //a view for the model to update

    //TO-DO - MAKE THESE BIGGER WHEN I KNOW THEY ARE WORKING!!!
    private final int MAX_UNDO = 10;
    private final int MAX_REDO = 10;

    /*====== GENERAL ======*/

    //create a new image model with a width and a height
    ImageModel(int w, int h, ImageView imageView) {
        _currentState = new Canvas(w, h);
        _imageView = imageView;
        refresh(); //dont remove
    }

    //refresh the view
    void refresh() {
        _imageView.refresh(_currentState.getImage());
    }

    //get the current state of the drawing
    private Canvas getCurrentState() {
        return _currentState;
    }

    //before a tool is used, save state to past states
    void saveCurrentState() {
        _pastStates.addFirst(new Canvas(_currentState));
    }


    /*====== UNDOING AND REDOING ======*/

    //update the current state (note: is used for undo/redo)
    private void updateCurrentState(Canvas canvas) {
        _currentState = new Canvas(canvas);
    }

    boolean canUndo() {
        //no past states to revert to
        return _pastStates.size() > 0; //returns true if number of past states > 0
    }

    boolean canRedo() {
        return _undoneStates.size() > 0; //returns true if number of undoed states > 0
    }

    //add to past states - this happens if we take any action to change the canvas, or if we redo
    private void addToPastStates(Canvas canvas) {
        _pastStates.addFirst(new Canvas(canvas)); //push state to deque
        if(_pastStates.size() > MAX_REDO) _pastStates.removeLast(); //remove excess states to prevent overflow
    }

    //add a state to undone states when undoing
    private void addToUndoneStates(Canvas canvas) {
        _undoneStates.addFirst(new Canvas(canvas)); //push state to deque
        if(_undoneStates.size() > MAX_UNDO) _undoneStates.removeLast(); //remove excess states to prevent overflow
    }

    //undo the most recently made change
    public void undo() {
        if(this.canUndo()) { //if we can undo
            addToUndoneStates(_currentState); //push current state to beginning of undonestates
            updateCurrentState(_pastStates.removeFirst()); //pop past state to current state
        } //else cant undo (nothing can be popped from the previous state
    }

    //redo most recently undone state
    public void redo() {
        if(this.canRedo()) { //if we can redo
            addToPastStates(_currentState); //push current state to past states
            updateCurrentState(_undoneStates.removeFirst()); //pop undone state to current state
        } //else cant redo (nothing was previously undone
    }

    /*====== ACCESSING CANVAS ======*/
    int getWidth() {
        return _currentState.getWidth();
    }

    int getHeight() {
        return _currentState.getHeight();
    }

    Color getPixel(int x, int y) {
        return getCurrentState().getColor(x,y);
    }

    void setPixel(int x, int y, int argb) {
        _currentState.setPixel(x,y,argb);
    }

    void setPixel(int x, int y, Color color) {
        _currentState.setPixel(x,y,color.getARGB());
    }



}
