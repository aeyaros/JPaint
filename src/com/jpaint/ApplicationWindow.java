package com.jpaint;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class ApplicationWindow {
    //paramaters for certain UI sizes
    final static int TOOL_BUTTON_SIZE = 48;
    final static int COLOR_BUTTON_SIZE = 32;
    private final int TOOL_BUTTON_GAP = 4;
    private final int NUMBER_TOOL_COLUMNS = 2;
    private final int NUMBER_COLOR_COLUMNS = 3;
    private final int MINIMUM_WINDOW_WIDTH = 560;
    private final int MINIMUM_WINDOW_HEIGHT = 560;
    private final int THIN_EMPTY_BORDER_SIZE = 6;
    private final int WINDOW_CHROME_SIZE = 8;
    private final Color PAGE_BACKGROUND_COLOR = new Color(255,180,180,200);

    private JLabel coordinatesLabel;
    private ImageModel theModel;

    ApplicationWindow() { }

    void WindowSetup(int width, int height) {
        //initial setup
        JFrame mainFrame = new JFrame("JPaint");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if(Main.IS_MAC) { //mac specific stuff
            System.setProperty("apple.laf.useScreenMenuBar", "true"); //put menu at top of screen

            //these dont really work; I need to do plist stuff; low priority for now
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "JPaint"); //set title
            System.setProperty("apple.awt.application.name", "JPaint");
        }

        try { //I have to do this after setting the app title or it will revert to the class name
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
            /* //UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName()); // */
        } catch (Exception e) { e.printStackTrace(); }
        /* // JFrame.setDefaultLookAndFeelDecorated(true); // */

        //set minimum dimensions
        Dimension minimumSize = new Dimension(MINIMUM_WINDOW_WIDTH, MINIMUM_WINDOW_HEIGHT);
        mainFrame.setMinimumSize(minimumSize);

        //base panel containing entire layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainFrame.getContentPane().add(mainPanel);

        /*====== CENTER ======*/

        //center panel has the top card and the main image panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.setBorder(new EmptyBorder(0,WINDOW_CHROME_SIZE,WINDOW_CHROME_SIZE,WINDOW_CHROME_SIZE));

        //image panel: a container which is inside a panel with scrollbars
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(java.awt.Color.white);

        //image panel is inside a scroll pane
        JScrollPane imageScrollPanel = new JScrollPane(imagePanel);
        //imageScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //imageScrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        imageScrollPanel.setBorder(null);
        centerPanel.add(imageScrollPanel, BorderLayout.CENTER);

        //create a view
        //first make label that will be used in the bottom bar to display coordinates
        coordinatesLabel = new JLabel("  ");
        //then create view
        ImageView theView = new ImageView(coordinatesLabel);
        theView.setOpaque(false);
        theView.setBorder(null);
        imagePanel.add(theView);

        //create model with a width, a height, and the view
        theModel = new ImageModel(width, height, theView);

        //bottom bar
        JPanel bottomBar = new JPanel();
        bottomBar.setLayout(new BorderLayout());
        bottomBar.setBorder(BorderFactory.createEmptyBorder(WINDOW_CHROME_SIZE,0,0,0));
        centerPanel.add(bottomBar, BorderLayout.SOUTH);

        JPanel coordinatesPanel = new JPanel(new BorderLayout());
        coordinatesPanel.add(coordinatesLabel);
        //create glue to stick first label to the left
        bottomBar.add(Box.createHorizontalGlue());
        bottomBar.add(coordinatesPanel, BorderLayout.WEST);

        //do not put glue between these labels so they remain apart

        JPanel sizePanel = new JPanel();
        JLabel sizeLabel = new JLabel(" ");
        sizePanel.add(sizeLabel);
        updateSizeLabel(sizeLabel, width, height);
        bottomBar.add(sizeLabel, BorderLayout.EAST);
        //add glue to stick this to the right
        bottomBar.add(Box.createHorizontalGlue());

        /*====== CREATE SIDE PANEL ======*/

        //side panel with grid layout
        JPanel sidePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weighty = 0.0;
        sidePanel.setBorder(new EmptyBorder(THIN_EMPTY_BORDER_SIZE,0,THIN_EMPTY_BORDER_SIZE,THIN_EMPTY_BORDER_SIZE));
        mainPanel.add(sidePanel, BorderLayout.WEST);


        /*====== ADD CONTROLS TO SIDE PANEL ======*/

        //using gridbag layout, so I have to set all constraints initially
        // and then change y value each time I add something
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.gridx = 0; //x - will remain 0
        EmptyBorder sidePanelBorder = new EmptyBorder(THIN_EMPTY_BORDER_SIZE,THIN_EMPTY_BORDER_SIZE,THIN_EMPTY_BORDER_SIZE,THIN_EMPTY_BORDER_SIZE);

        //tools panel
        JPanel toolsPanel = new JPanel(new GridLayout(0, NUMBER_TOOL_COLUMNS,TOOL_BUTTON_GAP,TOOL_BUTTON_GAP));
        toolsPanel.setBorder(sidePanelBorder);
        gridBagConstraints.gridy = 0; //y - will be changed as I add components
        sidePanel.add(toolsPanel, gridBagConstraints);

        //color presets panel
        JPanel presetPanel = new JPanel(new GridLayout(0, NUMBER_COLOR_COLUMNS,0,0));
        presetPanel.setBorder(sidePanelBorder);
        gridBagConstraints.gridy = 1;
        sidePanel.add(presetPanel, gridBagConstraints);

        //opacity slider panel
        JPanel opacityPanel = new JPanel();
        BoxLayout opacityLayout = new BoxLayout(opacityPanel, BoxLayout.Y_AXIS);
        opacityPanel.setLayout(opacityLayout);
        opacityPanel.setBorder(sidePanelBorder);
        gridBagConstraints.gridy = 2;
        sidePanel.add(opacityPanel, gridBagConstraints);

        //selected colors panel
        JPanel selectedColorsPanel = new JPanel(new GridBagLayout());
        selectedColorsPanel.setBorder(sidePanelBorder);
        gridBagConstraints.gridy = 3;
        sidePanel.add(selectedColorsPanel, gridBagConstraints);

        //add empty padding at bottom of sidebar
        gridBagConstraints.weighty = 1.0;
        sidePanel.add(Box.createVerticalGlue(),gridBagConstraints);

        /*====== SET UP TOOLS ======*/

        //class for managing tools
        ToolsManager toolsManager = new ToolsManager(toolsPanel, theModel, theView);

        //add top panel to the north of the center panel
        centerPanel.add(toolsManager.getTopPanel(), BorderLayout.NORTH);

        /*====== SET UP COLORS ======*/

        //color manager for managing selected colors and giving tools access to them
        //created with three initial colors
        ColorManager colorManager = new ColorManager(toolsManager.getTools(), presetPanel, selectedColorsPanel, opacityPanel);
        toolsManager.addColorManager(colorManager);

        /*====== MENU BAR ======*/

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);

        //add the menubar
        mainFrame.setJMenuBar(menuBar);

        /*====== MENU ITEMS ======*/

        ArrayList<MenuItem> menuItems = new ArrayList<>();

        int cmdCtrlModifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        int cmdCtrlShiftModifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | InputEvent.SHIFT_DOWN_MASK;

        //file menu
        menuItems.add(new MenuItem("New", KeyEvent.VK_N, cmdCtrlModifier, fileMenu, KeyEvent.VK_N, e -> dummy()));
        menuItems.add(new MenuItem("Open", KeyEvent.VK_O, cmdCtrlModifier, fileMenu, KeyEvent.VK_O,e -> dummy()));
        menuItems.add(new MenuItem("Save", KeyEvent.VK_S, cmdCtrlModifier, fileMenu, KeyEvent.VK_S,e -> dummy()));
        menuItems.add(new MenuItem("Save As", KeyEvent.VK_S, cmdCtrlShiftModifier, fileMenu, KeyEvent.VK_A,e -> dummy()));
        fileMenu.addSeparator();
        menuItems.add(new MenuItem("Print", KeyEvent.VK_P, cmdCtrlModifier, fileMenu, KeyEvent.VK_P,e -> dummy()));
        fileMenu.addSeparator();
        if(Main.IS_MAC) menuItems.add(new MenuItem("Close", KeyEvent.VK_W, cmdCtrlModifier, fileMenu, KeyEvent.VK_W,e -> dummy()));
        else menuItems.add(new MenuItem("Exit", KeyEvent.VK_E, cmdCtrlModifier, fileMenu, KeyEvent.VK_E,e -> dummy()));

        //edit menu
        menuItems.add (new MenuItem("Undo", KeyEvent.VK_Z, cmdCtrlModifier, editMenu, KeyEvent.VK_Z, e -> undo()));

        MenuItem redoItem;
        if(Main.IS_MAC) redoItem = new MenuItem("Redo", KeyEvent.VK_Z, cmdCtrlShiftModifier, editMenu, KeyEvent.VK_Z,e -> redo());
        else redoItem = new MenuItem("Redo", KeyEvent.VK_Y, cmdCtrlModifier, editMenu, KeyEvent.VK_Y,e -> redo());
        menuItems.add(redoItem);

        editMenu.addSeparator();

        editMenu.add(new MenuItem("Select All", KeyEvent.VK_A, cmdCtrlModifier, editMenu, KeyEvent.VK_A, e -> dummy()));

        editMenu.addSeparator();

        editMenu.add(new MenuItem("Cut", KeyEvent.VK_X, cmdCtrlModifier, editMenu, KeyEvent.VK_X, e -> dummy()));
        editMenu.add(new MenuItem("Copy", KeyEvent.VK_C, cmdCtrlModifier, editMenu, KeyEvent.VK_C, e -> dummy()));
        editMenu.add(new MenuItem("Paste", KeyEvent.VK_V, cmdCtrlModifier, editMenu, KeyEvent.VK_V, e -> dummy()));

        /*====== COLOR SCHEME ======*/
        imagePanel.setBackground(PAGE_BACKGROUND_COLOR.getAWT());

        /*====== SHOW WINDOW ======*/
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    private void updateSizeLabel(JLabel label, int w, int h) {
        label.setText("Width: " + w + ", Height: " + h);
    }

    void undo() { theModel.undo(); }
    void redo() { theModel.redo(); }

    void dummy() {} //temporary for menu listeners
}
