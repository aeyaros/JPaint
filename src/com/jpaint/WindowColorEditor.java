package com.jpaint;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

class WindowColorEditor {
private final int COLOR_LABEL_WIDTH = 128;
private final int COLOR_LABEL_HEIGHT = 64;
private final int BORDER_THICKNESS = 12;
private final Dimension colorSize = new Dimension(256, 470);
//window elements
private JFrame frame;
private JLabel[] labels;
private JSlider[] sliders;
private ButtonColorPreset colorLabel;
//color stuff
private Color colorBeingChanged;
private ButtonColorPreset buttonColorPreset;
private ManagerColors managerColors;
private JLabel originalColorText;
private JLabel newColorText;

//when initially created, we dont use it
WindowColorEditor() {
	close();
}

void close() {
	try {
		buttonColorPreset.resetBorder();
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	} catch (Exception e) {
		//System.out.println(
		//	  "Color picker window close event sent even though window already closed. Ignore this message.");
	}
}

private void save() {
	buttonColorPreset.setColor(colorBeingChanged);
	managerColors.notifyTools();
	close();
}

//each time color button double clicked, set up the window and open it
void setColorPickerWindow(ButtonColorPreset buttonColorPreset, JFrame mainFrame, ManagerColors managerColors) {
	
	this.buttonColorPreset = buttonColorPreset; //not used until saving
	this.managerColors = managerColors; //not used until saving
	colorBeingChanged = new Color(buttonColorPreset.getColor()); //store a copy of the original color
	
	this.buttonColorPreset.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, SystemColor.controlHighlight,
	                                                                 SystemColor.controlHighlight
	                                                                ));
	
	frame = new JFrame("Color Editor");
	
	//set dimensions
	frame.setResizable(false);
	frame.setMaximumSize(colorSize);
	frame.setPreferredSize(colorSize);
	frame.setSize(colorSize);
	
	//base panel containing layout
	JPanel mainPanel = new JPanel();
	frame.getContentPane().add(mainPanel);
	mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
	
	//labels containing the colors themselves - two colors next to each other
	JPanel colorLabels = new JPanel(new GridLayout(1, 2));
	
	//label to show original color, is never changed once set
	ButtonColorPreset originalColorLabel =
		  new ButtonColorPreset(buttonColorPreset.getColor(), COLOR_LABEL_WIDTH, COLOR_LABEL_HEIGHT);
	originalColorLabel.setBorder(null);
	colorLabels.add(originalColorLabel);
	setColorPane(originalColorLabel);
	originalColorText = new JLabel("OldColor");
	originalColorLabel.add(originalColorText);
	setColorLabelTextColor(originalColorText, buttonColorPreset.getColor());
	
	//label to show new color color, changes when user uses controls on the window
	colorLabel = new ButtonColorPreset(buttonColorPreset.getColor(), COLOR_LABEL_WIDTH, COLOR_LABEL_HEIGHT);
	colorLabel.setBorder(null);
	colorLabels.add(colorLabel);
	setColorPane(colorLabel);
	newColorText = new JLabel("New color");
	colorLabel.add(newColorText);
	setColorLabelTextColor(newColorText, buttonColorPreset.getColor());
	
	//panel containing both titles and colors
	JPanel colorPanel = new JPanel();
	colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.Y_AXIS));
	colorPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
	//colorPanel.add(colorLabelText); //titles
	colorPanel.add(colorLabels); //colors
	
	mainPanel.add(colorPanel); //add to main
	
	
	//panels containing slider controls
	JPanel[] sliderPanels = new JPanel[4];
	sliders = new JSlider[sliderPanels.length];
	labels = new JLabel[sliderPanels.length];
	
	for (int i = 0; i < sliderPanels.length; i++) {
		
		labels[i] = new JLabel(Integer.toString(colorBeingChanged.getChannel(i)), SwingConstants.RIGHT);
		labels[i].setPreferredSize(new Dimension(48, 24));
		labels[i].setMinimumSize(labels[i].getPreferredSize());
		labels[i].setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
		
		sliders[i] =
			  new JSlider(SwingConstants.HORIZONTAL, Color.MIN_VALUE, Color.MAX_VALUE,
			              colorBeingChanged.getChannel(i));
		sliders[i].addChangeListener(new sliderListener(i));
		
		sliders[i].setMajorTickSpacing(85);
		sliders[i].setMinorTickSpacing(17);
		sliders[i].setPaintTicks(true);
		sliders[i].setPaintLabels(true);
		
		sliderPanels[i] = new JPanel();
		sliderPanels[i].setLayout(new BoxLayout(sliderPanels[i], BoxLayout.X_AXIS));
		sliderPanels[i].add(sliders[i]);
		sliderPanels[i].add(labels[i]);
		sliderPanels[i].setBorder(
			  BorderFactory
					.createTitledBorder(BorderFactory.createEtchedBorder(), "Adjust " + Color.getChannelString(i)));
	}
	
	//add sliders; red first
	for (int i = 1; i < sliderPanels.length + 1; i++) {
		mainPanel.add(sliderPanels[i % sliderPanels.length]);
	}
	
	//add OK and cancel button
	JPanel buttons = new JPanel(new GridLayout(1, 0, 6, 0));
	
	JButton applyButton = new JButton("Apply");
	JButton cancelButton = new JButton("Cancel");
	
	applyButton.addActionListener(e -> save());
	cancelButton.addActionListener(e -> close());
	
	buttons.add(applyButton);
	buttons.add(cancelButton);
	mainPanel.add(buttons);
	buttons.setBorder(BorderFactory.createEmptyBorder(17, 0, 12, 0));
	mainPanel.setBorder(BorderFactory.createEmptyBorder(
		  BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS));
	
	frame.setAlwaysOnTop(true);
	
	//always show on top, but only if the main window is in focus
	mainFrame.addWindowFocusListener(new WindowFocusListener() {
		@Override
		public void windowGainedFocus(WindowEvent e) {
			frame.setAlwaysOnTop(true);
			System.out.println("gained focus");
		}
		
		@Override
		public void windowLostFocus(WindowEvent e) {
			frame.setAlwaysOnTop(false);
			System.out.println("lost focus");
		}
	});
	
	frame.pack();
	frame.setLocationRelativeTo(mainFrame);
	frame.setVisible(true);
}

private void updateChannel(int i) {
	colorBeingChanged.setChannel(i, sliders[i].getValue());
	labels[i].setText(Integer.toString(colorBeingChanged.getChannel(i)));
	setColorPane(colorLabel);
	//update color of text so it contrasts with the color
	setColorLabelTextColor(newColorText, colorBeingChanged);
}

private void setColorPane(ButtonColorPreset pane) {
	pane.setColor(colorBeingChanged);
}

private void setColorLabelTextColor(JLabel text, Color backgroundColor) {
	if (Color.isDarkColor(backgroundColor)) text.setForeground(java.awt.Color.white);
	else text.setForeground(java.awt.Color.black);
}

private class sliderListener implements ChangeListener {
	private int index;
	
	sliderListener(int index) {
		this.index = index;
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		updateChannel(index);
	}
}
}
