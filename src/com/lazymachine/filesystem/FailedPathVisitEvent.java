package com.lazymachine.filesystem;


import java.nio.file.Path;

public class FailedPathVisitEvent {

    private final Path failedPath;

    public FailedPathVisitEvent(Path failedPath) {
        this.failedPath = failedPath;
    }

    public Path getFailedPath() {
        return failedPath;
    }

}
