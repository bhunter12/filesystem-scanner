package com.lazymachine.filesystem;

import java.nio.file.Path;
import java.util.List;

interface FileTreeTraverser extends Runnable {

    void setTraversalStartPath(String traversalStartPath);

    boolean traverseFileTree(String startPath);

    Long getSuccessfullyVisitedFileCount();

    Long getSuccessfullyVisitedDirectoryCount();

    List<Path> getFailedPathVisits();

}
