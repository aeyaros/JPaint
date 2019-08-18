package com.jpaint;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;

//ImageModel: THIS IS THE MAIN MODEL CLASS, contains canvas and state functionality
public class ImageModel {
    private ImageView imageView; //a view for the model to update
    private Canvas currentState; //current state of the drawing
    private ArrayDeque<Canvas> pastStates; //previous states
    private ArrayDeque<Canvas> undoneStates; //"future states" that were undone
    private int[][] tileBG; //tiled background

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

        updateTileBG();
        refresh(); //dont remove this from here!
    }

    /*====== VIEWING TOOLS ======*/

    //blend the image with the tile background and then send that to the view
    void refresh() {
        imageView.refresh(getImage( overlayMatrices(currentState.getPixels(), tileBG)));
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

    //update the tile BG if image is resized
    private void updateTileBG() {
        tileBG = generateTileBG(currentState.getWidth(), currentState.getHeight());
    }

    //draw a checkerboard at a given size
    private int[][] generateTileBG(int w, int h) {
        int[][] matrix = new int[w][h];
        int[] squareColors = {
                new Color(255,255,255,255).getARGB(),
                new Color(255,200,200,200).getARGB()
        }; int squareSize = 8; //px
        boolean use1 = false;
        for(int i = 0; i < w; i++) {
            //every 8 pixels along the row, new color
            if (i % squareSize == 0) use1 = !use1;
            for (int j = 0; j < h; j++) {
                //every 8 pixels in the column, new color
                if (j % squareSize == 0) use1 = !use1;
                if (use1) matrix[i][j] = squareColors[1];
                else matrix[i][j] = squareColors[0];
            }
        } return matrix;
    }

    /*====== STATES, UNDOING AND REDOING ======*/

    //get the current state of the drawing
    private Canvas getCurrentState() {
        return currentState;
    }

    //before a tool is used, save state to past states
    void saveCurrentState() {
        pastStates.addFirst(new Canvas(currentState));
    }

    //update the current state (note: is used for undo/redo)
    private void updateCurrentState(Canvas canvas) {
        currentState = new Canvas(canvas);
    }

    private boolean canUndo() {
        return pastStates.size() > 0; //returns true if number of past states > 0
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

    void setPixel(int x, int y, int argb) {
        if(isInBounds(x,y)) currentState.setPixel(x,y,argb);
    }
    void setPixel(int x, int y, Color color) {
        if(isInBounds(x,y)) currentState.setPixel(x,y,color.getARGB());
    }
}
