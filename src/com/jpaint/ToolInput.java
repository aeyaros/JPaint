package com.jpaint;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

//interface for editing the canvas
interface ToolInput {
//key listener
void toolKeyPressed(KeyEvent e);

void toolKeyReleased(KeyEvent e);

void toolKeyTyped(KeyEvent e);

//motion listener
void toolDragged(MouseEvent e);

void toolMoved(MouseEvent e);

//regular listener
void toolClicked(MouseEvent e);

void toolPressed(MouseEvent e);

void toolReleased(MouseEvent e);

void toolEntered(MouseEvent e);

void toolExited(MouseEvent e);

//function used for line drawing tool
void draw(int x, int y, int color);
}
