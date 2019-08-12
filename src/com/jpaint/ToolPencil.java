package com.jpaint;

public class ToolPencil extends Tool implements EditCanvas {
    ToolPencil(String name, ImageModel model, String iconSource) {
        super(name, model, iconSource);
    }

    @Override
    public void clickCanvas(int x, int y, Color color) {

    }
}
