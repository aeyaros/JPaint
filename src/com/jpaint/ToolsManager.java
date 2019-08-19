package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

class ToolsManager {
    private Tool[] tools;
    private MouseStaticController mouseStaticController;
    private MouseMotionController mouseMotionController;
    private JPanel topPanel;
    private CardLayout topLayout;
    private ToolColorPicker toolColorPicker;

    ToolsManager(JPanel toolsPanel, ImageModel theModel, ImageView theView) {
        //create tools
        //add them to arraylist one by one, then copy to regular array to avoid magic numbers
        ArrayList<Tool> newTools = new ArrayList<>();
        newTools.add(new ToolSelect("Select", theModel,
                "icons/select.png", "icons/selected select.png"));

        toolColorPicker = new ToolColorPicker("Picker", theModel,
                "icons/colorpicker.png", "icons/selected colorpicker.png");
        newTools.add(toolColorPicker); //later, add the color manager once it is declared

        newTools.add(new ToolPencil("Pencil", theModel,
                "icons/pencil.png", "icons/selected pencil.png"));
        newTools.add(new ToolPaintBrush("Brush", theModel,
                "icons/paintbrush.png", "icons/selected paintbrush.png"));
        newTools.add(new ToolEraser("Eraser", theModel,
                "icons/eraser.png", "icons/selected eraser.png"));
        newTools.add(new ToolPaintBucket("Bucket", theModel,
                "icons/paintbucket.png", "icons/selected paintbucket.png"));
        newTools.add(new ToolLine("Line", theModel,
                "icons/line.png", "icons/selected line.png"));
        newTools.add(new ToolShapes("Shapes", theModel,
                "icons/shapes.png", "icons/selected shapes.png"));
        //update colors of tools

        tools = new Tool[newTools.size()]; //use regular array for better performance
        for(int i = 0; i < tools.length; i++) tools[i] = newTools.get(i);

        //create both static and motion mouse controllers and add them to the view
        mouseStaticController = new MouseStaticController(tools);
        theView.addMouseListener(mouseStaticController);

        mouseMotionController = new MouseMotionController(tools);
        theView.addMouseMotionListener(mouseMotionController);

        //set up top panel for tool-specific controls
        topPanel = new JPanel();
        topLayout = new CardLayout();
        topPanel.setLayout(topLayout);

        //button group, only one tool can be selected at a time
        //buttons are radio buttons
        ButtonGroup buttonGroup = new ButtonGroup();

        //add listeners to tools
        for(int i = 0; i < tools.length; i++) {// t: tools) {
            //add an event listener to the tool button
            //to define behavior when tool button is pressed
            tools[i].button.putClientProperty("index", i); //set index of the button
            tools[i].button.addActionListener(new ToolButtonListener(i)); //add listener to button

            //add the tool's top card to the top panel
            topPanel.add(tools[i].upperCard, tools[i].getName());

            //add the tool's button to the button group
            buttonGroup.add(tools[i].button);

            //add the tool's button to the sidebar
            toolsPanel.add(tools[i].button); //add button to panel on sidebar

            //System.out.println("Added " + tools[i].getName());
        }

        /*====== START WITH AN INITIAL TOOL SELECTED ======*/
        int startingToolIndex = getIndexByToolName("Brush");
        setCurrentTool(startingToolIndex); //start with pencil
        //still need to make it initially selected
        tools[startingToolIndex].button.setSelected(true);
    }

    Tool[] getTools() {
        return tools;
    }

    int getIndexByToolName(String toolName) {
        for(int i = 0; i < tools.length; i++) if(tools[i].getName().compareTo(toolName) == 0) return i;
        return 0; //if no matching name return 0
    }

    JPanel getTopPanel(){
        return topPanel;
    }

    void addColorManager(ColorManager colorManager) {
        toolColorPicker.addColorManager(colorManager);
    }

    //allows me to pass an index value through the action listener so I can use it to set the mouse controllers
    private class ToolButtonListener implements ActionListener {
        private int _index;
        ToolButtonListener(int index) {
            _index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            setCurrentTool(_index);
        }
    }

    private void setCurrentTool(int i) {
        //show the tool's top card in the card panel
        topLayout.show(topPanel, tools[i].getName());

        //set the current tool for mouse listeners to use
        mouseStaticController.setTool(i);
        mouseMotionController.setTool(i);
    }
}
