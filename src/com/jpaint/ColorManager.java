package com.jpaint;

import java.util.ArrayList;

//manager selected colors
//add this to anything that needs to access a color
//will be used to show current colors
public class ColorManager {
    private Color _left;
    private Color _middle;
    private Color _right;

    private ArrayList<Tool> _observers;

    ColorManager(ArrayList<Tool> tools, Color leftClickColor, Color middleClickColor, Color rightClickColor) {
        _left = leftClickColor;
        _middle = middleClickColor;
        _right = rightClickColor;
        _observers = tools;
        //notifyTools();
    }

    //when a color is changed, send new colors to observers
    public void notifyTools() {
        for(int i = 0; i < _observers.size(); i++) {
            _observers.get(i).updateColors(_left, _middle, _right);
        }
    }

    //getters
    public Color getLeft() {
        return _left;
    }
    public Color getMiddle() {
        return _middle;
    }
    public Color getRight() {
        return _right;
    }

    //setters
    public void setLeft(Color leftClickColor) {
        _left = leftClickColor;
        notifyTools();
    }

    public void setMiddle(Color middleClickColor) {
        _middle = middleClickColor;
        notifyTools();
    }

    public void setRight(Color rightClickColor) {
        _right = rightClickColor;
        notifyTools();
    }
}