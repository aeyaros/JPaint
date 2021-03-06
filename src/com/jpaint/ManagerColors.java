package com.jpaint;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

//manage selected colors
class ManagerColors {
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
private ButtonColorPreset[] selectedColors;

//the preset colors shown in the panels
private ButtonColorPreset[] presetColors;

//tools that use the colors
private Tool[] tools;

//elements
private JSlider opacitySlider;
private JLabel opacityLabel;

//the current application window, so I can give it to the color picker window
// so it can be centered when I open it
private JFrame mainFrame;

//the color picker window
private WindowColorEditor windowColorEditor;

ManagerColors(
	  Tool[] tools, JFrame mainFrame, JPanel presetPanel, JPanel selectedColorsPanel, JPanel opacitySliderPanel
             ) {
	selectedColors = new ButtonColorPreset[3];
	
	//set current colors and add to the panel showing currently selected colors
	int halfMiddleWidth = 8; //half of the width of the middle button color selector
	selectedColors[0] = new ButtonColorPreset(
		  colors[0 % colors.length],
		  ButtonColorPreset.COLOR_PRESET_BUTTON_SIZE, //ButtonToolbar.TOOL_BUTTON_SIZE - halfMiddleWidth,
		  ButtonColorPreset.COLOR_PRESET_BUTTON_SIZE
	);
	selectedColors[1] = new ButtonColorPreset(
		  colors[1 % colors.length],
	                                          halfMiddleWidth + halfMiddleWidth,
		  ButtonColorPreset.COLOR_PRESET_BUTTON_SIZE
	);
	selectedColors[2] = new ButtonColorPreset(
		  colors[2 % colors.length],
		  ButtonColorPreset.COLOR_PRESET_BUTTON_SIZE,//ButtonToolbar.TOOL_BUTTON_SIZE - halfMiddleWidth,
		  ButtonColorPreset.COLOR_PRESET_BUTTON_SIZE
	);
	
	
	JPanel leftButton = new JPanel();
	leftButton.setBorder(BorderFactory.createEmptyBorder(
		  ButtonColorPreset.COLOR_PRESET_BUTTON_SIZE / 3,
		  ButtonColorPreset.COLOR_PRESET_BUTTON_SIZE / 3,
		  ButtonColorPreset.COLOR_PRESET_BUTTON_SIZE / 3,
		  ButtonColorPreset.COLOR_PRESET_BUTTON_SIZE / 6
	                                                    ));
	leftButton.setLayout(new BoxLayout(leftButton, BoxLayout.X_AXIS));
	
	JPanel rightButton = new JPanel();
	rightButton.setBorder(BorderFactory.createEmptyBorder(
		  ButtonColorPreset.COLOR_PRESET_BUTTON_SIZE / 3,
		  ButtonColorPreset.COLOR_PRESET_BUTTON_SIZE / 6,
		  ButtonColorPreset.COLOR_PRESET_BUTTON_SIZE / 3,
		  ButtonColorPreset.COLOR_PRESET_BUTTON_SIZE / 3
	                                                     ));
	rightButton.setLayout(new BoxLayout(rightButton, BoxLayout.X_AXIS));
	
	leftButton.add(selectedColors[0]);
	rightButton.add(selectedColors[2]); //index was 2
	
	
	selectedColorsPanel.add(leftButton, 0);
	//selectedColorsPanel.add(selectedColors[1], 1);
	selectedColorsPanel.add(rightButton, 1); //index was 2
	
	selectedColorsPanel.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
	selectedColorsPanel.setBackground(new Color(255, 220, 220, 220).getAWT());
	selectedColorsPanel.setOpaque(true);
	
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
		  (int) Math.floor(ButtonColorPreset.COLOR_PRESET_BUTTON_SIZE * 3.5),
		  ButtonToolbar.TOOL_BUTTON_SIZE
	));
	
	//add action listener to slider to update opacity of current color
	opacitySlider.addChangeListener(new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			setOpacityLabel();
			setSelectedColorOpacity(opacitySlider.getValue());
		}
	});
	
	//disabling opacity slider for now by not adding it
	//need to figure out a better way to format it before adding it
	//opacitySliderPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),"Opacity"));
	
	opacitySliderPanel.add(innerContainer);
	
	//keep track of the tools being used
	this.tools = tools;
	
	//build the list of color presets from input
	presetColors = new ButtonColorPreset[colors.length];
	
	for (int i = 0; i < presetColors.length; i++) {
		//create the button
		presetColors[i] = new ButtonColorPreset(
			  colors[i],
			  ButtonColorPreset.COLOR_PRESET_BUTTON_SIZE,
			  ButtonColorPreset.COLOR_PRESET_BUTTON_SIZE
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
	for (ButtonColorPreset selectedColor : selectedColors) {
		selectedColor.addMouseListener(new SelectButtonListener(selectedColor));
		selectedColor.addMouseMotionListener(new SelectButtonListener(selectedColor));
	}
	
	//we need this for the color picker window
	this.mainFrame = mainFrame;
	
	//instantiate color picker
	this.windowColorEditor = new WindowColorEditor();
	
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
//either left, middle, or right button returns corresponding ButtonColorPreset object
private ButtonColorPreset accessButton(int buttonCode) {
	switch (buttonCode) {
		case MouseEvent.BUTTON2:
			//return selectedColors[1]; //no more right click
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
	for (ButtonColorPreset cb : selectedColors) {
		Color c = new Color(cb.getColor());
		c.setChannel(0, opacity);
		cb.setColor(c);
	}
	notifyTools(); //notify tools of the change
}

private void setUpColorPicker(ButtonColorPreset colorToChange) {
	windowColorEditor.close(); //either window is open, closed already
	//in the color manager constructor, this is instantiated without a color (and therefore immediately closed)
	windowColorEditor.setColorPickerWindow(colorToChange, mainFrame, this);
}

//allows me to pass an index value through the action listener
// so I can use it to set the mouse controllers
//used for clicking the preset color buttons
private class ColorButtonListener implements MouseListener, MouseMotionListener {
	ButtonColorPreset buttonColorPreset;
	
	ColorButtonListener(ButtonColorPreset buttonColorPreset) {
		this.buttonColorPreset = buttonColorPreset;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) { //when a color preset is clicked
		if (e.getClickCount() > 1) { //if double click or more
			//double click; restore previous color to the corresponding button
			setButtonColor(accessButton(e.getButton()).getPreviousColor(), e.getButton());
			//open the window to edit the color
			setUpColorPicker(buttonColorPreset);
			
			//System.out.println("doubleclick");
		} else { //if single click
			//set the current button color based on which button is clicked
			setButtonColor(buttonColorPreset.getColor(), e.getButton());
			
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
	SelectButtonListener(ButtonColorPreset buttonColorPreset) {
		super(buttonColorPreset);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() > 1) setUpColorPicker(buttonColorPreset);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseClicked(e);
	}
}
}