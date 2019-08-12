package com.jpaint;

public class ToolShapes extends Tool implements EditCanvas {
    ToolShapes(String name, ImageModel model, String iconSource) {
        super(name, model, iconSource);
    }

    @Override
    public void clickCanvas(int x, int y, Color color) {

    }
}
