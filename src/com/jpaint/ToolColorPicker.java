package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ToolColorPicker extends Tool {
    private ColorManager colorManager;
    private CardLayout cards;
    private JPanel coordinates;
    private JLabel[] ARGB;
    private String[] labelPrefixes = {"Opacity: ", "Red: ", "Green: ", "Blue: "};
    private final int DARK_THRESHOLD = 80;

    ToolColorPicker(String name, ImageModel model, String iconSource, String selectedIconSource) {
        super(name, model, iconSource, selectedIconSource);
        //set up upper card
        cards = new CardLayout();
        upperCard = new JPanel(cards);

        //add coordinates panel with grid layout
        coordinates = new JPanel(new GridLayout(1,4));
        coordinates.setOpaque(true);
        upperCard.add(coordinates,0);

        //add panel to be shown when not hovering over the image
        JLabel infoText = new JLabel("Hover over the image, then click to pick a color.");
        infoText.setHorizontalAlignment(SwingConstants.CENTER);
        infoText.setVerticalAlignment(SwingConstants.CENTER);
        upperCard.add(infoText);

        //set up labels showing the coordinates
        ARGB = new JLabel[4];
        for(int i = 0; i < ARGB.length; i++) {
            ARGB[i] = new JLabel("");
            ARGB[i].setHorizontalAlignment(SwingConstants.CENTER);
            ARGB[i].setVerticalAlignment(SwingConstants.CENTER);
            coordinates.add(ARGB[i]);
        } clearStatus();
    }

    void addColorManager(ColorManager colorManager) {
        this.colorManager = colorManager;
    }

    private Color getColorAtPixel(int x, int y) {
        return new Color(model.getColorAtPixel(x,y).getARGB());
    }

    private void updateStatus(int x, int y) {
        if(model.isInBounds(x,y)) {
            //set background to selected color
            try {
                Color c = getColorAtPixel(x, y);
                coordinates.setBackground(c.getAWT());
                for(int i = 0; i < ARGB.length; i++) {
                    //invert color of text if color is too dark
                    if(isDarkColor(c)) ARGB[i].setForeground(java.awt.Color.white);
                    else ARGB[i].setForeground(java.awt.Color.black);
                    ARGB[i].setText(labelPrefixes[i] + c.getChannel(i));
                } cards.first(upperCard);
                //System.out.println(x + ", " + y + " r: " + c.getChannel(1) + " g: " + c.getChannel(2) + " b: " + c.getChannel(3) + " a: " + c.getChannel(0));
            } catch (Exception e) {
                System.err.println("Tried to access out of bounds pixel");
                e.printStackTrace();
            }
        }
    }

    //WC3 Brightness formula: ((Red value X 299) + (Green value X 587) + (Blue value X 114)) / 1000
    private boolean isDarkColor(Color c) {
        double r = c.getChannel(1)/Color.MAX_VALUE_FLOAT;
        double g = c.getChannel(2)/Color.MAX_VALUE_FLOAT;
        double b = c.getChannel(3)/Color.MAX_VALUE_FLOAT;
        return ((((r * 299d) + (g * 587d) + (b * 114d)) / 1000.0d) < 0.4);
    }

    private void setColor(int x, int y, int buttonCode) {
        if(model.isInBounds(x,y)) {
            Color c = getColorAtPixel(x,y);
            colorManager.setButtonColor(c, buttonCode);
        }
    }

    private void clearStatus() {
        coordinates.setBackground(new Color(Color.MAX_VALUE,Color.MAX_VALUE/2,Color.MAX_VALUE/2,Color.MAX_VALUE/2).getAWT());
        for(JLabel l: ARGB) l.setText("");
        cards.last(upperCard);
        //System.out.print("Cleared!");
    }

    @Override
    public void toolDragged(MouseEvent e) {
        updateStatus(e.getX(),e.getY());
        setColor(e.getX(), e.getY(), e.getButton());
    }

    @Override
    public void toolMoved(MouseEvent e) {
        updateStatus(e.getX(),e.getY());
    }

    @Override
    public void toolClicked(MouseEvent e) {
        updateStatus(e.getX(),e.getY());
    }

    @Override
    public void toolPressed(MouseEvent e) {
        updateStatus(e.getX(),e.getY());
        setColor(e.getX(),e.getY(), e.getButton());
    }

    @Override
    public void toolReleased(MouseEvent e) {
        updateStatus(e.getX(),e.getY());
    }

    @Override
    public void toolEntered(MouseEvent e) {
        updateStatus(e.getX(),e.getY());
    }

    @Override
    public void toolExited(MouseEvent e) {
        clearStatus();
    }
}
