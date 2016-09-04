package com.lazymachine.gui;

// TODO: Find out which methods should be synchronized
public interface GraphicalUserInterface {

    void startDisplayWith(int columns, int rows);

    public void insertText(String textToInsert);

    void listenToDisplayEvents();

    void tearDownDisplay();

    void cleanUp();

}
