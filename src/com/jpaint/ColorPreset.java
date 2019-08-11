package com.jpaint;

import javax.swing.*;

//ColorPreset: a button for choosing a preset color in the UI
public class ColorPreset extends JButton {
    private Color _color;

    ColorPreset(Color color) {
        super();
        this.setOpaque(true);
        this.setBorderPainted(false);
        changeColor(color);
    }

    public void changeColor(Color newColor) {
        _color = newColor;
        this.setBackground(_color.getAWT());
    }
}
