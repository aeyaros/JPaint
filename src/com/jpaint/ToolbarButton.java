package com.jpaint;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;

class ToolbarButton extends JToggleButton {
final static int TOOL_BUTTON_SIZE = 48;
static final int TOOL_BUTTON_GAP = 4;

ToolbarButton(String iconSource) {
	super();
	this.setOpaque(true);
	this.setBorderPainted(true);
	//this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
	this.setSize(ToolbarButton.TOOL_BUTTON_SIZE, ToolbarButton.TOOL_BUTTON_SIZE);
	this.setPreferredSize(this.getSize());
	this.setMaximumSize(this.getSize());
	
	try { //get icon of button
		Image image = ImageIO.read(getClass().getResource(iconSource));
		image = image.getScaledInstance(ToolbarButton.TOOL_BUTTON_SIZE, ToolbarButton.TOOL_BUTTON_SIZE,
		                                Image.SCALE_SMOOTH
		                               );
		this.setIcon(new ImageIcon(image));
		this.setBackground(SystemColor.window);
		if (Main.IS_MAC) {
			//get the same image for the selected state, but invert the colors
			BufferedImage selectedImage = ImageIO.read(getClass().getResource(iconSource));
			for (int i = 0; i < selectedImage.getWidth(); i++) {
				for (int j = 0; j < selectedImage.getHeight(); j++) {
					Color c = new Color(selectedImage.getRGB(i, j));
					for (int k = 1; k < 4; k++) {
						c.setChannel(k, 255 - c.getChannel(k));
					}
					selectedImage.setRGB(i, j, c.getARGB());
				}
			}
			this.setSelectedIcon(new ImageIcon(
				  selectedImage.getScaledInstance(
						ToolbarButton.TOOL_BUTTON_SIZE, ToolbarButton.TOOL_BUTTON_SIZE, Image.SCALE_REPLICATE)));
		}
	} catch (Exception e) {
		e.printStackTrace();
		System.err.println("Couldn't load icon from " + iconSource);
	}
	
	this.addItemListener(e -> {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			//this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			//set border
			this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, SystemColor.controlHighlight,
			                                                SystemColor.controlShadow
			                                               ));
			//set background
			this.setBackground(WindowApplication.TOOL_PRESSED_BG_COLOR.getAWT());
		} else if (e.getStateChange() == ItemEvent.DESELECTED) {
			//set border
			//this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			//set background
			this.setBackground(SystemColor.window);
		}
	});
}
}
