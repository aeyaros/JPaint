package com.jpaint;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

//Tool: This is a tool button - subclasses contain specific tool functionality
public abstract class Tool implements ToolInput {
    protected ImageModel model; //the model class this tool acts on
    protected JPanel upperCard; //the upper panel shown when this tool is selected
    JToggleButton button; //the tool button
    private String name;
    private int[] colorsInts;

    //set button name and add the model
    Tool(String name, ImageModel model, String iconSource, String selectedIconSource) {
        super();
        this.name = name;
        this.model = model;
        colorsInts = new int[3];

        //panel with top controls - initialize
        //you can do whatever you want with this in the derived tool classes
        upperCard = new JPanel();

        //button for toolbar
        button = new JToggleButton();
        button.setOpaque(true);
        //button.setContentAreaFilled(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createEtchedBorder());
        button.setSize(ApplicationWindow.TOOL_BUTTON_SIZE, ApplicationWindow.TOOL_BUTTON_SIZE);

        try { //get icon of button
            Image image = ImageIO.read(getClass().getResource(iconSource));
            image = image.getScaledInstance(ApplicationWindow.TOOL_BUTTON_SIZE, ApplicationWindow.TOOL_BUTTON_SIZE, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(image));

            Image selectedImage = ImageIO.read(getClass().getResource(selectedIconSource));
            selectedImage = selectedImage.getScaledInstance(ApplicationWindow.TOOL_BUTTON_SIZE, ApplicationWindow.TOOL_BUTTON_SIZE, Image.SCALE_SMOOTH);
            button.setSelectedIcon(new ImageIcon(selectedImage));
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Couldn't load icon for " + name + " button from location " + iconSource);
        }
    }

    void updateColors(Color left, Color middle, Color right) {
        colorsInts[0] = left.getARGB();
        colorsInts[1] = middle.getARGB();
        colorsInts[2] = right.getARGB();
    }

    String getName() {
        return name;
    }

    int getColorIntByButton(int mouseEventButtonCode) {
        switch (mouseEventButtonCode) {
            case MouseEvent.BUTTON2: return colorsInts[1];
            case MouseEvent.BUTTON3: return colorsInts[2];
            default: return colorsInts[0]; //MouseEvent.BUTTON1
        }
    }

    /*====== DRAWING TOOLS ======*/

    //source of algorithm:
    //https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
    //version that accounts for x and y error; a Java implementation
    //only a limited amount of mouse events are actually captured
    //this results in a row of dots on the canvas
    //this is solved by drawing a line from the current dot to the previous dot
    void bresenham(int x0, int y0, int x1, int y1, int color) {
        int dx = Math.abs(x1 - x0);
        int sx = -1;
        if(x0 < x1) sx = 1;

        int dy = -1 * Math.abs(y1 - y0);
        int sy = -1;
        if(y0 < y1) sy = 1;

        int err = dx + dy; //error value ex_y
        int e2;

        while (x0 != x1 || y0 != y1) { //until we reach the current point
            //calculate next point on line towards current point
            // and update past coordinates (x0, y0) to that point
            e2 = 2 * err;
            if (e2 >= dy) {
                err += dy; // e_xy + e_x > 0
                x0 += sx;
            }
            if (e2 <= dx) { // e_xy + e_y < 0
                err += dx;
                y0 += sy;
            }

            //then draw at that point
            draw(x0,y0,color);
        }
    }

    void makeCircle(int origX, int origY, int color, int radius, int negativeRadius, boolean blend, boolean useOverlay) {
        for(int y = negativeRadius; y <= radius; y++) {
            for(int x = negativeRadius; x <= radius; x++) {
                if (x * x + y * y <= radius * radius) //draw if inside bounds of circle
                    model.setPixel(origX + x, origY + y, color, blend,useOverlay);
            }
        } model.refreshView();
    }
}
