package com.jpaint;

public class ToolLine extends Tool implements EditCanvas {
    ToolLine(String name, ImageModel model, String iconSource) {
        super(name, model, iconSource);
    }

    @Override
    public void clickCanvas(int x, int y, Color color) {

    }
}
