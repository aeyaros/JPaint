package com.jpaint;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

class WindowColorPicker {
private final int COLOR_LABEL_WIDTH = 128;
private final int COLOR_LABEL_HEIGHT = 64;
private final int SLIDER_BORDER = 12;
//window elements
private JFrame frame;
private JLabel[] labels;
private JSlider[] sliders;
private ColorButton colorLabel;
//color stuff
private Color colorToChange;
private ColorButton colorButton;
private ManageColors manageColors;

//when initially created, we dont use it
WindowColorPicker() {
	close();
}

void close() {
	try {
		colorButton.resetBorder();
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	} catch (Exception e) {
		System.out.println(
			  "Color picker window close event sent even though window already closed. Ignore this message.");
	}
}

private void save() {
	colorButton.setColor(colorToChange);
	manageColors.notifyTools();
	close();
}

//each time color button double clicked, set up the window and open it
void setColorPickerWindow(ColorButton colorButton, JFrame mainFrame, ManageColors manageColors) {
	
	this.colorButton = colorButton; //not used until saving
	this.manageColors = manageColors; //not used until saving
	colorToChange = new Color(colorButton.getColor()); //store a copy of the original color
	
	this.colorButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, SystemColor.controlHighlight,
	                                                           SystemColor.controlHighlight
	                                                          ));
	
	frame = new JFrame("Color Picker");
	
	//set dimensions
	Dimension colorSize = new Dimension(256, 480);
	frame.setResizable(false);
	frame.setMinimumSize(colorSize);
	frame.setMaximumSize(colorSize);
	frame.setPreferredSize(colorSize);
	frame.setSize(colorSize);
	
	//base panel containing layout
	JPanel mainPanel = new JPanel();
	frame.getContentPane().add(mainPanel);
	
	mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
	
	JPanel originalColorLabels = new JPanel();
	JPanel newColorLabels = new JPanel();
	
	BoxLayout oc = new BoxLayout(originalColorLabels, BoxLayout.Y_AXIS);
	BoxLayout nc = new BoxLayout(newColorLabels, BoxLayout.Y_AXIS);
	originalColorLabels.setLayout(oc);
	newColorLabels.setLayout(nc);
	
	JLabel originalLabelText = new JLabel("Original", SwingConstants.CENTER);
	originalColorLabels.add(originalLabelText);
	
	//label to show original color, is never changed once set
	ColorButton oldColorButtonLabel = new ColorButton(colorButton.getColor(), COLOR_LABEL_WIDTH, COLOR_LABEL_HEIGHT);
	oldColorButtonLabel.setBorder(null);
	originalColorLabels.add(oldColorButtonLabel);
	setColorPane(oldColorButtonLabel);
	
	//stuff for new color that is being set by user
	JLabel newLabelText = new JLabel("New", SwingConstants.CENTER);
	newColorLabels.add(newLabelText);
	
	//add label to show new color color, changes when user uses controls on the window
	colorLabel = new ColorButton(colorButton.getColor(), COLOR_LABEL_WIDTH, COLOR_LABEL_HEIGHT);
	colorLabel.setBorder(null);
	newColorLabels.add(colorLabel);
	setColorPane(colorLabel);
	
	//panel containing both colors
	JPanel colorPanel = new JPanel(new GridLayout(1, 2));
	colorPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, SLIDER_BORDER, 0));
	
	colorPanel.add(originalColorLabels);
	colorPanel.add(newColorLabels);
	
	mainPanel.add(colorPanel);
	
	
	JPanel[] sliderPanels = new JPanel[4];
	sliders = new JSlider[sliderPanels.length];
	labels = new JLabel[sliderPanels.length];
	
	for (int i = 0; i < sliderPanels.length; i++) {
		
		labels[i] = new JLabel(Integer.toString(colorToChange.getChannel(i)), SwingConstants.RIGHT);
		labels[i].setPreferredSize(new Dimension(48, 24));
		labels[i].setMinimumSize(labels[i].getPreferredSize());
		labels[i].setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
		
		sliders[i] =
			  new JSlider(SwingConstants.HORIZONTAL, Color.MIN_VALUE, Color.MAX_VALUE, colorToChange.getChannel(i));
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
			  BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Color.getChannelString(i)));
	}
	
	//add sliders; red first
	for (int i = 1; i < sliderPanels.length + 1; i++) {
		mainPanel.add(sliderPanels[i % sliderPanels.length]);
	}
	
	//add OK and cancel button
	JPanel buttons = new JPanel(new GridLayout(1, 0, 6, 0));
	
	JButton applyButton = new JButton("Apply");
	JButton cancelButton = new JButton("Cancel");
	buttons.setBorder(new EmptyBorder(17, 0, 12, 0));
	
	applyButton.addActionListener(e -> save());
	cancelButton.addActionListener(e -> close());
	
	buttons.add(applyButton);
	buttons.add(cancelButton);
	mainPanel.add(buttons);
	buttons.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
	mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
	
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
	colorToChange.setChannel(i, sliders[i].getValue());
	labels[i].setText(Integer.toString(colorToChange.getChannel(i)));
	setColorPane(colorLabel);
}

private void setColorPane(ColorButton pane) {
	pane.setColor(colorToChange);
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
