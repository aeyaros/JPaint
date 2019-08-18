package com.jpaint;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class ApplicationWindow {
    //paramaters for certain UI sizes
    final static int TOOL_BUTTON_SIZE = 48;
    private final int NUMBER_TOOL_COLUMNS = 2;
    final static int COLOR_BUTTON_SIZE = 32;
    private final int NUMBER_COLOR_COLUMNS = 3;
    private final int MINIMUM_WINDOW_WIDTH = 500;
    private final int MINIMUM_WINDOW_HEIGHT = 500;
    private final int THIN_EMPTY_BORDER_SIZE = 6;
    private final int WINDOW_CHROME_SIZE = 8;
    private final Color PAGE_BACKGROUND_COLOR = new Color(255,180,180,200);

    //privately accessible resources
    private Tool[] tools;
    private JPanel topPanel;
    private CardLayout topLayout;
    private MouseStaticController mouseStaticController;
    private MouseMotionController mouseMotionController;
    private JLabel coordinatesLabel;
    private ImageModel theModel;

    ApplicationWindow() { }

    void WindowSetup(int width, int height) {
        //initial setup
        JFrame frame = new JFrame("JPaint");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        frame.setMinimumSize(minimumSize);

        //base panel containing entire layout
        JPanel mainLayout = new JPanel(new BorderLayout());
        frame.getContentPane().add(mainLayout);

        /*====== CENTER ======*/

        //center panel has the top card and the main image panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        mainLayout.add(centerPanel, BorderLayout.CENTER);
        mainLayout.setBorder(new EmptyBorder(0,WINDOW_CHROME_SIZE,WINDOW_CHROME_SIZE,WINDOW_CHROME_SIZE));

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

        /*====== SIDE PANEL ======*/

        //side panel with grid layout
        JPanel sidePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weighty = 0.0;
        sidePanel.setBorder(new EmptyBorder(THIN_EMPTY_BORDER_SIZE,0,THIN_EMPTY_BORDER_SIZE,THIN_EMPTY_BORDER_SIZE));
        mainLayout.add(sidePanel, BorderLayout.WEST);

        //side panel has a tools panel and a colors panel

        /*====== TOOLS ======*/

        //create tools
        //add them to arraylist one by one, then copy to regular array to avoid magic numbers
        ArrayList<Tool> newTools = new ArrayList<>();
        newTools.add(new ToolSelect("Select", theModel,
                "icons/select.png", "icons/selected select.png"));

        ToolColorPicker toolColorPicker = new ToolColorPicker("Picker", theModel,
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

        //tools panel

        JPanel toolsPanel = new JPanel(new GridLayout(0, NUMBER_TOOL_COLUMNS,4,4));
        toolsPanel.setBorder(new EmptyBorder(THIN_EMPTY_BORDER_SIZE,THIN_EMPTY_BORDER_SIZE,THIN_EMPTY_BORDER_SIZE,THIN_EMPTY_BORDER_SIZE));

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

        /*====== START WITH PENCIL TOOL SELECTED ======*/
        setCurrentTool(2); //start with pencil
        //still need to make it initially selected
        tools[2].button.setSelected(true);

        //add top panel to the north of the center panel
        centerPanel.add(topPanel, BorderLayout.NORTH);

        //add toolbar to side panel
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        sidePanel.add(toolsPanel, gridBagConstraints);

        //color presets panel
        JPanel presetPanel = new JPanel(new GridLayout(0, NUMBER_COLOR_COLUMNS,0,0));
        presetPanel.setBorder(new EmptyBorder(THIN_EMPTY_BORDER_SIZE,THIN_EMPTY_BORDER_SIZE,THIN_EMPTY_BORDER_SIZE,THIN_EMPTY_BORDER_SIZE));
        gridBagConstraints.anchor = GridBagConstraints.PAGE_START;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        sidePanel.add(presetPanel, gridBagConstraints);

        //selected colors panel
        JPanel selectedColorsPanel = new JPanel(new GridBagLayout());
        selectedColorsPanel.setBorder(new EmptyBorder(THIN_EMPTY_BORDER_SIZE,THIN_EMPTY_BORDER_SIZE,THIN_EMPTY_BORDER_SIZE,THIN_EMPTY_BORDER_SIZE));
        gridBagConstraints.anchor = GridBagConstraints.PAGE_START;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        sidePanel.add(selectedColorsPanel, gridBagConstraints);

        //add padding on bottom of sidebar
        gridBagConstraints.weighty = 1.0;
        sidePanel.add(Box.createVerticalGlue(),gridBagConstraints);

        //color manager for managing selected colors and giving tools access to them
        //created with three initial colors
        ColorManager colorManager = new ColorManager(tools, presetPanel, selectedColorsPanel);
        toolColorPicker.addColorManager(colorManager);

        /*====== MENU BAR ======*/
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);

        //add the menubar
        frame.setJMenuBar(menuBar);

        /*====== MENU ITEMS ======*/
        //file menu
        JMenuItem newItem = new JMenuItem("New");
        KeyStroke newStroke = KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        newItem.setAccelerator(newStroke);
        newItem.setMnemonic(KeyEvent.VK_N);
        fileMenu.add(newItem);

        JMenuItem openItem = new JMenuItem("Open");
        KeyStroke openStroke = KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        openItem.setAccelerator(openStroke);
        openItem.setMnemonic(KeyEvent.VK_O);
        fileMenu.add(openItem);

        fileMenu.addSeparator();

        JMenuItem saveItem = new JMenuItem("Save");
        KeyStroke saveStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        saveItem.setAccelerator(saveStroke);
        saveItem.setMnemonic(KeyEvent.VK_S);
        fileMenu.add(saveItem);

        JMenuItem saveAsItem = new JMenuItem("Save As");
        KeyStroke saveAsStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | InputEvent.SHIFT_DOWN_MASK);
        saveAsItem.setAccelerator(saveAsStroke);
        saveAsItem.setMnemonic(KeyEvent.VK_A);
        fileMenu.add(saveAsItem);

        fileMenu.addSeparator();

        JMenuItem printItem = new JMenuItem("Print");
        KeyStroke printStroke = KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        printItem.setAccelerator(printStroke);
        printItem.setMnemonic(KeyEvent.VK_P);
        fileMenu.add(printItem);

        fileMenu.addSeparator();

        JMenuItem closeItem = new JMenuItem("Close");
        KeyStroke closeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        closeItem.setAccelerator(closeStroke);
        if(Main.IS_MAC) fileMenu.add(closeItem);

        JMenuItem exitItem = new JMenuItem("Quit");
        KeyStroke exitStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        exitItem.setAccelerator(exitStroke);
        exitItem.setMnemonic(KeyEvent.VK_E);
        if(!Main.IS_MAC) fileMenu.add(exitItem);

        //edit menu
        JMenuItem undoItem = new JMenuItem("Undo");
        KeyStroke undoStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        undoItem.setAccelerator(undoStroke);
        undoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        }); editMenu.add(undoItem);


        JMenuItem redoItem = new JMenuItem("Redo");
        KeyStroke redoStroke; //mac is command shift z; windows is control y
        if(Main.IS_MAC) redoStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | InputEvent.SHIFT_DOWN_MASK);
        else redoStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        redoItem.setAccelerator(redoStroke);
        redoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redo();
            }
        }); editMenu.add(redoItem);


        editMenu.addSeparator();

        JMenuItem selectAllItem = new JMenuItem("Select All");
        KeyStroke selectAllStroke = KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        selectAllItem.setAccelerator(selectAllStroke);
        editMenu.add(selectAllItem);

        editMenu.addSeparator();

        JMenuItem cutItem = new JMenuItem("Cut");
        KeyStroke cutStroke = KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        cutItem.setAccelerator(cutStroke);
        editMenu.add(cutItem);

        JMenuItem copyItem = new JMenuItem("Copy");
        KeyStroke copyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        copyItem.setAccelerator(copyStroke);
        editMenu.add(copyItem);

        JMenuItem pasteItem = new JMenuItem("Paste");
        KeyStroke pasteStroke = KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        pasteItem.setAccelerator(pasteStroke);
        editMenu.add(pasteItem);

        /*====== COLOR SCHEME ======*/
        imagePanel.setBackground(PAGE_BACKGROUND_COLOR.getAWT());

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

    private void updateSizeLabel(JLabel label, int w, int h) {
        label.setText("Width: " + w + ", Height: " + h);
    }

    void undo() { theModel.undo(); }
    void redo() { theModel.redo(); }
}
