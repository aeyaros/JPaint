package com.jpaint;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowEvent;

public class ColorPickerWindow {
    private JFrame frame;
    private Color colorToChange;
    private JLabel[] labels;
    private JSlider[] sliders;
    private JLabel colorLabel;
    private int COLOR_LABEL_WIDTH = 256;
    private int COLOR_LABEL_HEIGHT = 64;
    private int SLIDER_BORDER = 8;
    private ColorButton colorButton;

    void close() {
        try {
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        } catch (Exception e) {
            System.out.println("Color picker window close event sent even though window already closed. This is not an issue.");
        }
    }

    void save() {
        colorButton.setColor(colorToChange);
        close();
    }

    //initialize without doing anything
    ColorPickerWindow() {
        close();
    }

    ColorPickerWindow(ColorButton colorButton) {
        this.colorButton = colorButton; //dont use until saving

        colorToChange = new Color(colorButton.getColor()); //for internal temporary use

        frame = new JFrame("JPaint | Color Picker");

        //set dimensions
        Dimension colorSize = new Dimension(256, 384);
        frame.setMinimumSize(colorSize);
        frame.setMaximumSize(colorSize);
        frame.setPreferredSize(colorSize);
        frame.setSize(colorSize);

        //base panel containing layout
        JPanel colorPanel = new JPanel();
        frame.getContentPane().add(colorPanel);

        //gridbag layout
        //GridBagLayout gb = new GridBagLayout();
        BoxLayout b = new BoxLayout(colorPanel,BoxLayout.Y_AXIS);

        //add label to show current color
        colorLabel = new JLabel();
        colorLabel.setBackground(java.awt.Color.white);
        colorLabel.setOpaque(true);
        setColorPane();
        colorPanel.add(colorLabel);

        JPanel[] sliderPanels = new JPanel[4];
        sliders = new JSlider[sliderPanels.length];
        labels = new JLabel[sliderPanels.length];

        for(int i = 0; i < sliderPanels.length; i++) {
            sliderPanels[i] = new JPanel(new BorderLayout());

            labels[i] = new JLabel(Color.getChannelString(i) + ":", SwingConstants.LEFT);
            sliderPanels[i].add(labels[i], BorderLayout.NORTH);

            sliders[i] = new JSlider(SwingConstants.HORIZONTAL,Color.MIN_VALUE,Color.MAX_VALUE, colorToChange.getChannel(i));
            sliders[i].addChangeListener(new sliderListener(i));

            sliderPanels[i].add(sliders[i], BorderLayout.SOUTH);
            sliderPanels[i].setBorder(BorderFactory.createEmptyBorder(SLIDER_BORDER,0,0,0));
        }

        //add sliders; red first
        for(int i = 1; i < sliderPanels.length + 1; i++) colorPanel.add(sliderPanels[i % sliderPanels.length]);

        //add OK and cancell button
        JPanel buttons = new JPanel(new GridLayout(1,2));

        JButton cancelButton = new JButton("Cancel");
        JButton okButton = new JButton("OK");
        buttons.setBorder(new EmptyBorder(17,12,12,12));

        cancelButton.addActionListener(e -> { close(); });
        okButton.addActionListener(e -> save());

        buttons.add(okButton);
        buttons.add(cancelButton);
        colorPanel.add(buttons);

        frame.pack();
        frame.setVisible(true);
    }

    private class sliderListener implements ChangeListener {
        private int index;
        sliderListener(int index) { this.index = index; }

        @Override public void stateChanged(ChangeEvent e) {
            updateChannel(index);
        }
    }

    void updateChannel(int i) {
        colorToChange.print();
        System.out.println(i);
        colorToChange.setChannel(i, sliders[i].getValue());
        labels[i].setText(Color.getChannelString(i) + ":" + colorToChange.getChannel(i));
        setColorPane();
    }

    void setColorPane() {
        colorLabel.setIcon(ColorButton.generateColorIcon(COLOR_LABEL_WIDTH, COLOR_LABEL_HEIGHT, colorToChange.getARGB()));
    }
}
