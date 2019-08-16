package com.jpaint;

import javax.swing.*;

//ImageView: THIS IS THE MAIN VIEW CLASS
class ImageView extends JLabel {
    ImageView() {
        super();
        //set label background to opaque white
        this.setOpaque(true);
        this.setBackground(java.awt.Color.white);
    }

    //update the view
    void refresh(ImageIcon icon) {
        //overlay the canvas on top of the checkerboard
        this.setIcon(icon);
    }


}
