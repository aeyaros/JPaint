package com.jpaint;

import javax.swing.*;
import java.awt.event.ActionListener;

class MenuItem extends JMenuItem {
    MenuItem(String label, int keyEvent, int modifiers, JMenu menu, int mnemonic, ActionListener listener) {
        super(label);
        this.addActionListener(listener);
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyEvent, modifiers);
        this.setAccelerator(keyStroke);
        this.setMnemonic(mnemonic);
        menu.add(this);
    }

    //no key shortcut
    MenuItem(String label, JMenu menu, int mnemonic, ActionListener listener) {
        super(label);
        this.addActionListener(listener);
        this.setMnemonic(mnemonic);
        menu.add(this);
    }
}
