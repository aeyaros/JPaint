package com.jpaint;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;

public class ToolPaintBucket extends Tool {
    //boundaries of cannvas
    private int lowX = 0;
    private int lowY = 0;
    private int highX = model.getWidth();
    private int highY = model.getHeight();

    ToolPaintBucket(String name, ImageModel model, String iconSource) {
        super(name, model, iconSource);
        //set up upper card
        upperCard.add(new JButton("bucket button"));
    }


    //Node object for flood algorithm
    private class Node {
        int x; int y;
        Node(int X, int Y) { x = X; y = Y; } //new node
        Node(Node n) { x = n.x; y = n.y; } //deep copy

        int c() { return model.getPixel(x,y).getARGB(); } //get color of node
        void set(Color c) { model.setPixel(x,y,c); } //set color of node

        //get nodes to north, south, east, west
        Node N() {
            if(y+1 > highY) return new Node(x, y+1);
            else throw new IndexOutOfBoundsException();
        } Node S() {
            if(y-1 < lowY) return new Node(x, y-1);
            else throw new IndexOutOfBoundsException();
        } Node E() {
            if(x-1 < lowX) return new Node(x-1, y);
            else throw new IndexOutOfBoundsException();
        } Node W() {
            if(x+1 > highX) return new Node(x+1, y);
            else throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public void toolClicked(MouseEvent e) {
        //Flood-fill algorithm from Wikipedia
        int x = e.getX();
        int y = e.getY();

        Color targetColor = model.getPixel(x, y);
        int target = targetColor.getARGB();

        //////////////
        Color replacement = new Color(255,0,255,0);

        //no need to save state unless there's actually something for us to do

        if (target == replacement.getARGB()) return; // 1. If target-color is equal to replacement-color, return.
        // not applicable: 2. If color of node is not equal to target-color, return.

        //save current state before performing operation
        model.saveCurrentState();

        model.setPixel(x, y, targetColor); // 3. Set the color of node to replacement-color.
        ArrayDeque<Node> nodes = new ArrayDeque<>();      // 4. Set Q to the empty queue.
        nodes.addLast(new Node(x,y)); // 5. Add node to the end of Q.

        while(nodes.size() > 0) { // 6. While Q is not empty:
            Node n = new Node(nodes.removeFirst());// 7. Set n equal to the first element of Q. 8. Remove first element from Q.

            try {
                if(n.W().c() == target) {   // 9. If the color of the node to the west of n is target-color,
                    n.W().set(replacement);// set the color of that node to replacement-color
                    nodes.addLast(n.W()); // and add that node to the end of Q.
                }
            } catch (IndexOutOfBoundsException ex) { System.out.println("Fill: West boundary reached"); }

            try {
                if(n.E().c() == target) {   //10. If the color of the node to the east of n is target-color,
                    n.E().set(replacement);// set the color of that node to replacement-color
                    nodes.addLast(n.E()); // and add that node to the end of Q.
                }
            } catch (IndexOutOfBoundsException ex) { System.out.println("Fill: East boundary reached"); }

            try {
                if(n.N().c() == target) {   //11. If the color of the node to the north of n is target-color,
                    n.N().set(replacement);// set the color of that node to replacement-color
                    nodes.addLast(n.N()); // and add that node to the end of Q.
                }
            } catch (IndexOutOfBoundsException ex) { System.out.println("Fill: North boundary reached"); }

            try {
                if(n.S().c() == target) {   //12. If the color of the node to the south of n is target-color,
                    n.S().set(replacement);// set the color of that node to replacement-color
                    nodes.addLast(n.S()); // and add that node to the end of Q.
                }
            } catch (IndexOutOfBoundsException ex) { System.out.println("Fill: South boundary reached"); }
        } //13. Continue looping until Q is exhausted.
        //14. Return.
    }

    @Override public void toolDragged(MouseEvent e) { }
    @Override public void toolMoved(MouseEvent e) { }
    @Override public void toolPressed(MouseEvent e) { }
    @Override public void toolReleased(MouseEvent e) { }
    @Override public void toolEntered(MouseEvent e) { }
    @Override public void toolExited(MouseEvent e) { }
}