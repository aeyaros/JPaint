package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

class ManageTools {
private static int INITIAL_TOOL = 3;
private Tool[] tools;
private ControllerMouseStatic controllerMouseStatic;
private ControllerMouseMotion controllerMouseMotion;
private ControllerKey controllerKey;
private JPanel topPanel;
private CardLayout topLayout;
private ToolColorPicker toolColorPicker;
private ImageView imageView;

ManageTools(JPanel toolsPanel, ImageModel theModel, ImageView theView) {
	//create tools
	//add them to arraylist one by one, then copy to regular array to avoid magic numbers
	ArrayList<Tool> newTools = new ArrayList<>();
	newTools.add(new ToolSelect(theModel, "icons/sidebar_tool_icons/select.png"));
	
	toolColorPicker = new ToolColorPicker(theModel, "icons/sidebar_tool_icons/colorpicker.png");
	newTools.add(toolColorPicker); //later, add the color manager once it is declared
	
	newTools.add(new ToolPencil(theModel, "icons/sidebar_tool_icons/pencil.png"));
	newTools.add(new ToolPaintBrush(theModel, "icons/sidebar_tool_icons/paintbrush.png"));
	newTools.add(new ToolEraser(theModel, "icons/sidebar_tool_icons/eraser.png"));
	newTools.add(new ToolPaintBucket(theModel, "icons/sidebar_tool_icons/paintbucket.png"));
	newTools.add(new ToolLine(theModel, "icons/sidebar_tool_icons/line.png"));
	newTools.add(new ToolShapes(theModel, "icons/sidebar_tool_icons/shapes.png"));
	
	tools = new Tool[newTools.size()]; //use regular array for better performance
	for (int i = 0; i < tools.length; i++) tools[i] = newTools.get(i);
	
	//create both static and motion mouse controllers and add them to the view
	controllerMouseStatic = new ControllerMouseStatic(tools);
	theView.addMouseListener(controllerMouseStatic);
	
	controllerMouseMotion = new ControllerMouseMotion(tools);
	theView.addMouseMotionListener(controllerMouseMotion);
	
	/*
	theView.getInputMap().put(KeyStroke.getKeyStroke("SPACE"),
	                            "doSomething");
	theView.getActionMap().put(
		  "doSomething",
		  new AbstractAction() {
			  @Override public void actionPerformed(ActionEvent e) {
			  	System.out.println("TEST lol");
			  }
		  });
	*/
	/*
	controllerKey = new ControllerKey(tools);
	theView.addKeyListener(controllerKey);
	
	//ensure imageview is in focus so key events are received
	imageView = theView; //so listener can see it
	imageView.setFocusable(true);
	imageView.setFocusTraversalKeysEnabled(false);
	theView.addMouseListener(new MouseListener() {
		@Override public void mouseEntered(MouseEvent e) {
			System.out.println("imageView in focus");
			System.out.println(imageView.isFocusable());
			imageView.requestFocusInWindow();
			System.out.println(imageView.isFocusable());
			
		}
		@Override public void mouseClicked(MouseEvent e) { }
		@Override public void mousePressed(MouseEvent e) { }
		@Override public void mouseReleased(MouseEvent e) { }
		@Override public void mouseExited(MouseEvent e) { }
	});
	*/
	
	//set up top panel for tool-specific controls
	topPanel = new JPanel();
	topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, WindowApplication.WINDOW_CHROME_SIZE, 0));
	
	topLayout = new CardLayout();
	topPanel.setLayout(topLayout);
	
	//button group, only one tool can be selected at a time
	//buttons are radio buttons
	ButtonGroup buttonGroup = new ButtonGroup();
	
	//add listeners to tools
	for (int i = 0; i < tools.length; i++) {// t: tools) {
		//add an event listener to the tool button
		//to define behavior when tool button is pressed
		tools[i].button.putClientProperty("index", i); //set index of the button
		tools[i].button.addActionListener(new ToolButtonListener(i)); //add listener to button
		
		//add the tool's top card to the top panel
		topPanel.add(tools[i].upperCard, Integer.toString(i)); //add to card, 'name' is the index as a string
		
		//add the tool's button to the button group
		buttonGroup.add(tools[i].button);
		
		//add the tool's button to the sidebar
		toolsPanel.add(tools[i].button); //add button to panel on sidebar
	}
	
	/*====== START WITH AN INITIAL TOOL SELECTED ======*/
	setCurrentTool(INITIAL_TOOL); //start with pencil
	//still need to make it initially selected
	tools[INITIAL_TOOL].button.setSelected(true);
}

Tool[] getTools() {
	return tools;
}


JPanel getTopPanel() {
	return topPanel;
}

void addColorManager(ManageColors manageColors) {
	toolColorPicker.addColorManager(manageColors);
}

private void setCurrentTool(int i) {
	//show the tool's top card in the card panel
	topLayout.show(topPanel, Integer.toString(i)); //refer to 'name' here
	
	//set the current tool for mouse listeners to use
	controllerMouseStatic.setTool(i);
	controllerMouseMotion.setTool(i);
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
}
