package com.jpaint;

public class ToolSelect extends Tool implements EditCanvas {
    ToolSelect(String name, ImageModel model, String iconSource) {
        super(name, model, iconSource);
    }

    @Override
    public void clickCanvas(int x, int y, Color color) {

    }
}
