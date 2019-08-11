package com.jpaint;

import javax.swing.*;
import java.awt.*;

/* * * * * * * * *\
 * Andrew Yaros  *
 *    CS 338     *
 *    JPaint     *
\* * * * * * * * */

public class Main {
    private static void setupWindow(int width, int height) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("JPaint");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //main panel
        JPanel mainLayout = new JPanel();
        mainLayout.setLayout(new BorderLayout());
        frame.getContentPane().add(mainLayout);

        /*====================================*/
        //center with image panel
        JPanel imagePanel = new JPanel();
        //container is inside a panel with scrollbars
        JScrollPane imageScrollPanel = new JScrollPane(imagePanel);
        //add to layout
        mainLayout.add(imageScrollPanel, BorderLayout.CENTER);

        //create a view and add it to the panel
        ImageView theView = new ImageView();
        imagePanel.add(theView);

        //create model with a width, a height, and the view
        ImageModel theModel = new ImageModel(width, height, theView);
        //model will refresh the view

        //create the mouse controller and add it to the view
        MouseController mouseController = new MouseController();
        theView.addMouseListener(mouseController);


        //side panel with grid layout
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BorderLayout());
        mainLayout.add(sidePanel, BorderLayout.WEST);

        //sidepanel has a tools panel and a colors panel

        //tools panel
        JPanel toolsPanel = new JPanel();
        toolsPanel.setLayout(new GridLayout(6, 2));
        sidePanel.add(toolsPanel, BorderLayout.NORTH);

        //create tools
        Tool paintbrush = new ToolPaintBrush("Paintbrush", theModel);
        toolsPanel.add(paintbrush);
        Tool paintbucket = new ToolPaintBucket("Paintbucket", theModel);
        toolsPanel.add(paintbucket);

        //colors panel
        JPanel colorsPanel = new JPanel();
        colorsPanel.setLayout(new GridLayout(5, 2));
        sidePanel.add(colorsPanel, BorderLayout.SOUTH);

        //preset colors
        Color[] colorPresets = {
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
        for(int i = 0; i < colorPresets.length; i++) {
            ColorPreset currentPreset = new ColorPreset(colorPresets[i]);
            colorsPanel.add(currentPreset);
        }



        //add a button to the side panel
        //JButton refreshButton = new JButton("Refresh");
        //sidePanel.add(refreshButton);
        //...

        //top panel with card layout
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new CardLayout());
        //...



        //show window
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        setupWindow(512, 342);
        Color green = new Color(255, 123, 234, 12);

        int greenint = green.getARGB();

        Color green2 = new Color(greenint);
        green2.print();
    }



}
