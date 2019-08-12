package com.jpaint;

public class ToolColorPicker extends Tool implements EditCanvas {

    ToolColorPicker(String name, ImageModel model, String iconSource) {
        super(name, model, iconSource);
    }

    @Override
    public void clickCanvas(int x, int y, Color color) {
        //need to implement current color functionality
    }
}
