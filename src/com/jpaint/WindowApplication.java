package com.jpaint;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Hashtable;

class WindowApplication {
//parameters for certain UI sizes
static final String APPLICATION_NAME = "JPaint";
static final String NEW_DOCUMENT = "New Document";
private final int NUMBER_TOOL_COLUMNS = 2;
private final int NUMBER_COLOR_COLUMNS = 3;
private final int MINIMUM_WINDOW_WIDTH = 700;
private final int MINIMUM_WINDOW_HEIGHT = 560;
private final int THIN_EMPTY_BORDER_SIZE = 6;
private final int WINDOW_CHROME_SIZE = 8;
//colors
private final Color PAGE_BACKGROUND_COLOR = new Color(255, 180, 180, 200);
//windows
private JFrame mainFrame;
//labels
private JLabel coordinatesLabel;
private JLabel sizeLabel;
private ImageModel theModel;
private ImageView theView;
private ManageFiles manageFiles;

WindowApplication() {
}

void WindowSetup(int width, int height) {
	//initial setup
	mainFrame = new JFrame(APPLICATION_NAME);
	mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	
	
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
	mainPanel
		  .setBorder(new EmptyBorder(WINDOW_CHROME_SIZE, WINDOW_CHROME_SIZE, WINDOW_CHROME_SIZE,
		                             WINDOW_CHROME_SIZE
		  ));
	
	//image panel: a container which is inside a panel with scrollbars
	JPanel imagePanel = new JPanel();
	imagePanel.setBackground(java.awt.Color.gray);
	
	//image panel is inside a scroll pane
	JScrollPane imageScrollPanel = new JScrollPane(imagePanel);
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
	EmptyBorder sidePanelBorder =
		  new EmptyBorder(THIN_EMPTY_BORDER_SIZE, THIN_EMPTY_BORDER_SIZE, THIN_EMPTY_BORDER_SIZE,
		                  THIN_EMPTY_BORDER_SIZE
		  );
	
	//tools panel
	JPanel toolsPanel =
		  new JPanel(new GridLayout(0, NUMBER_TOOL_COLUMNS, ToolButton.TOOL_BUTTON_GAP, ToolButton.TOOL_BUTTON_GAP));
	toolsPanel.setBorder(sidePanelBorder);
	gridBagConstraints.gridy = 0; //y - will be changed as I add components
	sidePanel.add(toolsPanel, gridBagConstraints);
	
	//color presets panel
	JPanel presetPanel = new JPanel(new GridLayout(0, NUMBER_COLOR_COLUMNS, 0, 0));
	presetPanel.setBorder(sidePanelBorder);
	gridBagConstraints.gridy++;
	sidePanel.add(presetPanel, gridBagConstraints);
	
	//opacity slider panel
	JPanel opacityPanel = new JPanel();
	BoxLayout opacityLayout = new BoxLayout(opacityPanel, BoxLayout.Y_AXIS);
	opacityPanel.setLayout(opacityLayout);
	opacityPanel
		  .setBorder(new EmptyBorder(THIN_EMPTY_BORDER_SIZE * 3, THIN_EMPTY_BORDER_SIZE, 0, THIN_EMPTY_BORDER_SIZE));
	gridBagConstraints.gridy++;
	sidePanel.add(opacityPanel, gridBagConstraints);
	
	//selected colors panel
	JPanel selectedColorsPanel = new JPanel(new GridBagLayout());
	selectedColorsPanel
		  .setBorder(new EmptyBorder(0, THIN_EMPTY_BORDER_SIZE, THIN_EMPTY_BORDER_SIZE, THIN_EMPTY_BORDER_SIZE));
	gridBagConstraints.gridy++;
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
	ManageColors manageColors =
		  new ManageColors(manageTools.getTools(), mainFrame, presetPanel, selectedColorsPanel, opacityPanel);
	manageTools.addColorManager(manageColors);
	
	/*====== File I/O Manager Object ======*/
	manageFiles = new ManageFiles(theModel, mainFrame);
	
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
	menuItems.put("new", new MenuItem("New Opaque Image", KeyEvent.VK_N, cmdCtrlModifier, fileMenu, KeyEvent.VK_N,
	                                  e -> manageFiles.newFile(false)
	));
	menuItems.put(
		  "newtrans",
		  new MenuItem("New Transparent Image", KeyEvent.VK_N, cmdCtrlShiftModifier, fileMenu, KeyEvent.VK_T,
		               e -> manageFiles.newFile(true)
		  )
	             );
	fileMenu.addSeparator();
	
	menuItems.put(
		  "open",
		  new MenuItem("Open", KeyEvent.VK_O, cmdCtrlModifier, fileMenu, KeyEvent.VK_O, e -> manageFiles.openFile())
	             );
	menuItems.put(
		  "save",
		  new MenuItem("Save", KeyEvent.VK_S, cmdCtrlModifier, fileMenu, KeyEvent.VK_S,
		               e -> manageFiles.save()
		  )
	             );
	menuItems.put(
		  "saveas",
		  new MenuItem(
				"Save As", KeyEvent.VK_S, cmdCtrlShiftModifier, fileMenu, KeyEvent.VK_A, e -> manageFiles.saveas())
	             );
	fileMenu.addSeparator();
	
	menuItems
		  .put("print", new MenuItem("Print", KeyEvent.VK_P, cmdCtrlModifier, fileMenu, KeyEvent.VK_P,
		                             e -> print()
		  ));
	fileMenu.addSeparator();
	
	if (Main.IS_MAC)
		menuItems.put(
			  "close",
			  new MenuItem("Close", KeyEvent.VK_W, cmdCtrlModifier, fileMenu, KeyEvent.VK_W, e -> manageFiles.exit())
		             );
	else
		menuItems.put(
			  "exit",
			  new MenuItem("Exit", KeyEvent.VK_E, cmdCtrlModifier, fileMenu, KeyEvent.VK_E, e -> manageFiles.exit())
		             );
	
	//edit menu
	menuItems.put("undo", new MenuItem("Undo", KeyEvent.VK_Z, cmdCtrlModifier, editMenu, KeyEvent.VK_Z, e -> undo()));
	MenuItem redoItem; //shortcut changes depending on platform
	if (Main.IS_MAC)
		redoItem = new MenuItem("Redo", KeyEvent.VK_Z, cmdCtrlShiftModifier, editMenu, KeyEvent.VK_Z, e -> redo());
	else redoItem = new MenuItem("Redo", KeyEvent.VK_Y, cmdCtrlModifier, editMenu, KeyEvent.VK_Y, e -> redo());
	menuItems.put("redo", redoItem);
	editMenu.addSeparator();
	
	menuItems.put(
		  "selectall",
		  new MenuItem("Select All", KeyEvent.VK_A, cmdCtrlModifier, editMenu, KeyEvent.VK_A, e -> dummy())
	             );
	editMenu.addSeparator();
	
	menuItems.put("cut", new MenuItem("Cut", KeyEvent.VK_X, cmdCtrlModifier, editMenu, KeyEvent.VK_X, e -> dummy()));
	menuItems.put("copy", new MenuItem("Copy", KeyEvent.VK_C, cmdCtrlModifier, editMenu, KeyEvent.VK_C, e -> dummy()));
	menuItems
		  .put("paste", new MenuItem("Paste", KeyEvent.VK_V, cmdCtrlModifier, editMenu, KeyEvent.VK_V,
		                             e -> dummy()
		  ));
	
	//transform menu
	menuItems.put("resize", new MenuItem("Resize", transformMenu, KeyEvent.VK_R, e -> resize()));
	transformMenu.addSeparator();
	
	menuItems.put("fliph", new MenuItem("Flip Image Horizontally", transformMenu, KeyEvent.VK_H, e -> flip(true)));
	menuItems.put("flipv", new MenuItem("Flip Image Vertically", transformMenu, KeyEvent.VK_V, e -> flip(false)));
	transformMenu.addSeparator();
	
	menuItems.put(
		  "rotateleft",
		  new MenuItem("Rotate Left 90\u00B0", transformMenu, KeyEvent.VK_L,
		               e -> rotate(Canvas.Transform.ROTATE_LEFT)
		  )
	             );
	menuItems.put("rotateright", new MenuItem("Rotate Right 90\u00B0", transformMenu, KeyEvent.VK_R,
	                                          e -> rotate(Canvas.Transform.ROTATE_RIGHT)
	));
	menuItems.put(
		  "rotate180",
		  new MenuItem("Rotate 180\u00B0", transformMenu, KeyEvent.VK_U, e -> rotate(Canvas.Transform.ROTATE_180))
	             );
	
	
	menuItems.put("about", new MenuItem("About " + APPLICATION_NAME, helpMenu, KeyEvent.VK_U, e -> {
		Image image = null;
		try {
			image = ImageIO.read(getClass().getResource("icons/appicon.png"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		try {
			image = image.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
		} catch (NullPointerException ignored) { }
		JOptionPane.showMessageDialog(mainFrame, APPLICATION_NAME +
		                                         " is a fun painting program inspired by Apple's MacPaint (from " +
		                                         "1984)" +
		                                         " " +
		                                         "\n" +
		                                         "and old versions of Microsoft Paint. Created by Andrew Yaros in " +
		                                         "2019, \n" +
		                                         "originally for the CS 338 (GUIs) class at Drexel University.",
		                              "About " + APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE,
		                              new ImageIcon(image)
		                             );
	}));
	
	/*====== COLOR SCHEME ======*/
	imagePanel.setBackground(PAGE_BACKGROUND_COLOR.getAWT());
	
	/*====== SET TITLE ======*/
	manageFiles.setTitle(NEW_DOCUMENT);
	
	//ask to save when closing window
	mainFrame.addWindowListener(new WindowListener() {
		@Override public void windowClosing(WindowEvent e) {
			manageFiles.exit();
		}
		
		@Override public void windowOpened(WindowEvent e) { }
		
		@Override public void windowClosed(WindowEvent e) {}
		
		@Override public void windowIconified(WindowEvent e) { }
		
		@Override public void windowDeiconified(WindowEvent e) { }
		
		@Override public void windowActivated(WindowEvent e) { }
		
		@Override public void windowDeactivated(WindowEvent e) {}
	});
	
	/*====== SHOW WINDOW ======*/
	mainFrame.pack();
	mainFrame.setLocationRelativeTo(null); //center image
	mainFrame.setVisible(true);
}

//undo/redo
private void undo() {
	theModel.undo();
}

private void redo() {
	theModel.redo();
}

//not done yet
private void print() {
	//PrinterJob newPrintJob = PrinterJob.getPrinterJob();
}

private void resize() {
	new WindowResize(mainFrame, theModel);
}

private void flip(boolean horizontally) {
	if (horizontally) theModel.flip(0);
	else theModel.flip(1);
}

private void rotate(Canvas.Transform option) {
	theModel.rotate(option);
}

private void dummy() {
} //temporary for menu listeners


}
