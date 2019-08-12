package com.jpaint;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

//Tool: This is a tool button - subclasses contain specific tool functionality
public class Tool extends JButton {
    protected ImageModel _model; //the model class this tool acts on
    protected JPanel _upperCard; //the upper panel shown when this tool is selected

    //set button name and add the model
    Tool(String name, ImageModel model, String iconSource) {
        super();
        Dimension size = new Dimension();
        this.setSize(Main.TOOL_BUTTON_SIZE, Main.TOOL_BUTTON_SIZE);
        this.setBorderPainted(false);
        _model = model;
        try {
            Image image = ImageIO.read(getClass().getResource(iconSource));
            image = image.getScaledInstance(Main.TOOL_BUTTON_SIZE, Main.TOOL_BUTTON_SIZE, Image.SCALE_SMOOTH);
            this.setIcon(new ImageIcon(image));
        } catch(Exception e) {
            e.printStackTrace();
            //System.err.println("Couldn't load icon for " + name + " button from location " + iconSource);
        }
    }


}
