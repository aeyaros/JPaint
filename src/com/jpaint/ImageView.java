package com.jpaint;

import javax.swing.*;

//ImageView: THIS IS THE MAIN VIEW CLASS
class ImageView extends JLabel {
    private JLabel coordinatesLabel;

    ImageView(JLabel coordinatesLabel) {
        super();
        //set label background to opaque white
        this.setOpaque(true);
        this.setBackground(java.awt.Color.white);
        this.coordinatesLabel = coordinatesLabel;
    }

    //update the view
    void refresh(ImageIcon icon) {
        //overlay the canvas on top of the checkerboard
        this.setIcon(icon);
    }

    void refreshCoordinates(int x, int y) {
        coordinatesLabel.setText("x: " + x + "px, y: " + y + "px");
    }

    void clearCoordinates() {
        coordinatesLabel.setText("");
    }


}
