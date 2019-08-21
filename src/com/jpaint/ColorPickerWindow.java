package com.jpaint;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

class ColorPickerWindow {
    //window elements
    private JFrame frame;
    private JLabel[] labels;
    private JSlider[] sliders;
    private JLabel colorLabel;
    private int COLOR_LABEL_WIDTH = 128;
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

    private void save() {
        colorButton.setColor(colorToChange);
        colorManager.notifyTools();
        close();
    }

    //when initially created, we dont use it
    ColorPickerWindow() {
        close();
    }

    //each time color button double clicked, set up the window and open it
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
        JPanel mainPanel = new JPanel();
        frame.getContentPane().add(mainPanel);
        BoxLayout b = new BoxLayout(mainPanel,BoxLayout.Y_AXIS);
        mainPanel.setLayout(b);



        JPanel originalColorLabels = new JPanel();
        JPanel newColorLabels = new JPanel();

        BoxLayout oc = new BoxLayout(originalColorLabels,BoxLayout.Y_AXIS);
        BoxLayout nc = new BoxLayout(newColorLabels,BoxLayout.Y_AXIS);

        JLabel originalLabelText = new JLabel("Original");
        originalLabelText.setHorizontalAlignment(SwingConstants.CENTER);
        originalLabelText.setVerticalAlignment(SwingConstants.CENTER);
        originalColorLabels.add(originalLabelText);

        //label to show original color, is never changed once set
        JLabel originalColorIconLabel = new JLabel();
        setColorPane(originalColorIconLabel);
        BackgroundPanel oldCheckersBG = new BackgroundPanel();
        oldCheckersBG.add(originalColorIconLabel);
        originalColorLabels.add(oldCheckersBG);


        JLabel newLabelText = new JLabel("New");
        newLabelText.setHorizontalAlignment(SwingConstants.CENTER);
        newLabelText.setVerticalAlignment(SwingConstants.CENTER);
        newColorLabels.add(newLabelText);

        //add label to show new color color, changes when user uses controls on the window
        colorLabel = new JLabel();
        BackgroundPanel newCheckersBG = new BackgroundPanel();
        setColorPane(colorLabel);
        newCheckersBG.add(colorLabel);
        newColorLabels.add(newCheckersBG);

        //panel containing both colors
        JPanel colorPanel = new JPanel(new GridLayout(1,2));
        colorPanel.add(originalColorLabels);
        colorPanel.add(newColorLabels);


        mainPanel.add(colorPanel);

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
        for(int i = 1; i < sliderPanels.length + 1; i++) mainPanel.add(sliderPanels[i % sliderPanels.length]);

        //add OK and cancel button
        JPanel buttons = new JPanel(new GridLayout(1,0,6,0));

        JButton applyButton = new JButton("Apply");
        JButton cancelButton = new JButton("Cancel");
        buttons.setBorder(new EmptyBorder(17,0,12,0));

        applyButton.addActionListener(e -> save());
        cancelButton.addActionListener(e -> close());

        buttons.add(applyButton);
        buttons.add(cancelButton);
        mainPanel.add(buttons);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

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
        setColorPane(colorLabel);
    }

    void setColorPane(JLabel pane) {
        pane.setIcon(ColorButton.generateColorIcon(COLOR_LABEL_WIDTH, COLOR_LABEL_HEIGHT, colorToChange.getARGB()));
    }
}
