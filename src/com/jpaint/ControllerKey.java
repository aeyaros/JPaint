package com.jpaint;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class ControllerKey extends Controller implements KeyListener {
    ControllerKey(Tool[] tools) {
        super(tools);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        tools[index].toolKeyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        tools[index].toolKeyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        tools[index].toolKeyReleased(e);
    }
}