package com.jpaint;


public class ToolPaintBrush extends Tool implements EditCanvas {
    ToolPaintBrush(String name, ImageModel model) {
        super(name, model);
    }

    @Override
    public void paint(int x, int y, Color color) {
        _imageModel.editPixel(x, y, color);
    }
}
