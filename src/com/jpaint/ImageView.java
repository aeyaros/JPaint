package com.jpaint;

import javax.swing.*;
import java.awt.image.BufferedImage;

//ImageView: THIS IS THE MAIN VIEW CLASS
public class ImageView extends JLabel {
    private ImageIcon _icon;

    ImageView() {
        super();
        //set label background to opaque white
        this.setOpaque(true);
        this.setBackground(java.awt.Color.white);
    }

    //update the view
    void refresh(ImageIcon icon) {
        _icon = icon;
        this.setIcon(_icon);
    }
}
