package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;

class WindowResize extends JDialog {
private final int MAX_INTEGER_DIGITS = 5; //not sure if I can handle 100,000 pixels :/
private ImageModel theModel;
private JTextField widthField;
private JTextField heightField;
private int MAX_RESIZE = (int) Math.pow(10, MAX_INTEGER_DIGITS);

WindowResize(JFrame mainFrame, ImageModel imageModel) {
	super(mainFrame, "Resize Image", true);
	theModel = imageModel;
	
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

private void setFieldsToDefault() {
	widthField.setText(Integer.toString(theModel.getWidth()));
	heightField.setText(Integer.toString(theModel.getHeight()));
}

private void close() {
	try {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	} catch (Exception e) {
		e.printStackTrace();
	}
}

private void apply() {
	//check inputs
	try {
		int newW = Integer.parseInt(widthField.getText());
		int newH = Integer.parseInt(heightField.getText());
		
		//check bounds of input
		if (newW <= 0 || newH <= 0) {
			JOptionPane.showMessageDialog(
				  this,
				  "You entered a negative number. Please only type positive whole numbers.",
				  "Error",
				  JOptionPane.ERROR_MESSAGE
			                             );
			setFieldsToDefault();
		} else if (newW > MAX_RESIZE || newH > MAX_RESIZE) {
			JOptionPane.showMessageDialog(
				  this,
				  "You entered a very large number! Please only type numbers less than" + MAX_RESIZE + ".",
				  "Error",
				  JOptionPane.ERROR_MESSAGE
			                             );
			setFieldsToDefault();
		} else { //if input within bounds
			//check if image will be cropped (a dimension is smaller than the previous dimension)
			if (newW < theModel.getWidth() || newH < theModel.getHeight()) {
				//if crop, ask for confirmation
				//Custom button text
				Object[] options = {"OK",
				                    "Cancel"};
				int result = JOptionPane.showOptionDialog(
					  this,
					  "The image will be cropped. Continue?",
					  "Alert",
					  JOptionPane.YES_NO_OPTION,
					  JOptionPane.WARNING_MESSAGE,
					  null,
					  options,
					  options[1]
				                                         );
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
		JOptionPane.showMessageDialog(
			  this,
			  "You didn't type a number. Please only type positive whole numbers.",
			  "Error",
			  JOptionPane.ERROR_MESSAGE
		                             );
		setFieldsToDefault();
	}
}

}

