package com.jpaint;

//manager selected colors
//add this to anything that needs to access a color
//will be used to show current colors
public class ColorManager {
    private Color left;
    private Color middle;
    private Color right;
    private Tool[] observers;

    ColorManager(Tool[] tools, Color leftClickColor, Color middleClickColor, Color rightClickColor) {
        left = leftClickColor;
        middle = middleClickColor;
        right = rightClickColor;
        observers = tools;
        notifyTools();
    }

    //when a color is changed, send new colors to observers
    void notifyTools() {
        for (Tool observer : observers) {
            observer.updateColors(left, middle, right);
        }
    }

    //getters
    public Color getLeft() {
        return left;
    }
    public Color getMiddle() {
        return middle;
    }
    public Color getRight() {
        return right;
    }

    //setters
    public void setLeft(Color leftClickColor) {
        left = leftClickColor;
        notifyTools();
    }

    public void setMiddle(Color middleClickColor) {
        middle = middleClickColor;
        notifyTools();
    }

    public void setRight(Color rightClickColor) {
        right = rightClickColor;
        notifyTools();
    }
}