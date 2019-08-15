package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class WindowSetup {
    final static int TOOL_BUTTON_SIZE = 48;
    final static int COLOR_BUTTON_SIZE = 32;
    private final static int MINIMUM_WINDOW_WIDTH = 600;
    private final static int MINIMUM_WINDOW_HEIGHT = 400;

    private ArrayList<Tool> _tools;
    private Tool _currentTool;
    private JPanel _topPanel;
    private CardLayout _topLayout;
    private MouseStaticController _mouseStaticController;
    private MouseMotionController _mouseMotionController;

    WindowSetup(int width, int height) {
        //initial setup
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("JPaint");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //set minimum dimensions
        Dimension minimumSize = new Dimension();
        minimumSize.setSize(MINIMUM_WINDOW_WIDTH, MINIMUM_WINDOW_HEIGHT);
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
        JScrollPane imageScrollPanel = new JScrollPane(imagePanel);
        centerPanel.add(imageScrollPanel, BorderLayout.CENTER);

        //create a view and add it to the panel
        ImageView theView = new ImageView();
        imagePanel.add(theView);

        //create model with a width, a height, and the view
        ImageModel theModel = new ImageModel(width, height, theView);

        /*====== SIDE PANEL ======*/

        //side panel with grid layout
        JPanel sidePanel = new JPanel(new BorderLayout());
        mainLayout.add(sidePanel, BorderLayout.WEST);

        //side panel has a tools panel and a colors panel

        /*====== TOOLS ======*/

        //tools panel
        JPanel toolsPanel = new JPanel(new GridLayout(4, 2));

        //create tools
        _tools = new ArrayList<>();
        _tools.add(new ToolSelect("Select", theModel, "icons/select.png"));
        _tools.add(new ToolColorPicker("Picker", theModel, "icons/colorpicker.png"));
        _tools.add(new ToolPencil("Pencil", theModel, "icons/pencil.png"));
        _tools.add(new ToolPaintBrush("Brush", theModel, "icons/paintbrush.png"));
        _tools.add(new ToolEraser("Eraser", theModel, "icons/eraser.png"));
        _tools.add(new ToolPaintBucket("Bucket", theModel, "icons/paintbucket.png"));
        _tools.add(new ToolLine("Line", theModel, "icons/line.png"));
        _tools.add(new ToolShapes("Shapes", theModel, "icons/shapes.png"));
        _currentTool = _tools.get(2);

        //create both static and motion mouse controllers and add them to the view
        _mouseStaticController = new MouseStaticController(_currentTool); //current tool
        theView.addMouseListener(_mouseStaticController);

        _mouseMotionController = new MouseMotionController(_currentTool); //current tool
        theView.addMouseMotionListener(_mouseMotionController);

        _topPanel = new JPanel();
        _topLayout = new CardLayout();
        _topPanel.setLayout(_topLayout);

        for(Tool t: _tools) {
            //add an event listener to the tool button
            //to define behavior when tool button is pressed
            t._button.addActionListener(e -> {
                //show the tool's top card in the card panel
                _topLayout.show(_topPanel, t.getName());

                //currentTool = t;

                //add tool to mouse listeners
                _mouseStaticController.setTool(t);
                _mouseMotionController.setTool(t);

                //theView.addMouseListener(mouseStaticController);
                //theView.addMouseMotionListener(mouseMotionController);

                //when button pressed, set the current tool
                //setCurrentTool(t); //see private function

            });
            //add the tool's top card to the top panel
            _topPanel.add(t._upperCard, t.getName());
            //add the tool itself to the sidebar
            toolsPanel.add(t._button); //add button to panel on sidebar
            System.out.println("Added " + t.getName());
        }

        //add top panel to the north of the center panel
        centerPanel.add(_topPanel, BorderLayout.NORTH);

        //add toolbar to side panel
        sidePanel.add(toolsPanel, BorderLayout.NORTH);

        //top panel with card layout
        //CardLayout topCards = new CardLayout(); //layout
        //JPanel topPanel = new JPanel(topCards); //panel with layout
        //    for(Tool t: tools) {
        //        toolsPanel.add(t);//add tool as a button to the tools panel on the sidebar
        //        topCards.addLayoutComponent(t._upperCard, t.getName()); //add the tool's top panel to the top card layout
        //    } topCards.show(topPanel, "Pencil");

        //add panels to main layout
        //    sidePanel.add(toolsPanel, BorderLayout.NORTH);
        //    centerPanel.add(topPanel, BorderLayout.NORTH);

        //colors panel
        JPanel colorsPanel = new JPanel(new GridLayout(5, 2));
        sidePanel.add(colorsPanel, BorderLayout.CENTER);

        //default preset colors
        Color[] presetColors = {
                new Color(255,  0,  0,  0), //black
                new Color(255,255,255,255), //white
                new Color(255,255,  0,  0), //red
                new Color(255,255,128,  0), //orange
                new Color(255,255,255,  0), //yellow
                new Color(255,  0,255,  0), //green
                new Color(255,  0,128,  0), //dark green
                new Color(255,  0,255,255), //cyan
                new Color(255,  0,  0,255), //blue
                new Color(255,255,  0,255)  //magenta
        }; //add colors to colorsPanel

        for(int i = 0; i < presetColors.length; i++) {
            colorsPanel.add(new ColorPreset(presetColors[i]), i);
        }

        //color manager for managing selected colors and giving tools access to them
        //created with three initial colors
        //////ColorManager colorManager = new ColorManager(tools, presetColors[0], presetColors[2], presetColors[6]);

        /////tool manager
        /////need class to set current tool?
        /////need to be able to update current tool

        /*====== CONTROLLERS ======*/



        /*====== SHOW WINDOW ======*/
        frame.pack();
        frame.setVisible(true);
    }
}
