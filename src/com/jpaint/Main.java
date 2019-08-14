package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/* * * * * * * * *\
 * Andrew Yaros  *
 *    CS 338     *
 *    JPaint     *
\* * * * * * * * */

public class Main {
    final static int TOOL_BUTTON_SIZE = 48;
    final static int COLOR_BUTTON_SIZE = 48;
    private final static int MINIMUM_WINDOW_WIDTH = 600;
    private final static int MINIMUM_WINDOW_HEIGHT = 400;


    private static void setupWindow(int width, int height) {
        //initial setup
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("JPaint");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //set minimum dimensions
        Dimension minimumSize = new Dimension();
        minimumSize.setSize(Main.MINIMUM_WINDOW_WIDTH, Main.MINIMUM_WINDOW_HEIGHT);
        frame.setMinimumSize(minimumSize);


        //base panel
        JPanel mainLayout = new JPanel();
        mainLayout.setLayout(new BorderLayout());
        frame.getContentPane().add(mainLayout);


        /*====== CENTER ======*/


        //center has the top card and the main image panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        mainLayout.add(centerPanel, BorderLayout.CENTER);


        //image panel
        JPanel imagePanel = new JPanel();
        //container is inside a panel with scrollbars
        JScrollPane imageScrollPanel = new JScrollPane(imagePanel);
        //add to center panel
        centerPanel.add(imageScrollPanel, BorderLayout.CENTER);


        //create a view and add it to the panel
        ImageView theView = new ImageView();
        imagePanel.add(theView);


        //create model with a width, a height, and the view
        ImageModel theModel = new ImageModel(width, height, theView);
        //model will refresh the view


        /*====== SIDE PANEL ======*/


        //side panel with grid layout
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BorderLayout());
        mainLayout.add(sidePanel, BorderLayout.WEST);


        //sidepanel has a tools panel and a colors panel


        //tools panel
        JPanel toolsPanel = new JPanel();
        toolsPanel.setLayout(new GridLayout(2, 2));

        /*====== TOOLS ARRAY ======*/

        //create tools
        ArrayList<Tool> tools= new ArrayList<>();
        tools.add(new ToolPencil("Pencil", theModel, "icons/pencil.png"));
        tools.add(new ToolColorPicker("Picker", theModel, "icons/colorpicker.png"));
        tools.add(new ToolPaintBrush("Brush", theModel, "icons/paintbrush.png"));
        tools.add(new ToolPaintBucket("Bucket", theModel, "icons/paintbucket.png"));
        //add the tools to the tools panel on the sidebar
        for(int i = 0; i < tools.size(); i++) {
            toolsPanel.add(tools.get(i), i);
        }
        sidePanel.add(toolsPanel, BorderLayout.NORTH);


        //colors panel
        JPanel colorsPanel = new JPanel();
        colorsPanel.setLayout(new GridLayout(5, 2));
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
        };
        //add colors to colorsPanel
        for(int i = 0; i < presetColors.length; i++) {
            colorsPanel.add(new ColorPreset(presetColors[i]), i);
        }

        //color manager for managing selected colors and giving tools access to them
        //created with three initial colors
        ColorManager colorManager = new ColorManager(tools, presetColors[0], presetColors[2], presetColors[1]);
        colorManager.notifyTools();
        /*====== TOP PANEL ======*/
        //top panel with card layout
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new CardLayout());
        centerPanel.add(topPanel, BorderLayout.NORTH);

        Dimension topCardMinimum = new Dimension();
        topCardMinimum.setSize(200, 128);
        topPanel.setMinimumSize(topCardMinimum);
        topPanel.add(new JButton("lol"));

        /*====== CONTROLLERS ======*/
        //create both static and motion mouse controllers and add them to the view
        MouseStaticController mouseStaticController = new MouseStaticController(tools.get(0));
        MouseMotionController mouseMotionController = new MouseMotionController(tools.get(0));

        theView.addMouseListener(mouseStaticController);
        theView.addMouseMotionListener(mouseMotionController);


        /*====== SHOW WINDOW ======*/
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        setupWindow(512, 384);
        /* Color green = new Color(255, 123, 234, 12);
        int greenint = green.getARGB();
        Color green2 = new Color(greenint); green2.print(); */
    }



}
