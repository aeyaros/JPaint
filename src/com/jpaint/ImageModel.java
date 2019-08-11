package com.jpaint;

import java.util.Deque;

//ImageModel: THIS IS THE MAIN MODEL CLASS, contains canvas and state functionality
public class ImageModel {
    private Deque<Canvas> _pastStates; //previous states
    private Deque<Canvas> _undoneStates; //"future states" that were undone
    private Canvas _currentState;
    private ImageView _imageView; //a view for the model to update

    private final int MAX_UNDO = 10;
    private final int MAX_REDO = 10;

    /*====== GENERAL ======*/

    //create a new image model with a width and a height
    ImageModel(int w, int h, ImageView imageView) {
        _currentState = new Canvas(w, h);
        _imageView = imageView;
        _imageView.refresh(_currentState.getImage());
    }

    //get the current state of the drawing
    private Canvas getCurrentState() {
        return _currentState;
    }

    //update the current state (note: is used for undo/redo)
    private void updateCurrentState(Canvas canvas) {
        _currentState = new Canvas(canvas);
    }

    /*====== UNDOING AND REDOING ======*/

    public boolean canUndo() {
        if(_pastStates.size() > 0) return true; //if stuff was done previously, we can undo
        else return false; //no past states to revert to
    }

    public boolean canRedo() {
        if (_undoneStates.size() > 0) return true;
        else return false;
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
    public Color getPixel(int x, int y) {
        return getCurrentState().getColor(x,y);
    }

    public void editPixel(int x, int y, Color color) {
        Canvas canvas = getCurrentState();


        updateCurrentState(canvas);
    }
}
