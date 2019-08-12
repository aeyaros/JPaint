package com.jpaint;

public class ToolEraser extends Tool implements EditCanvas {
    ToolEraser(String name, ImageModel model, String iconSource) {
        super(name, model, iconSource);
    }

    @Override
    public void clickCanvas(int x, int y, Color color) {

    }
}
