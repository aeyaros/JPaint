package com.jpaint;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;

//ImageModel: THIS IS THE MAIN MODEL CLASS, contains canvas and state functionality
public class ImageModel {
    private ImageView imageView; //a view for the model to update
    private Canvas currentState; //current state of the drawing
    private ArrayDeque<Canvas> pastStates; //previous states
    private ArrayDeque<Canvas> undoneStates; //"future states" that were undone
    private int[][] tileBG; //tiled background

    private final int MAX_UNDO = 25;
    private final int MAX_REDO = 25;

    /*====== GENERAL ======*/

    //create a new image model with a width and a height
    ImageModel(int w, int h, ImageView imageView) {
        currentState = new Canvas(w, h);
        pastStates = new ArrayDeque<>();
        undoneStates = new ArrayDeque<>();
        this.imageView = imageView;

        saveCurrentState();
        refresh(); //dont remove this from here!

        //show mouse coordinates in a view
        imageView.addMouseMotionListener(new MouseMotionListener() {
            @Override public void mouseMoved(MouseEvent e) {
                refreshCoordinates(e.getX(), e.getY()); }
            @Override public void mouseDragged(MouseEvent e) {
                refreshCoordinates(e.getX(), e.getY()); } });
        imageView.addMouseListener(new MouseListener() {
            //when pointer leaves canvas, display nothing
            @Override public void mouseExited(MouseEvent e) { clearCoordinates(); }
            @Override public void mouseClicked(MouseEvent e) {}
            @Override public void mousePressed(MouseEvent e) {}
            @Override public void mouseReleased(MouseEvent e) {}
            @Override public void mouseEntered(MouseEvent e) {} });
    }

    /* DISPLAYING COORDINATES */

    private void refreshCoordinates(int x, int y) {
        if(isInBounds(x,y)) imageView.refreshCoordinates(x,y);
        else clearCoordinates();
    }

    private void clearCoordinates() {
        imageView.clearCoordinates();
    }

    /*====== VIEWING TOOLS ======*/

    //blend the image with the tile background and then send that to the view
    void refresh() {
        imageView.refresh(getImage(currentState.getPixels()));// overlayMatrices(currentState.getPixels(), tileBG)));
    }

    //export a buffered image for the view
    private ImageIcon getImage(int[][] matrix) {
        BufferedImage bufferedImage = new BufferedImage(matrix.length, matrix[0].length, BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix[0].length; j++) {
                //set the current pixel in the buffered image to argb int from the pixels array
                bufferedImage.setRGB(i, j, matrix[i][j]);
            }
        } return new ImageIcon(bufferedImage);
    }

    //overlay one image matrix on top of another
    //assuming they are the same size
    private int[][] overlayMatrices(int[][] top, int[][] bottom) {
        int w = top.length; int h = top[0].length;
        try {
            int[][] output = new int[w][h];
            for(int i = 0; i < w; i++) { for(int j = 0; j < h; j++) {
                output[i][j] = Color.alphaBlend(top[i][j], bottom[i][j]); }
                        //new Color(top[i][j]), new Color(bottom[i][j])).getARGB(); }
            } return output;
        } catch(Exception e) {
            e.printStackTrace();System.out.print("\n");
            throw new IllegalArgumentException("To blend, screens must be same size: first screen is "
                    + top.length + ", " + top[0].length + " and second screen is "
                    + bottom.length + ", " + bottom[0].length);
        }
    }

    /*====== STATES, UNDOING AND REDOING ======*/

    //get the current state of the drawing
    private Canvas getCurrentState() {
        return currentState;
    }

    //before a tool is used, save state to past states
    void saveCurrentState() {
        //save current state
        pastStates.addFirst(new Canvas(currentState));

        //if too many undo'ed states then remove an action from the undo list
        if(pastStates.size() > MAX_UNDO) pastStates.removeLast();

        //we just did a new action; we cant keep the old undone actions as we are starting a new branch
        undoneStates.clear();
        System.out.println("Save current state");
        printStates();
    }

    //update the current state (note: is used for undo/redo)
    private void updateCurrentState(Canvas canvas) {
        currentState = new Canvas(canvas);
    }

    private boolean canUndo() {
        return pastStates.size() > 1; //returns true if number of past states > 0
    }

    private boolean canRedo() {
        return undoneStates.size() > 0; //returns true if number of undone states > 0
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
            addToUndoneStates(currentState); //push current state to beginning of undone states
            updateCurrentState(pastStates.removeFirst()); //pop past state to current state
            this.refresh();
            System.out.println("Undone");
            printStates();
        } //else cant undo (nothing can be popped from the previous state
        else System.out.println("Can't undo");

    }

    //redo most recently undone state
    public void redo() {
        if(this.canRedo()) { //if we can redo
            addToPastStates(currentState); //push current state to past states
            updateCurrentState(undoneStates.removeFirst()); //pop undone state to current state
            this.refresh();
            System.out.println("Redone");
            printStates();
        } //else cant redo (nothing was previously undone
        else System.out.println("Can't redo");
    }

    void printStates() {
        if(pastStates.size() > 0) for(int i = 0; i < pastStates.size(); i++) System.out.print("[p]");
        System.out.print("[c]"); //for current state
        if(undoneStates.size() > 0) for(int i = 0; i < undoneStates.size(); i++) System.out.print("[f]");
        System.out.print('\n');
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
        return (
                x >= 0 &&
                        y >= 0 &&
                        x < currentState.getWidth() &&
                        y < currentState.getHeight()
        );
    }

    Color getPixel(int x, int y) {
        if(isInBounds(x,y)) return getCurrentState().getColor(x,y);
        else throw new IndexOutOfBoundsException();
    }

    void setPixel(int x, int y, int argb, boolean blend) {
        if(isInBounds(x,y)) currentState.setPixel(x,y,argb,blend);
    }
}
