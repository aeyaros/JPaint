package com.jpaint;

/*
Send mouse events to tools
Defined as abstract class because there are two types of mouse listeners
and I don't want to violate single responsibility principle
thus, this class allows for adding and changing tool used by listeners

derived classes actually implement the mouselistener
interfaces and send the events to the tools
*/
public abstract class MouseController {
    Tool[] tools;
    int index;

    MouseController(Tool[] tools) {
        this.tools = tools;
        index = 0;
    }
    public void setTool(int newIndex) {
        index = newIndex % tools.length;
    }
}
