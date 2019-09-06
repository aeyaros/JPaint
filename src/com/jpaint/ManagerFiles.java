package com.jpaint;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;

import static com.jpaint.WindowApplication.APPLICATION_NAME;
import static com.jpaint.WindowApplication.NEW_DOCUMENT;

class ManagerFiles {

private ImageModel theModel;
private JFrame mainFrame;

//path of opened/saved file
private File theFile;

ManagerFiles(ImageModel imageModel, JFrame jFrame) {
	theModel = imageModel;
	mainFrame = jFrame;
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
	int result = JOptionPane.showOptionDialog(
		  mainFrame,
		  "Do you want to save your changes to this file?",
		  "Unsaved Changes",
		  JOptionPane.YES_NO_CANCEL_OPTION,
		  JOptionPane.WARNING_MESSAGE,
		  null,
		  options,
		  options[0]
	                                         );
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


void newFile(boolean transparent) {
	if (askToSave()) {
		theModel.startOverFromScratch(Main.DEFAULT_WINDOW_WIDTH, Main.DEFAULT_WINDOW_HEIGHT, transparent);
		theFile = null;
		setTitle(NEW_DOCUMENT);
	}
}

//open a file
void openFile() {
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
			e.printStackTrace();
			JOptionPane.showMessageDialog(
				  mainFrame,
				  "The file \"" + file.getName() + "\" couldn't be opened. Please try again with a different file.",
				  "Error",
				  JOptionPane.ERROR_MESSAGE
			                             );
			System.out.println("Couldn't open file.");
		}
	} else {
		System.out.println("Open command cancelled by user.");
	}
}


void save() {
	if (!theModel.isSaved()) saveas(); //if not saved, save the file as something
		//if model was already saved and still exists then just write it
	else if (theFile.exists()) { //if the file was saved and exists, then just write to it
		writeImageToFile(theFile);
		//done
	} else { //otherwise, file was saved but doesn't exist anymore so you have to save as
		JOptionPane.showMessageDialog(
			  mainFrame,
			  "The file \"" + theFile.getName() +
			  "\" no longer exists and may have been deleted. Please save as a new file.",
			  "File not found",
			  JOptionPane.ERROR_MESSAGE
		                             );
		saveas();
	}
}

void saveas() {
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
		JOptionPane.showMessageDialog(
			  mainFrame,
			  "The file \"" + outputfile.getName() +
			  "\" couldn't be saved. Please try again in a different location or as a different file type.",
			  "Error Saving File",
			  JOptionPane.ERROR_MESSAGE
		                             );
		System.out.println("Couldn't open file.");
	}
	
}

void setTitle(String documentTitle) {
	mainFrame.setTitle(APPLICATION_NAME + " - " + documentTitle);
}


void exit() {
	File printedFile = new File("./.temp.png");
	if (printedFile.exists()) printedFile.delete();
	if (askToSave()) System.exit(0);
}

}
