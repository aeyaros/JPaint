package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

class ApplicationWindow {
    final static int TOOL_BUTTON_SIZE = 48;
    final static int COLOR_BUTTON_SIZE = 32;
    final static int SELECTED_COLOR_BUTTON_HEIGHT = 64;
    private final static int MINIMUM_WINDOW_WIDTH = 480;
    private final static int MINIMUM_WINDOW_HEIGHT = 470;

    private Tool[] tools;
    private JPanel topPanel;
    private CardLayout topLayout;
    private MouseStaticController mouseStaticController;
    private MouseMotionController mouseMotionController;

    ApplicationWindow() { }

    void WindowSetup(int width, int height) {
        //initial setup
        try {
            UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }
        ////JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("JPaint");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //set minimum dimensions
        Dimension minimumSize = new Dimension(MINIMUM_WINDOW_WIDTH, MINIMUM_WINDOW_HEIGHT);
        frame.setMinimumSize(minimumSize);

        //base panel containing entire layout
        JPanel mainLayout = new JPanel(new BorderLayout());
        frame.getContentPane().add(mainLayout);

        /*====== CENTER ======*/

        //center panel has the top card and the main image panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        mainLayout.add(centerPanel, BorderLayout.CENTER);

        //image panel: a container which is inside a panel with scrollbars
        JPanel imagePanel = new JPanel();

        //image panel is inside a scroll pane
        JScrollPane imageScrollPanel = new JScrollPane(imagePanel);
        centerPanel.add(imageScrollPanel, BorderLayout.CENTER);

        //create a view
        ImageView theView = new ImageView();
        theView.setOpaque(false);

        //add JFrameLabel to the image panel
        imagePanel.add(theView);

        //create model with a width, a height, and the view
        ImageModel theModel = new ImageModel(width, height, theView);

        /*====== SIDE PANEL ======*/

        //side panel with grid layout
        JPanel sidePanel = new JPanel(new BorderLayout());
        Dimension maxSidePanelSize = new Dimension(96,512);
        sidePanel.setMaximumSize(maxSidePanelSize);
        Dimension minSidePanelSize = new Dimension(96,460);
        sidePanel.setMinimumSize(minSidePanelSize);
        mainLayout.add(sidePanel, BorderLayout.WEST);

        //side panel has a tools panel and a colors panel

        /*====== TOOLS ======*/

        //tools panel
        JPanel toolsPanel = new JPanel(new GridLayout(4, 2,0,0));

        //create tools
        //add them to arraylist one by one, then copy to regular array to avoid magic numbers
        ArrayList<Tool> newTools = new ArrayList<>();
        newTools.add(new ToolSelect("Select", theModel, "icons/select.png"));
        newTools.add(new ToolColorPicker("Picker", theModel, "icons/colorpicker.png"));
        newTools.add(new ToolPencil("Pencil", theModel, "icons/pencil.png"));
        newTools.add(new ToolPaintBrush("Brush", theModel, "icons/paintbrush.png"));
        newTools.add(new ToolEraser("Eraser", theModel, "icons/eraser.png"));
        newTools.add(new ToolPaintBucket("Bucket", theModel, "icons/paintbucket.png"));
        newTools.add(new ToolLine("Line", theModel, "icons/line.png"));
        newTools.add(new ToolShapes("Shapes", theModel, "icons/shapes.png"));
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

        //add listeners to tools
        for(int i = 0; i < tools.length; i++) {// t: tools) {
            //add an event listener to the tool button
            //to define behavior when tool button is pressed
            tools[i].button.putClientProperty("index", i); //set index of the button
            tools[i].button.addActionListener(new ToolButtonListener(i)); //add listener to button

            //add the tool's top card to the top panel
            topPanel.add(tools[i].upperCard, tools[i].getName());

            //add the tool itself to the sidebar
            toolsPanel.add(tools[i].button); //add button to panel on sidebar

            System.out.println("Added " + tools[i].getName());
        }

        //add top panel to the north of the center panel
        centerPanel.add(topPanel, BorderLayout.NORTH);

        //add toolbar to side panel
        sidePanel.add(toolsPanel, BorderLayout.NORTH);

        //color presets panel
        JPanel presetPanel = new JPanel(new GridLayout(4, 3,0,0));
        sidePanel.add(presetPanel, BorderLayout.CENTER);

        //selected colors panel
        JPanel selectedColorsPanel = new JPanel(new GridBagLayout());
        sidePanel.add(selectedColorsPanel, BorderLayout.SOUTH);

        //default preset colors
        Color[] presetColors = {
                new Color(255,  0,  0,  0), //black
                new Color(255,128,128,128), //gray
                new Color(255,255,255,255), //white
                new Color(255,255,  0,255), //magenta
                new Color(255,255,  0,  0), //red
                new Color(255,255,128,  0), //orange
                new Color(255,255,255,  0), //yellow
                new Color(255,  0,255,  0), //green
                new Color(255,  0,128,  0), //dark green
                new Color(255,  0,255,255), //cyan
                new Color(255,  0,  0,255), //blue
                new Color(255,128,  0,255)  //purple
        };

        //color manager for managing selected colors and giving tools access to them
        //created with three initial colors
        ColorManager colorManager = new ColorManager(tools, presetPanel, presetColors, selectedColorsPanel);

        /*====== COLOR SCHEME ======*/
        imagePanel.setBackground(new Color(255,128,128,128).getAWT());


        /*====== SHOW WINDOW ======*/
        frame.pack();
        frame.setVisible(true);
    }

    //allows me to pass an index value through the action listener so I can use it to set the mouse controllers
    private class ToolButtonListener implements ActionListener {
        private int _index;
        ToolButtonListener(int index) {
            _index = index;
        }
        @Override public void actionPerformed(ActionEvent e) {
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
