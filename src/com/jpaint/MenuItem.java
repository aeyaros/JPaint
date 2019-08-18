package com.jpaint;

import javax.swing.*;
import java.awt.event.ActionListener;

public class MenuItem extends JMenuItem {
    MenuItem(String label, int keyEvent, int modifiers, JMenu menu, int mnemonic, ActionListener listener) {
        super(label);
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyEvent, modifiers);
        this.setAccelerator(keyStroke);

        this.setMnemonic(mnemonic);

        this.addActionListener(listener);

        menu.add(this);
    }

    MenuItem(String label, int keyEvent, int modifiers, JMenu menu, ActionListener listener) {
        super(label);
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyEvent, modifiers);
        this.setAccelerator(keyStroke);
        this.addActionListener(listener);

        menu.add(this);
    }

}
