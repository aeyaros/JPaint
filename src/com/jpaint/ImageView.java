package com.jpaint;

import javax.swing.*;

//ImageView: THIS IS THE MAIN VIEW CLASS
class ImageView extends JLabel {
    private JLabel coordinatesLabel;
    private JLabel sizeLabel;

    ImageView(JLabel coordinatesLabel, JLabel sizeLabel) {
        super();
        //set label background to opaque white
        this.setOpaque(true);
        this.setBackground(java.awt.Color.white);
        this.coordinatesLabel = coordinatesLabel;
        this.sizeLabel = sizeLabel;
    }

    //update the view
    void refresh(ImageIcon icon) {
        //this is where I should overlay the canvas on the checkerboard
        this.setIcon(icon);
    }

    void refreshCoordinates(int x, int y) {
        coordinatesLabel.setText("x: " + x + "px, y: " + y + "px");
    }

    void updateSizeLabel(int w, int h) {
        sizeLabel.setText("Width: " + w + ", Height: " + h);
    }

    void clearCoordinates() {
        coordinatesLabel.setText("");
    }


}
