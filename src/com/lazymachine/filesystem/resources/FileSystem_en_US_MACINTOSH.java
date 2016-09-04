package com.lazymachine.filesystem.resources;

import java.util.ListResourceBundle;

public class FileSystem_en_US_MACINTOSH extends ListResourceBundle {

    public Object[][] getContents() {
        return contents;
    }

    private Object[][] contents = {
            {"filesystem.file.visit.status.accessDenied", "Access denied for: "},
            {"filesystem.file.visit.status.processingDirctory", "Processing directory: "},
            {"filesystem.file.visit.status.processingFile", "Processing file: "},
            {"filesystem.file.visit.status.skippingDirectory","Skipping directory: "},
            {"filesystem.successfully.visited.files.message", "Successfully visited files: "},
            {"filesystem.successfully.visited.directories.message", "Successfully visited directories: "},
            {"filesystem.failed.path.visits.message", "Failed Path Visits: "},
            {"filesystem.traversal.time.message", "File system traversal took: "},
            {"filesystem.time.unit.seconds", " seconds "}
    };

}
