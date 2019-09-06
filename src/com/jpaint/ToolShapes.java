package com.jpaint;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ToolShapes extends ToolLine {
    //values
    private final int SIDES_IN_A_CIRCLE = 64;
    private final int MINIMUM_POSSIBLE_NUMBER_OF_SIDES = 3;
    private final int MAX_ALLOWED_NUMBER_OF_SIDES = 9; //really 8; at max number setting we will do a circle

    //colors
    private int strokeColor;
    private int fillColor;
    //states
    private boolean useStroke;
    private boolean shouldFill;
    private int numberOfSides;
    //objects
    private JSlider sidesChangerSlider;
    private JComboBox<Integer> shapeBox;

    //for combo box - the shape selection menu
    private String sideIconPathName = "icons/shape_tool_sides_icons/";
    private ImageIcon[] shapeIcons;
    private String[] shapeNames = {
            "Triangle",
            "Square",
            "Pentagon",
            "Hexagon",
            "Heptagon",
            "Octagon",
            "Circle"
    };
    private String[] shapeFileNames = {
            "3_black_triangle.png",
            "4_black_square.png",
            "5_black_pentagon.png",
            "6_black_hexagon.png",
            "7_black_heptagon.png",
            "8_black_octagon.png",
            "9_black_circle.png"
    };

    ToolShapes(ImageModel model, String iconSource) {
        super(model, iconSource);
        widthSlider.setMaximum(11); //we dont want shape borders to be too thick

        upperCard.removeAll();
        upperCard.setLayout(new GridLayout(1, 0));

        //panel holding everything
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        upperCard.add(mainPanel);

        //setup combo box
        int numShapes = shapeNames.length;
        System.out.println("Number of shapes = " + numShapes);
        shapeIcons = new ImageIcon[numShapes];
        Integer[] integerArray = new Integer[numShapes];
        for (int i = 0; i < numShapes; i++) {
            integerArray[i] = i;
            shapeIcons[i] = getIconFromFile(sideIconPathName + shapeFileNames[i]);
            if (shapeIcons[i] != null) shapeIcons[i].setDescription(shapeNames[i]);
        }
        shapeBox = new JComboBox<>(integerArray);
        BoxRenderer shapeBoxRenderer = new BoxRenderer();
        shapeBox.setMaximumSize(new Dimension(
                (int) (ButtonToolbar.TOOL_BUTTON_SIZE * 3.5),
                ButtonToolbar.TOOL_BUTTON_SIZE
        ));
        shapeBox.setPreferredSize(new Dimension(
                (int) (ButtonToolbar.TOOL_BUTTON_SIZE * 3.5),
                ButtonToolbar.TOOL_BUTTON_SIZE
        ));
        shapeBox.setRenderer(shapeBoxRenderer);
        shapeBox.setMaximumRowCount(numShapes);
        shapeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setNumberOfSides(shapeBox.getSelectedIndex() + 3);
            }
        });
        shapeBox.setSelectedIndex(0);
        numberOfSides = 3;

        mainPanel.add(shapeBox);
        mainPanel.add(Box.createRigidArea(new Dimension(ButtonToolbar.TOOL_BUTTON_GAP, ButtonToolbar.TOOL_BUTTON_GAP)));


        //setting border mode/width
        JPanel borderOptions = new JPanel();
        borderOptions.setLayout(new BoxLayout(borderOptions, BoxLayout.X_AXIS));
        ButtonGroup borderOptionButtons = new ButtonGroup();

        //first button
        ButtonToolbar borderOnly = new ButtonToolbar("icons/shape_tool_mode_icons/onlystroke.png"); //stroke only
        borderOnly.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    setFillEnabled(false);
                    setStrokeEnabled(true);
                    System.out.println("Border only");
                }
            }
        });

        //set values for first button; button event doesnt happen initially
        setFillEnabled(false);
        setStrokeEnabled(true);
        borderOnly.setSelected(true);

        //second button
        ButtonToolbar borderWithFill = new ButtonToolbar("icons/shape_tool_mode_icons/strokeandfill.png"); //border + fill
        borderWithFill.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    setFillEnabled(true);
                    setStrokeEnabled(true);
                    System.out.println("Border and fill");
                }
            }
        });

        //third button
        ButtonToolbar fillOnly = new ButtonToolbar("icons/shape_tool_mode_icons/onlyfill.png"); //fill only
        fillOnly.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    setFillEnabled(true);
                    setStrokeEnabled(false);
                    System.out.println("Fill only");
                }
            }
        });
        //add buttons to panel
        borderOptions.add(Box.createRigidArea(new Dimension(
                ButtonToolbar.TOOL_BUTTON_GAP,
                ButtonToolbar.TOOL_BUTTON_GAP
        )));
        borderOptions.add(borderOnly);
        borderOptions.add(Box.createRigidArea(new Dimension(
                ButtonToolbar.TOOL_BUTTON_GAP,
                ButtonToolbar.TOOL_BUTTON_GAP
        )));
        borderOptions.add(borderWithFill);
        borderOptions.add(Box.createRigidArea(new Dimension(ButtonToolbar.TOOL_BUTTON_GAP,
                ButtonToolbar.TOOL_BUTTON_GAP)));
        borderOptions.add(fillOnly);
        borderOptions.add(Box.createRigidArea(new Dimension(ButtonToolbar.TOOL_BUTTON_GAP, ButtonToolbar.TOOL_BUTTON_GAP)));

        //add buttons to button group
        borderOptionButtons.add(borderOnly);
        borderOptionButtons.add(borderWithFill);
        borderOptionButtons.add(fillOnly);

        mainPanel.add(borderOptions, BorderLayout.WEST);
        JPanel widthPanel = new JPanel();
        widthPanel.setLayout(new BoxLayout(widthPanel, BoxLayout.X_AXIS));
        widthPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Border Width"));
        widthPanel.add(widthLabel);
        widthPanel.add(widthSlider);
        mainPanel.add(widthPanel);
    }

    private ImageIcon getIconFromFile(String path) {
        try {
            Image image = ImageIO.read(getClass().getResource(path));
            if (Main.IS_MAC) {
                image = image.getScaledInstance(
                        ButtonToolbar.TOOL_BUTTON_SIZE / 3,
                        ButtonToolbar.TOOL_BUTTON_SIZE / 3,
                        Image.SCALE_SMOOTH
                );
            } else {
                image = image.getScaledInstance(
                        ButtonToolbar.TOOL_BUTTON_SIZE,
                        ButtonToolbar.TOOL_BUTTON_SIZE,
                        Image.SCALE_SMOOTH
                );
            }
            return new ImageIcon(image);
        } catch (Exception e) {
            System.err.println("Couldn't get icon from " + path);
            e.printStackTrace();
            return null;
        }
    }

    //Based on the java custom combobox demo
    private class BoxRenderer extends JLabel implements ListCellRenderer {
        BoxRenderer() {
            this.setOpaque(true);
            this.setHorizontalAlignment(LEFT);
            this.setVerticalAlignment(CENTER);
        }

        public Component getListCellRendererComponent(
                JList list,
                Object value, int index, boolean isSelected, boolean cellHasFocus
        ) {
            //get index
            int selectedIndex = (Integer) value;
            Dimension size;
            if (Main.IS_MAC) {
                size = new Dimension(
                        ButtonToolbar.TOOL_BUTTON_SIZE,
                        ButtonToolbar.TOOL_BUTTON_SIZE / 2
                );
            } else {
                size = new Dimension(
                        ButtonToolbar.TOOL_BUTTON_SIZE,
                        ButtonToolbar.TOOL_BUTTON_SIZE
                );
            }
            setPreferredSize(size);
            setMinimumSize(size);

            //set background/foreground colors
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            //set icon
            ImageIcon icon = shapeIcons[selectedIndex];
            String string = shapeNames[selectedIndex];
            setIcon(icon);
            setText(string);
            setFont(list.getFont());
            return this;
        }
    }

    private void setStrokeEnabled(boolean isEnabled) {
        useStroke = isEnabled;
        if (isEnabled) {
            widthSlider.setEnabled(true);
            width = widthSlider.getValue();
        } else {
            widthSlider.setEnabled(false);
            width = 1;
        }
    }

    private void setFillEnabled(boolean enabled) {
        shouldFill = enabled;
    }

    //if not using stroke, then the width is as small as possible
    private int getStrokeWidth() {
        if (useStroke) return width;
        else return 1;
    }

    //if no stroke, then color of thin border and fill should match
    private int getFillColor() {
        if (useStroke) return fillColor;
        else return strokeColor;
    }

    //minimum of 3 sides
    private void setNumberOfSides(int n) {
        numberOfSides = n;
        if (numberOfSides < MINIMUM_POSSIBLE_NUMBER_OF_SIDES)
            numberOfSides = MINIMUM_POSSIBLE_NUMBER_OF_SIDES;
    }

    @Override
    public void draw(int x, int y, int color, Canvas.DrawMode drawMode) {
        model.setPixel(x, y, color, drawMode);
    }

    @Override
    public void drawBrush(int x, int y, int color, Canvas.DrawMode drawMode) {
        makeCircle(x, y, color, getStrokeWidth(), false, drawMode);
    }

    private void drawShape(int originX, int originY, int currentX, int currentY, int type) {
        //calculate values based on current and past mouse positions
        int w = currentX - originX; //relative x coordinate to originX
        int h = currentY - originY; //relative y coordinate to originY
        int radius = (int) Math.sqrt((w * w) + (h * h)); //distance between points
        //calculate angle offset from horizontal line so first point of polygon matches current location of pointer
        double offset = Math.atan2(h, w);

        if (type == MAX_ALLOWED_NUMBER_OF_SIDES) { //draw circle if slide is at this position
            //draw a circle - use approximation with large number of sidess
            makeRegularPolygon(
                    originX, originY, SIDES_IN_A_CIRCLE, radius, offset, strokeColor, true, Canvas.DrawMode.USE_OVERLAY);
        } else if (type >= MINIMUM_POSSIBLE_NUMBER_OF_SIDES && type < MAX_ALLOWED_NUMBER_OF_SIDES) {
            //draw a polygon
            makeRegularPolygon(originX, originY, type, radius, offset, strokeColor, true, Canvas.DrawMode.USE_OVERLAY);
        } else return; //else dont draw because input value is too high or too low

        //fill in the shape; dont fill if radius is too small
        if (shouldFill && radius > 1) {
            fill(originX, originY, getFillColor(), Canvas.DrawMode.USE_OVERLAY);
        }
    }

    //when mouse is moved
    @Override
    void refreshPreview(int x1, int y1, int color) {
        model.clearOverlay();
        drawShape(x0, y0, x1, y1, numberOfSides);
        model.refreshView();
    }

    //on first click or mousedown
    @Override
    void startDrawing(int startX, int startY, int color, MouseEvent e) {
        //dont save state until we know the line is getting drawn
        //get current points
        x0 = startX;
        y0 = startY;
        strokeColor = getColorIntByButton(e.getButton());
        fillColor = getAlternateColor(e.getButton());
    }

    //on second click, or mouse release
    @Override
    void finishDrawing(int endX, int endY, int color, MouseEvent e) {
        //we know we are drawing the line now, and so we are going to save the old state
        model.saveCurrentState();
        model.clearOverlay();
        strokeColor = getColorIntByButton(e.getButton());
        fillColor = getAlternateColor(e.getButton());
        drawShape(x0, y0, endX, endY, numberOfSides);
        model.mergeOverlay();
        model.refreshView();
    }

}
