package com.jpaint;


import javax.swing.*;

public class ToolEraser extends ToolPaintBrush {
    private boolean eraseToTransparent;

    ToolEraser(String name, ImageModel model, String iconSource, String selectedIconSource) {
        super(name, model, iconSource, selectedIconSource);

        JRadioButton eraseWhite = new JRadioButton("White");
        eraseWhite.setSelected(true);
        JRadioButton eraseTrans = new JRadioButton("Transparent");
        eraseTrans.setSelected(false);

        eraseWhite.addActionListener(e -> setEraseToTransparent(false));
        eraseTrans.addActionListener(e -> setEraseToTransparent(true));

        ButtonGroup eraserButtons = new ButtonGroup();
        eraserButtons.add(eraseWhite);
        eraserButtons.add(eraseTrans);

        JPanel eraserButtonPanel = new JPanel();
        eraserButtonPanel.setLayout(new BoxLayout(eraserButtonPanel,BoxLayout.X_AXIS));
        eraserButtonPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Eraser type"));

        eraserButtonPanel.add(eraseWhite);
        eraserButtonPanel.add(eraseTrans);

        upperCard.add(eraserButtonPanel);

        //set up as a normal eraser
        setEraseToTransparent(false);
    }

    private void setEraseToTransparent(boolean eraseToTransparent) {
        this.eraseToTransparent = eraseToTransparent;
    }

    //change how the eraser erases
    private int getEraserColor() {
        int value;
        if(eraseToTransparent) value = Color.MIN_VALUE; //erase to transparent
        else value = Color.MAX_VALUE; //erase to white
        return new Color(value,value,value,value).getARGB();
    }

    @Override
    public void draw(int x, int y, int color) {
        makeCircle(x,y,getEraserColor(),radius,negrad,false,false);
        //eraseCircle(x,y,getEraserColor());
    }

    /*
    private void eraseCircle(int origX, int origY, int color) {
        for(int y = negrad; y <= radius; y++) {
            for(int x = negrad; x <= radius; x++) {
                if (x * x + y * y <= radius * radius) //draw if inside bounds of circle
                    model.setPixel(origX + x, origY + y, color, false, false);
            }
        } model.refreshView();
    }*/
}
