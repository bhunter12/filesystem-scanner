package com.lazymachine.resources;

import java.util.ListResourceBundle;

public class Lazymachine_en_US_LINUX extends ListResourceBundle {

    public Object[][] getContents() {
        return contents;
    }

    private Object[][] contents = {
        {"lazymachine.filesystem.scan.startPath", "/"},
        {"lazymachine.gui.rows.size", "20"},
        {"lazymachine.gui.columns.size", "200"}
    };

}
