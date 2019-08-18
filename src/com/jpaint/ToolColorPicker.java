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

    ToolColorPicker(String name, ImageModel model, String iconSource) {
        super(name, model, iconSource);
        //set up upper card
        cards = new CardLayout();
        upperCard = new JPanel(cards);

        //add coordinates panel with grid layout
        coordinates = new JPanel(new GridLayout(1,4));
        coordinates.setOpaque(true);
        upperCard.add(coordinates,0);

        //add panel to be shown when not hovering over the image
        JLabel infoText = new JLabel("Hover over the image to pick a color.");
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
        }

        clearStatus();
    }

    void addColorManager(ColorManager colorManager) {
        this.colorManager = colorManager;
    }

    private Color getColorAtPixel(int x, int y) {
        return new Color(model.getPixel(x,y).getARGB());
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
                }
                cards.first(upperCard);
                //System.out.println(x + ", " + y + " r: " + c.getChannel(1) + " g: " + c.getChannel(2) + " b: " + c.getChannel(3) + " a: " + c.getChannel(0));
            } catch (Exception e) {
                System.err.println("Tried to access out of bounds pixel");
                e.printStackTrace();
            }

        }
    }

    private boolean isDarkColor(Color c) {
        return ((c.getChannel(1) + c.getChannel(2) + c.getChannel(3))/ 3 < DARK_THRESHOLD);
        //return (c.getChannel(1) < DARK_THRESHOLD || c.getChannel(2) < DARK_THRESHOLD || c.getChannel(3) < DARK_THRESHOLD );
    }

    private void setColor(int x, int y, int buttonCode) {
        if(model.isInBounds(x,y)) {
            Color c = getColorAtPixel(x,y);
            colorManager.setButtonColor(c, buttonCode);
        }
    }

    private void clearStatus() {
        coordinates.setBackground(new Color(255,128,128,128).getAWT());
        for(JLabel l: ARGB) l.setText("");
        cards.last(upperCard);
        System.out.print("Cleared!");
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
