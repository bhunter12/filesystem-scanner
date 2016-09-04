package com.lazymachine.resources;

import java.util.ListResourceBundle;

public class Lazymachine_en_US_WINDOWS extends ListResourceBundle {

    public Object[][] getContents() {
        return contents;
    }

    private Object[][] contents = {
        {"lazymachine.filesystem.scan.startPath", "C:\\Windows"},
        {"lazymachine.gui.rows.size", "20"},
        {"lazymachine.gui.columns.size", "200"}
    };

}
