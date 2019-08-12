package com.jpaint;


public class ToolPaintBrush extends Tool implements EditCanvas {
    ToolPaintBrush(String name, ImageModel model, String iconSource) {
        super(name, model, iconSource);
    }

    @Override
    public void clickCanvas(int x, int y, Color color) {
        _model.setPixel(x, y, color);
    }
}
