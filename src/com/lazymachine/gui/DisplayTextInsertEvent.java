package com.lazymachine.gui;

public class DisplayTextInsertEvent {

    private final String text;

    public DisplayTextInsertEvent(String text) {
        this.text = text; }

    public String getText() {
        return text;
    }

}
