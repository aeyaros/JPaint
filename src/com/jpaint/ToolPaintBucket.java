package com.jpaint;

public class ToolPaintBucket extends Tool implements EditCanvas {
    ToolPaintBucket(String name, ImageModel model) {
        super(name, model);
    }

    @Override
    public void paint(int x, int y, Color color) {
        //algorithm to fill canvas

        //get color of current pixel as orig

        //change color of pixel to new color

        //in a square around orig, fill any pixels matching orig
        //in a square around the square, fill any matching orig
        //keep going until none in square are filled
        //ignore values out of bounds of canvas dimensions
    }
}
