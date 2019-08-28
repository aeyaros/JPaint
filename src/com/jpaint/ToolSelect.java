package com.jpaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class ToolSelect extends Tool {
private JButton deselectButton;
//current selection coordinates
private int[] start;
private int[] end;

private enum SelectionState {
	NOTHING_SELECTED,
	CREATING_SELECTION,
	SOMETHING_SELECTED
}

private SelectionState currentState;

private boolean dragImageUnderneath;
//buttons
private JRadioButton editSelection; //drag underneath = false
private JRadioButton editImage; //drag underneath = true

ToolSelect(ImageModel model, String iconSource) {
	super(model, iconSource);
	//set up upper card
	upperCard.setLayout(new CardLayout());
	//set up upper card
	JLabel infoText = new JLabel("Select tool - Not yet implemented");
	infoText.setHorizontalAlignment(SwingConstants.CENTER);
	infoText.setVerticalAlignment(SwingConstants.CENTER);
	upperCard.add(infoText, 0);
	
	editSelection = new JRadioButton("Modify selection boundary");
	editImage = new JRadioButton("Modify image");
	
	ButtonGroup selectModeButtons = new ButtonGroup();
	selectModeButtons.add(editSelection);
	selectModeButtons.add(editImage);
	
	upperCard.add(editSelection);
	upperCard.add(editImage);
	
	deselectButton = new JButton("Deselect");
	upperCard.add(deselectButton);
	
	//initially nothing is selected
	currentState = SelectionState.NOTHING_SELECTED;
	start = new int[2];
	end = new int[2];
}

//is a coordinate within the seletion bounds
boolean inSelectionBounds(int[] currentPosition) {
	//if not in finished state, then just return false for now
	if (currentState == SelectionState.NOTHING_SELECTED ||
	    currentState == SelectionState.CREATING_SELECTION) return false;
	
	//if both equal, then there's no selection, so false
	if (start[0] == end[0] || start[1] == end[1]) return false;
	
	boolean[] coordinateInBounds = new boolean[2];
	
	//check each coordinate
	for (int i = 0; i < 2; i++) {
		//if start > end then is i < start, i >= end
		if (start[i] > end[i]) coordinateInBounds[i] = (
			  currentPosition[i] > end[i] &&
			  currentPosition[i] < start[i]
		); //else is i >= start, i < end
		else coordinateInBounds[i] = (
			  currentPosition[i] > start[i] &&
			  currentPosition[i] < end[i]
		);
	} //return if both coordinates are in bounds
	return (coordinateInBounds[0] && coordinateInBounds[1]);
}

void clearSelection() {
	clearSelectionBox();
	currentState = SelectionState.NOTHING_SELECTED;
}

void clearSelectionBox() {
	//remove selection box
}

void drawSelectionBox(int[] currentPosition) {
	clearSelectionBox(); //clear a current selection box
	//then draw
}

private int[] getIntArray(int x, int y) {
	int[] tmp = new int[2];
	tmp[0] = x; tmp[1] = y;
	return tmp;
}

//to drag selection

private boolean dragStarted;
private int[] distanceClickedFromStart;

//if drag isnt started, get distance of clicked coordinate from x and y coordinates
//if drag is started,
//     redraw selection box based on the current starting point and the current location of the pointer

@Override
public void toolDragged(MouseEvent e) {
	switch (currentState) {
		case NOTHING_SELECTED:
			//if we haven't initialized selection creation then do nothing
			break;
		case CREATING_SELECTION:
			drawSelectionBox(getIntArray(e.getX(), e.getY()));
			break;
		case SOMETHING_SELECTED:
			//if clicking is in bounds, drag selection
			
			//else, clear selection
			break;
		default:
			break;
	}
}


@Override
public void toolPressed(MouseEvent e) {
	switch (currentState) {
		case NOTHING_SELECTED:
			start[0] = e.getX();
			start[1] = e.getY();
			break;
		case CREATING_SELECTION:
			break;
		case SOMETHING_SELECTED:
			break;
		default:
			break;
	}
	if (currentState == SelectionState.NOTHING_SELECTED) {
		
		currentState = SelectionState.CREATING_SELECTION;
	}
}

@Override
public void toolReleased(MouseEvent e) {
	switch (currentState) {
		case NOTHING_SELECTED:
			break;
		case CREATING_SELECTION:
			int currentX = e.getX();
			int currentY = e.getY();
			//
			if (currentX != start[0] && currentY != start[1]) {
			
			}
			break;
		case SOMETHING_SELECTED:
			break;
		default:
			break;
	}
}

@Override public void toolEntered(MouseEvent e) { }
@Override public void toolExited(MouseEvent e) { }
@Override public void toolMoved(MouseEvent e) { }
@Override public void toolClicked(MouseEvent e) { }
@Override public void draw(int x, int y, int color, Canvas.DrawMode drawMode) { }
@Override public void drawBrush(int x, int y, int color, Canvas.DrawMode drawMode) { }
@Override public void drawCursor(int x, int y, int color) {

}
@Override public void toolKeyPressed(KeyEvent e) { }
@Override public void toolKeyReleased(KeyEvent e) { }
@Override public void toolKeyTyped(KeyEvent e) { }
}
