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
    private BufferedImage tileBG; //tiled background

    private final int MAX_UNDO = 50;
    private final int MAX_REDO = 50;

    /*====== GENERAL ======*/

    //create a new image model with a width and a height
    ImageModel(int w, int h, ImageView imageView) {
        currentState = new Canvas(w, h);
        pastStates = new ArrayDeque<>();
        undoneStates = new ArrayDeque<>();
        this.imageView = imageView;
        this.tileBG = getBG(w,h,8);
        this.imageView.updateSizeLabel(w, h);

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
        imageView.refresh(new ImageIcon(currentState.getPixels()));
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

    //draw a checkerboard at a given size
    static BufferedImage getBG(int w, int h, int squareSize) {
        BufferedImage matrix = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        int[] squareColors = {
                new Color(255,255,255,255).getARGB(),
                new Color(255,200,200,200).getARGB()
        }; boolean use1 = false;
        for(int i = 0; i < w; i++) {
            //every few pixels along the row, new color
            if (i % squareSize == 0) use1 = !use1;
            for (int j = 0; j < h; j++) {
                //every few pixels in the column, new color
                if (j % squareSize == 0) use1 = !use1;
                if (use1) matrix.setRGB(i,j,squareColors[1]);
                else matrix.setRGB(i,j,squareColors[0]);
            }
        } return matrix;
    }

    /*====== STATES, UNDOING AND REDOING ======*/

    //get the current state of the drawing
    //private Canvas getCurrentState() { return currentState; }

    //before a tool is used, save state to past states
    void saveCurrentState() {
        //save current state
        pastStates.addFirst(new Canvas(currentState));

        //if too many undo'ed states then remove an action from the undo list
        if(pastStates.size() > MAX_UNDO) pastStates.removeLast();

        //we just did a new action; we cant keep the old undone actions as we are starting a new branch
        undoneStates.clear();
        System.out.println("Saved current state"); printStates();
    }

    //update the current state (note: this is used for undo/redo)
    private void updateCurrentState(Canvas canvas) {
        currentState = new Canvas(canvas);
    }

    //returns true if number of past states > 0
    private boolean canUndo() { return pastStates.size() > 1; }

    //returns true if number of undone states > 0
    private boolean canRedo() { return undoneStates.size() > 0; }

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
    void undo() {
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
    void redo() {
        if(this.canRedo()) { //if we can redo
            addToPastStates(currentState); //push current state to past states
            updateCurrentState(undoneStates.removeFirst()); //pop undone state to current state
            this.refresh();
            System.out.println("Redone");
            printStates();
        } //else cant redo (nothing was previously undone
        else System.out.println("Can't redo");
    }

    private void printStates() {
        if(pastStates.size() > 0) for(int i = 0; i < pastStates.size(); i++) System.out.print("[p]");
        System.out.print("[c]"); //for current state
        if(undoneStates.size() > 0) for(int i = 0; i < undoneStates.size(); i++) System.out.print("[f]");
        System.out.print('\n');
    }

    /*====== EDITING CANVAS ======*/

    void setPixel(int x, int y, int argb) {
        currentState.setPixel(x,y,argb);
    }
    void setPixelWithoutBlending(int x, int y, int argb) {
        currentState.setPixelWithoutBlending(x,y,argb);
    }

    //this can be used directly by a menu
    void resize(int newX, int newY) {
        saveCurrentState(); //save current state
        currentState.resize(newX, newY);
        imageView.updateSizeLabel(newX, newY);
        refresh();
    }

    /*====== ACCESSING CANVAS ======*/

    int getWidth() { return currentState.getWidth(); }
    int getHeight() { return currentState.getHeight(); }

    //return true if coordinate is inside the bounds of the canvas
    boolean isInBounds(int x, int y) {
        return (x >= 0 && y >= 0 && x < currentState.getWidth() && y < currentState.getHeight());
    }

    int getPixel(int x, int y) {
        return currentState.getPixel(x,y);
    }

    Color getColorAtPixel(int x, int y) {
        if(isInBounds(x,y)) return new Color(currentState.getPixels().getRGB(x,y));
        else throw new IndexOutOfBoundsException();
    }
}
