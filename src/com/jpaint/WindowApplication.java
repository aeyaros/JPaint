package com.jpaint;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Hashtable;

class WindowApplication {
    private final String APPLICATION_NAME = "JPaint";
    private final String NEW_DOCUMENT = "New Document";
    //parameters for certain UI sizes
    final static int TOOL_BUTTON_SIZE = 48;
    final static int COLOR_BUTTON_SIZE = 32;
    private final int TOOL_BUTTON_GAP = 4;
    private final int NUMBER_TOOL_COLUMNS = 2;
    private final int NUMBER_COLOR_COLUMNS = 3;
    private final int MINIMUM_WINDOW_WIDTH = 560;
    private final int MINIMUM_WINDOW_HEIGHT = 560;
    private final int THIN_EMPTY_BORDER_SIZE = 6;
    private final int WINDOW_CHROME_SIZE = 8;
    private final Color PAGE_BACKGROUND_COLOR = new Color(255, 180, 180, 200);

    //windows
    private JFrame mainFrame;

    //labels
    private JLabel coordinatesLabel;
    private JLabel sizeLabel;
    private ImageModel theModel;
    private ImageView theView;

    //path of opened/saved file
    private File theFile;

    WindowApplication() {
    }

    void WindowSetup(int width, int height) {
        //initial setup
        mainFrame = new JFrame(APPLICATION_NAME);
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //ask to save when closing window
        mainFrame.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }

            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        if (Main.IS_MAC) { //mac specific stuff
            System.setProperty("apple.laf.useScreenMenuBar", "true"); //put menu at top of screen

            //these dont really work; I need to do plist stuff; low priority for now
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "JPaint"); //set title
            System.setProperty("apple.awt.application.name", "JPaint");
        }

        try { //I have to do this after setting the app title or it will revert to the class name
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName()); // */
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        mainPanel.setBorder(new EmptyBorder(WINDOW_CHROME_SIZE, WINDOW_CHROME_SIZE, WINDOW_CHROME_SIZE, WINDOW_CHROME_SIZE));

        //image panel: a container which is inside a panel with scrollbars
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(java.awt.Color.gray);

        //image panel is inside a scroll pane
        JScrollPane imageScrollPanel = new JScrollPane(imagePanel);
        //imageScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //imageScrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        imageScrollPanel.setBorder(null);
        centerPanel.add(imageScrollPanel, BorderLayout.CENTER);

        //create a view
        //first make label that will be used in the bottom bar to display coordinates
        coordinatesLabel = new JLabel("  ");
        sizeLabel = new JLabel(" ");

        //then create view
        theView = new ImageView(width, height, coordinatesLabel, sizeLabel);
        theView.setOpaque(false);
        theView.setBorder(null);
        imagePanel.add(theView);

        //create model with a width, a height, and the view
        theModel = new ImageModel(width, height, theView);

        //bottom bar
        JPanel bottomBar = new JPanel();
        bottomBar.setLayout(new BorderLayout());
        bottomBar.setBorder(BorderFactory.createEmptyBorder(WINDOW_CHROME_SIZE, 0, 0, 0));
        centerPanel.add(bottomBar, BorderLayout.SOUTH);

        JPanel coordinatesPanel = new JPanel(new BorderLayout());
        coordinatesPanel.add(coordinatesLabel);
        //create glue to stick first label to the left
        bottomBar.add(Box.createHorizontalGlue());
        bottomBar.add(coordinatesPanel, BorderLayout.WEST);
        //do not put glue between these labels so they remain apart
        bottomBar.add(sizeLabel, BorderLayout.EAST);
        //add glue to stick this to the right
        bottomBar.add(Box.createHorizontalGlue());

        /*====== CREATE SIDE PANEL ======*/

        //side panel with grid layout
        JPanel sidePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.gridx = 0; //x - will remain 0

        sidePanel.setBorder(new EmptyBorder(0, 0, THIN_EMPTY_BORDER_SIZE, THIN_EMPTY_BORDER_SIZE));
        mainPanel.add(sidePanel, BorderLayout.WEST);


        /*====== ADD CONTROLS TO SIDE PANEL ======*/

        //using gridbag layout, so I have to set all constraints initially
        // and then change y value each time I add something
        EmptyBorder sidePanelBorder = new EmptyBorder(THIN_EMPTY_BORDER_SIZE, THIN_EMPTY_BORDER_SIZE, THIN_EMPTY_BORDER_SIZE, THIN_EMPTY_BORDER_SIZE);

        //tools panel
        JPanel toolsPanel = new JPanel(new GridLayout(0, NUMBER_TOOL_COLUMNS, TOOL_BUTTON_GAP, TOOL_BUTTON_GAP));
        toolsPanel.setBorder(sidePanelBorder);
        gridBagConstraints.gridy = 0; //y - will be changed as I add components
        sidePanel.add(toolsPanel, gridBagConstraints);

        //color presets panel
        JPanel presetPanel = new JPanel(new GridLayout(0, NUMBER_COLOR_COLUMNS, 0, 0));
        presetPanel.setBorder(sidePanelBorder);
        gridBagConstraints.gridy = 1;
        sidePanel.add(presetPanel, gridBagConstraints);

        //opacity slider panel
        JPanel opacityPanel = new JPanel();
        BoxLayout opacityLayout = new BoxLayout(opacityPanel, BoxLayout.Y_AXIS);
        opacityPanel.setLayout(opacityLayout);
        opacityPanel.setBorder(new EmptyBorder(THIN_EMPTY_BORDER_SIZE * 3, THIN_EMPTY_BORDER_SIZE, 0, THIN_EMPTY_BORDER_SIZE));
        gridBagConstraints.gridy = 2;
        sidePanel.add(opacityPanel, gridBagConstraints);

        //selected colors panel
        JPanel selectedColorsPanel = new JPanel(new GridBagLayout());
        selectedColorsPanel.setBorder(new EmptyBorder(0, THIN_EMPTY_BORDER_SIZE, THIN_EMPTY_BORDER_SIZE, THIN_EMPTY_BORDER_SIZE));
        gridBagConstraints.gridy = 3;
        sidePanel.add(selectedColorsPanel, gridBagConstraints);

        //add empty padding at bottom of sidebar
        gridBagConstraints.weighty = 1.0;
        sidePanel.add(Box.createVerticalGlue(), gridBagConstraints);

        /*====== SET UP TOOLS ======*/

        //class for managing tools
        ManageTools manageTools = new ManageTools(toolsPanel, theModel, theView);

        //add top panel to the north of the center panel
        centerPanel.add(manageTools.getTopPanel(), BorderLayout.NORTH);

        /*====== SET UP COLORS ======*/

        //color manager for managing selected colors and giving tools access to them
        //created with three initial colors
        ManageColors manageColors = new ManageColors(manageTools.getTools(), mainFrame, presetPanel, selectedColorsPanel, opacityPanel);
        manageTools.addColorManager(manageColors);


        /*====== MENU BAR ======*/

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);

        JMenu transformMenu = new JMenu("Transform");
        menuBar.add(transformMenu);

        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);

        //add the menubar
        mainFrame.setJMenuBar(menuBar);

        /*====== MENU ITEMS ======*/

        Hashtable<String, MenuItem> menuItems = new Hashtable<>();

        int cmdCtrlModifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        int cmdCtrlShiftModifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | InputEvent.SHIFT_DOWN_MASK;

        //file menu
        menuItems.put("new", new MenuItem("New Opaque Image", KeyEvent.VK_N, cmdCtrlModifier, fileMenu, KeyEvent.VK_N, e -> newFile(false)));
        menuItems.put("newtrans", new MenuItem("New Transparent Image", KeyEvent.VK_N, cmdCtrlShiftModifier, fileMenu, KeyEvent.VK_T, e -> newFile(true)));
        fileMenu.addSeparator();

        menuItems.put("open", new MenuItem("Open", KeyEvent.VK_O, cmdCtrlModifier, fileMenu, KeyEvent.VK_O, e -> openFile()));
        menuItems.put("save", new MenuItem("Save", KeyEvent.VK_S, cmdCtrlModifier, fileMenu, KeyEvent.VK_S, e -> save()));
        menuItems.put("saveas", new MenuItem("Save As", KeyEvent.VK_S, cmdCtrlShiftModifier, fileMenu, KeyEvent.VK_A, e -> saveas()));
        fileMenu.addSeparator();

        menuItems.put("print", new MenuItem("Print", KeyEvent.VK_P, cmdCtrlModifier, fileMenu, KeyEvent.VK_P, e -> print()));
        fileMenu.addSeparator();

        if (Main.IS_MAC)
            menuItems.put("close", new MenuItem("Close", KeyEvent.VK_W, cmdCtrlModifier, fileMenu, KeyEvent.VK_W, e -> exit()));
        else
            menuItems.put("exit", new MenuItem("Exit", KeyEvent.VK_E, cmdCtrlModifier, fileMenu, KeyEvent.VK_E, e -> exit()));

        //edit menu
        menuItems.put("undo", new MenuItem("Undo", KeyEvent.VK_Z, cmdCtrlModifier, editMenu, KeyEvent.VK_Z, e -> undo()));
        MenuItem redoItem; //shortcut changes depending on platform
        if (Main.IS_MAC)
            redoItem = new MenuItem("Redo", KeyEvent.VK_Z, cmdCtrlShiftModifier, editMenu, KeyEvent.VK_Z, e -> redo());
        else redoItem = new MenuItem("Redo", KeyEvent.VK_Y, cmdCtrlModifier, editMenu, KeyEvent.VK_Y, e -> redo());
        menuItems.put("redo", redoItem);
        editMenu.addSeparator();

        menuItems.put("selectall", new MenuItem("Select All", KeyEvent.VK_A, cmdCtrlModifier, editMenu, KeyEvent.VK_A, e -> dummy()));
        editMenu.addSeparator();

        menuItems.put("cut", new MenuItem("Cut", KeyEvent.VK_X, cmdCtrlModifier, editMenu, KeyEvent.VK_X, e -> dummy()));
        menuItems.put("copy", new MenuItem("Copy", KeyEvent.VK_C, cmdCtrlModifier, editMenu, KeyEvent.VK_C, e -> dummy()));
        menuItems.put("paste", new MenuItem("Paste", KeyEvent.VK_V, cmdCtrlModifier, editMenu, KeyEvent.VK_V, e -> dummy()));

        //transform menu
        menuItems.put("resize", new MenuItem("Resize", transformMenu, KeyEvent.VK_R, e -> resize()));
        transformMenu.addSeparator();

        menuItems.put("fliph", new MenuItem("Flip Image Horizontally", transformMenu, KeyEvent.VK_H, e -> flip(true)));
        menuItems.put("flipv", new MenuItem("Flip Image Vertically", transformMenu, KeyEvent.VK_V, e -> flip(false)));
        transformMenu.addSeparator();

        menuItems.put("rotateleft", new MenuItem("Rotate Left 90\u00B0", transformMenu, KeyEvent.VK_L, e -> rotate(0)));
        menuItems.put("rotateright", new MenuItem("Rotate Right 90\u00B0", transformMenu, KeyEvent.VK_R, e -> rotate(1)));
        menuItems.put("rotate180", new MenuItem("Rotate 180\u00B0", transformMenu, KeyEvent.VK_U, e -> rotate(2)));


        menuItems.put("about", new MenuItem("About " + APPLICATION_NAME, helpMenu, KeyEvent.VK_U, e -> {
            Image image = null;
            try {
                image = ImageIO.read(getClass().getResource("icons/appicon.png"));
            } catch (IOException ex) {
                ex.printStackTrace();
            } try{
                image = image.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            } catch (NullPointerException ignored) { }
            JOptionPane.showMessageDialog(mainFrame, APPLICATION_NAME +
                            " is a fun painting program inspired by Apple's MacPaint (from 1984) \n" +
                            "and old versions of Microsoft Paint. Created by Andrew Yaros in 2019, \n" +
                            "originally for the CS 338 (GUIs) class at Drexel University.",
                    "About " + APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(image));
        }));

        /*====== COLOR SCHEME ======*/
        imagePanel.setBackground(PAGE_BACKGROUND_COLOR.getAWT());

        /*====== SET TITLE ======*/
        setTitle(NEW_DOCUMENT);

        /*====== SHOW WINDOW ======*/
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null); //center image
        mainFrame.setVisible(true);
    }

    //either saves or doesnt save
    //returns false if canceled, otherwise returns true
    private boolean askToSave() {
        System.out.println("asking to save");
        //check to see if file was edited
        if (theModel.isUntouched()) {
            return true; //not asking to save since user hasn't done anything
        } //else, we need to ask if user wants to save
        Object[] options = {"Save",
                "Don't Save",
                "Cancel"};
        int result = JOptionPane.showOptionDialog(mainFrame,
                "Do you want to save your changes to this file?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]);
        switch (result) {
            case 0:
                save(); //if save option chosen save the file
                return true; //go to next action
            case 1:
                return true;//if dont save was chosen go to next action without saving
            default:
                return false; //if cancel was chosen dont go to next action
        }
    }

    private void exit() {
        if (askToSave()) System.exit(0);
    }

    //undo/redo
    private void undo() {
        theModel.undo();
    }

    private void redo() {
        theModel.redo();
    }

    private void newFile(boolean transparent) {
        if (askToSave()) {
            theModel.startOverFromScratch(Main.DEFAULT_WINDOW_WIDTH, Main.DEFAULT_WINDOW_HEIGHT, transparent);
            theFile = null;
            setTitle(NEW_DOCUMENT);
        }
    }

    //open a file
    private void openFile() {
        String extension = "png";

        //if user clicks the cancel button and declines to save
        if (!askToSave()) return; // then dont open a file

        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter imageFilters = new FileNameExtensionFilter("PNG files", extension);
        fileChooser.setFileFilter(imageFilters);

        int returnVal = fileChooser.showOpenDialog(mainFrame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            //put stuff here
            try {
                theModel.startOverFromImage(ImageIO.read(file));
                System.out.println("Read file from:" + file.getAbsolutePath());

                //file was opened, and thus was previously saved
                theModel.setSaved();
                //file was opened, save file object
                theFile = file;
                setTitle(theFile.getName());

                //done
            } catch (Exception e) {
                JOptionPane.showMessageDialog(mainFrame,
                        "The file \"" + file.getName() + "\" couldn't be opened. Please try again with a different file.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                System.out.println("Couldn't open file.");
            }
        } else {
            System.out.println("Open command cancelled by user.");
        }
    }

    private void save() {
        if (!theModel.isSaved()) saveas(); //if not saved, save the file as something
            //if model was already saved and still exists then just write it
        else if (theFile.exists()) { //if the file was saved and exists, then just write to it
            writeImageToFile(theFile);
            //done
        } else { //otherwise, file was saved but doesn't exist anymore so you have to save as
            JOptionPane.showMessageDialog(mainFrame,
                    "The file \"" + theFile.getName() + "\" no longer exists and may have been deleted. Please save as a new file.",
                    "File not found",
                    JOptionPane.ERROR_MESSAGE);
            saveas();
        }
    }

    private void saveas() {
        String extension = "png";

        //open save dialog box
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter imageFilters = new FileNameExtensionFilter("PNG files", "png");
        fileChooser.setFileFilter(imageFilters);

        if (theFile != null) fileChooser.setSelectedFile(theFile);
        else {
            File fileToSave = new File("New image" + "." + extension);
            fileChooser.setSelectedFile(fileToSave);
        }

        int returnVal = fileChooser.showSaveDialog(mainFrame);
        //then write to image file
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getAbsolutePath().endsWith(extension)) {
                file = new File(file.getAbsolutePath() + "." + extension);
            }
            //put stuff here
            writeImageToFile(file);
            //done
        } else {
            System.out.println("Save command cancelled by user.");
        }
    }

    private void writeImageToFile(File outputfile) {
        try {
            // retrieve image
            BufferedImage imageToSave = theModel.getImage();

            ImageIO.write(imageToSave, "png", outputfile);

            System.out.println("Saved image to file:" + outputfile.getAbsolutePath());

            //file was successfully saved, so we mark as saved
            theModel.setSaved();
            //file was saved and as of this state is untouched
            theModel.setUntouched();
            //file was saved, keep object here
            theFile = outputfile;

            //set tile to new filename
            setTitle(theFile.getName());

            //done
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame,
                    "The file \"" + outputfile.getName() + "\" couldn't be saved. Please try again in a different location or as a different file type.",
                    "Error Saving File",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println("Couldn't open file.");
        }

    }

    //not done yet
    private void print() {
        //PrinterJob newPrintJob = PrinterJob.getPrinterJob();
    }

    private class ResizeDialog extends JDialog {
        JTextField widthField;
        JTextField heightField;
        final int MAX_INTEGER_DIGITS = 5; //not sure if I can handle 100,000 pixels :/
        int MAX_RESIZE = (int) Math.pow(10, MAX_INTEGER_DIGITS);

        ResizeDialog() {
            super(mainFrame, "Resize Image", true);
            //set up small modal dialog box
            //JDialog resizeDiag = new JDialog(mainFrame, "Resize Image", true);
            this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            this.setResizable(false);
            JPanel main = new JPanel(new BorderLayout());
            main.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            this.getContentPane().add(main);

            //message
            JLabel message = new JLabel("Enter the new dimensions (in pixels):");
            message.setBorder(BorderFactory.createEmptyBorder(0, 0, 17, 0));
            main.add(message, BorderLayout.NORTH);

            //input fields, labels

            //we want integers only
            DecimalFormat intFormat = new DecimalFormat();
            intFormat.setParseIntegerOnly(true);
            intFormat.setMaximumFractionDigits(0);
            intFormat.setMinimumFractionDigits(0);
            intFormat.setMinimumIntegerDigits(1);
            intFormat.setMaximumIntegerDigits(MAX_INTEGER_DIGITS);

            JPanel fields = new JPanel(new GridLayout(0, 1));
            JPanel labels = new JPanel(new GridLayout(0, 1));
            widthField = new JTextField(MAX_INTEGER_DIGITS);
            heightField = new JTextField(MAX_INTEGER_DIGITS);

            setFieldsToDefault();

            JLabel widthLabel = new JLabel("Width:");
            JLabel heightLabel = new JLabel("Height:");
            fields.add(widthField);
            fields.add(heightField);
            labels.add(widthLabel);
            labels.add(heightLabel);

            main.add(labels, BorderLayout.CENTER);
            main.add(fields, BorderLayout.LINE_END);

            //buttons
            JButton applyButton = new JButton("Apply");
            JButton cancelButton = new JButton("Cancel");
            JPanel buttons = new JPanel(new GridLayout(0, 2, 6, 0));
            buttons.setBorder(BorderFactory.createEmptyBorder(17, 0, 0, 0));

            buttons.add(applyButton);
            buttons.add(cancelButton);
            cancelButton.addActionListener(e -> close());
            applyButton.addActionListener(e -> apply());
            main.add(buttons, BorderLayout.SOUTH);

            this.pack();
            this.setLocationRelativeTo(mainFrame);
            this.setVisible(true);
        }

        void setFieldsToDefault() {
            widthField.setText(Integer.toString(theModel.getWidth()));
            heightField.setText(Integer.toString(theModel.getHeight()));
        }

        void close() {
            try {
                this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void apply() {
            //check inputs
            try {
                int newW = Integer.parseInt(widthField.getText());
                int newH = Integer.parseInt(heightField.getText());

                //check bounds of input
                if (newW <= 0 || newH <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "You entered a negative number. Please only type positive whole numbers.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    setFieldsToDefault();
                } else if (newW > MAX_RESIZE || newH > MAX_RESIZE) {
                    JOptionPane.showMessageDialog(this,
                            "You entered a very large number! Please only type numbers less than" + MAX_RESIZE + ".",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    setFieldsToDefault();
                } else { //if input within bounds
                    //check if image will be cropped (a dimension is smaller than the previous dimension)
                    if (newW < theModel.getWidth() || newH < theModel.getHeight()) {
                        //if crop, ask for confirmation
                        //Custom button text
                        Object[] options = {"OK",
                                "Cancel"};
                        int result = JOptionPane.showOptionDialog(this,
                                "The image will be cropped. Continue?",
                                "Alert",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE,
                                null,
                                options,
                                options[1]);
                        if (result == 0) { //if OK option chosen
                            theModel.resize(newW, newH); //then resize
                            close(); //then close main dialog
                            //done
                        }
                    } else { //no danger of cropping
                        theModel.resize(newW, newH); //then resize
                        close(); //then close main dialog
                        //done
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "You didn't type a number. Please only type positive whole numbers.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                setFieldsToDefault();
            }
        }
    }

    private void resize() {
        new ResizeDialog();
    }

    private void flip(boolean horizontally) {
        if (horizontally) theModel.flip(0);
        else theModel.flip(1);
    }

    private void rotate(int option) {
        switch (option) {
            case 0:
                theModel.rotate(0);
                break;//left
            case 1:
                theModel.rotate(1);
                break;//right
            case 2:
                theModel.rotate(2);
                break;//180
            default:
                break;
        }
    }

    private void setTitle(String documentTitle) {
        mainFrame.setTitle(APPLICATION_NAME + " - " + documentTitle);
    }

    private void dummy() {
    } //temporary for menu listeners
}
