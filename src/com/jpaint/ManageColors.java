package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

//manage selected colors
class ManageColors {
//default preset colors
private final Color[] colors = {
	  new Color(255, 0, 0, 0), //black
	  new Color(255, 128, 128, 128), //gray
	  new Color(255, 255, 255, 255), //white
	  new Color(255, 255, 0, 255), //magenta
	  new Color(255, 255, 0, 0), //red
	  new Color(255, 255, 128, 0), //orange
	  new Color(255, 255, 255, 0), //yellow
	  new Color(255, 0, 255, 0), //green
	  new Color(255, 0, 128, 0), //dark green
	  new Color(255, 0, 255, 255), //cyan
	  new Color(255, 0, 0, 255), //blue
	  new Color(255, 128, 0, 255)  //purple
};

//the currently selected colors
private ColorButton[] selectedColors;

//the preset colors shown in the panels
private ColorButton[] presetColors;

//tools that use the colors
private Tool[] tools;

//elements
private JSlider opacitySlider;
private JLabel opacityLabel;

//the current application window, so I can give it to the color picker window
// so it can be centered when I open it
private JFrame mainFrame;

//the color picker window
private WindowColorPicker windowColorPicker;

ManageColors(
	  Tool[] tools, JFrame mainFrame, JPanel presetPanel, JPanel selectedColorsPanel, JPanel opacitySliderPanel
            ) {
	selectedColors = new ColorButton[3];
	
	//set current colors and add to the panel showing currently selected colors
	int halfMiddleWidth = 8; //half of the width of the middle button color selector
	selectedColors[0] = new ColorButton(
		  colors[0 % colors.length],
		  ToolButton.TOOL_BUTTON_SIZE - halfMiddleWidth,
		  ColorButton.COLOR_PRESET_BUTTON_SIZE
	);
	selectedColors[1] = new ColorButton(colors[1 % colors.length],
	                                    halfMiddleWidth + halfMiddleWidth, ColorButton.COLOR_PRESET_BUTTON_SIZE
	);
	selectedColors[2] = new ColorButton(
		  colors[2 % colors.length],
		  ToolButton.TOOL_BUTTON_SIZE - halfMiddleWidth,
		  ColorButton.COLOR_PRESET_BUTTON_SIZE
	);
	selectedColorsPanel.add(selectedColors[0], 0);
	selectedColorsPanel.add(selectedColors[1], 1);
	selectedColorsPanel.add(selectedColors[2], 2);
	
	//set up panel for opacity slider
	opacitySlider = new JSlider(SwingConstants.HORIZONTAL, Color.MIN_VALUE, Color.MAX_VALUE, Color.MAX_VALUE);
	opacitySlider.setMajorTickSpacing(85);
	opacitySlider.setMinorTickSpacing(17);
	opacitySlider.setPaintTicks(true);
	opacitySlider.setPaintLabels(false);
	
	opacityLabel = new JLabel("", SwingConstants.CENTER);
	opacityLabel.setHorizontalTextPosition(SwingConstants.CENTER);
	opacityLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
	opacityLabel.setVerticalAlignment(SwingConstants.BOTTOM);
	
	setOpacityLabel();
	
	//outer panel has box layout that respects widths
	//inner panel has borderlayout for positioning elements
	JPanel innerContainer = new JPanel(new BorderLayout());
	
	innerContainer.add(opacityLabel, BorderLayout.NORTH);
	innerContainer.add(opacitySlider, BorderLayout.SOUTH);
	innerContainer.setBorder(null);
	opacitySlider.setPreferredSize(new Dimension(
		  (int) Math.floor(ColorButton.COLOR_PRESET_BUTTON_SIZE * 3.5),
		  ToolButton.TOOL_BUTTON_SIZE
	));
	
	//add action listener to slider to update opacity of current color
	opacitySlider.addChangeListener(e -> {
		setOpacityLabel();
		setSelectedColorOpacity(opacitySlider.getValue());
	});
	
	//disabling opacity slider for now by not adding it
	//need to figure out a better way to format it before adding it
	//opacitySliderPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),"Opacity"));
	
	opacitySliderPanel.add(innerContainer);
	
	//keep track of the tools being used
	this.tools = tools;
	
	//build the list of color presets from input
	presetColors = new ColorButton[colors.length];
	
	for (int i = 0; i < presetColors.length; i++) {
		//create the button
		presetColors[i] = new ColorButton(colors[i],
		                                  ColorButton.COLOR_PRESET_BUTTON_SIZE, ColorButton.COLOR_PRESET_BUTTON_SIZE
		);
		
		//add event listener to the button
		//when it is clicked, the current color should be set
		presetColors[i].addMouseListener(new ColorButtonListener(presetColors[i]));
		
		//also add a mouse motion listener for drag events
		presetColors[i].addMouseMotionListener(new ColorButtonListener(presetColors[i]));
		
		//add the button to the panel
		presetPanel.add(presetColors[i]);
	}
	
	//add listeners to selected colors too
	for (ColorButton selectedColor : selectedColors) {
		selectedColor.addMouseListener(new SelectButtonListener(selectedColor));
		selectedColor.addMouseMotionListener(new SelectButtonListener(selectedColor));
	}
	
	//we need this for the color picker window
	this.mainFrame = mainFrame;
	
	//instantiate color picker
	this.windowColorPicker = new WindowColorPicker();
	
	//notify tools of color changes
	notifyTools();
}

//when a color is changed, send new colors to observers
void notifyTools() {
	for (Tool t : tools) {
		t.updateColors(
			  selectedColors[0].getColor(),
			  selectedColors[1].getColor(),
			  selectedColors[2].getColor()
		              );
	}
}

//given a mouse event, return the color button indicated by the mouse event
//either left, middle, or right button returns corresponding ColorButton object
private ColorButton accessButton(int buttonCode) {
	switch (buttonCode) {
		case MouseEvent.BUTTON2:
			return selectedColors[1];
		case MouseEvent.BUTTON3:
			return selectedColors[2];
		default:
			return selectedColors[0]; //MouseEvent.BUTTON1
	}
}

//set a color - updates the tools when color is set
void setButtonColor(Color color, int buttonCode) {
	accessButton(buttonCode).setColor(color);
	notifyTools();
}

private void setOpacityLabel() {
	opacityLabel.setText("Opacity: " + ((opacitySlider.getValue() * 100) / 255) + "%");
}

//take the selected colors and change their opacity
private void setSelectedColorOpacity(int opacity) {
	for (ColorButton cb : selectedColors) {
		Color c = new Color(cb.getColor());
		c.setChannel(0, opacity);
		cb.setColor(c);
	}
	notifyTools(); //notify tools of the change
}

private void setUpColorPicker(ColorButton colorToChange) {
	windowColorPicker.close(); //either window is open, closed already
	//in the color manager constructor, this is instantiated without a color (and therefore immediately closed)
	windowColorPicker.setColorPickerWindow(colorToChange, mainFrame, this);
}

//allows me to pass an index value through the action listener
// so I can use it to set the mouse controllers
//used for clicking the preset color buttons
private class ColorButtonListener implements MouseListener, MouseMotionListener {
	ColorButton colorButton;
	
	ColorButtonListener(ColorButton colorButton) {
		this.colorButton = colorButton;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) { //when a color preset is clicked
		if (e.getClickCount() > 1) { //if double click or more
			//double click; restore previous color to the corresponding button
			setButtonColor(accessButton(e.getButton()).getPreviousColor(), e.getButton());
			//open the window to edit the color
			setUpColorPicker(colorButton);
			
			System.out.println("doubleclick");
		} else { //if single click
			//set the current button color based on which button is clicked
			setButtonColor(colorButton.getColor(), e.getButton());
			
			//System.out.println("singleclick");
		}
		notifyTools();
	}
	
	//user may move mouse slightly when clicking, so we need a mousedragged event too
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseClicked(e);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
	}
}

private class SelectButtonListener extends ColorButtonListener {
	SelectButtonListener(ColorButton colorButton) {
		super(colorButton);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() > 1) setUpColorPicker(colorButton);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseClicked(e);
	}
}
}