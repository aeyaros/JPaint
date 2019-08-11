package com.jpaint;

import javax.swing.*;

//Tool: This is a tool button - subclasses contain specific tool functionality
public class Tool extends JButton {
    protected ImageModel _imageModel; //the model class this tool acts on
    protected JPanel _upperCard; //the upper panel shown when this tool is selected

    //set button name and add the model
    Tool(String name, ImageModel model) {
        super(name);
        _imageModel = model;
    }


}
