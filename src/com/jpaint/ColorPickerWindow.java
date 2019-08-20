package com.jpaint;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class ColorPickerWindow {
    //window elements
    private JFrame frame;
    private JLabel[] labels;
    private JSlider[] sliders;
    private JLabel colorLabel;
    private int COLOR_LABEL_WIDTH = 256;
    private int COLOR_LABEL_HEIGHT = 64;
    private int SLIDER_BORDER = 8;

    //color stuff
    private Color colorToChange;
    private ColorButton colorButton;
    private ColorManager colorManager;

    void close() {
        try {
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));

        } catch (Exception e) {
            System.out.println("Color picker window close event sent even though window already closed. This is not an issue.");
        }
    }

    void save() {
        colorButton.setColor(colorToChange);
        colorManager.notifyTools();
        close();
    }

    ColorPickerWindow() {
        close();
    }

    void setColorPickerWindow(ColorButton colorButton, JFrame mainFrame, ColorManager colorManager) {

        this.colorButton = colorButton; //not used until saving
        this.colorManager = colorManager; //not used until saving
        colorToChange = new Color(colorButton.getColor()); //store a copy of the original color

        frame = new JFrame("Color Picker");

        //set dimensions
        Dimension colorSize = new Dimension(256, 400);
        frame.setResizable(false);
        frame.setMinimumSize(colorSize);
        frame.setMaximumSize(colorSize);
        frame.setPreferredSize(colorSize);
        frame.setSize(colorSize);

        //base panel containing layout
        JPanel colorPanel = new JPanel();
        frame.getContentPane().add(colorPanel);
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

        //add OK and cancel button
        JPanel buttons = new JPanel(new GridLayout(1,0,6,0));

        JButton applyButton = new JButton("Apply");
        JButton cancelButton = new JButton("Cancel");
        buttons.setBorder(new EmptyBorder(17,0,12,0));

        applyButton.addActionListener(e -> save());
        cancelButton.addActionListener(e -> close());

        buttons.add(applyButton);
        buttons.add(cancelButton);
        colorPanel.add(buttons);
        colorPanel.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

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

    private class sliderListener implements ChangeListener {
        private int index;
        sliderListener(int index) { this.index = index; }

        @Override public void stateChanged(ChangeEvent e) {
            updateChannel(index);
        }
    }

    void updateChannel(int i) {
        colorToChange.setChannel(i, sliders[i].getValue());
        labels[i].setText(Color.getChannelString(i) + ":" + colorToChange.getChannel(i));
        setColorPane();
    }

    void setColorPane() {
        colorLabel.setIcon(ColorButton.generateColorIcon(COLOR_LABEL_WIDTH-32, COLOR_LABEL_HEIGHT, colorToChange.getARGB()));
    }
}
