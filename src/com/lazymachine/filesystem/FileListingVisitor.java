package com.lazymachine.filesystem;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.lazymachine.common.ResourceBundleProvider;
import com.lazymachine.gui.DisplayTextInsertEvent;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.lazymachine.ResourceBundleNames.FILE_SYSTEM;

@Singleton
public class FileListingVisitor<T> implements FileVisitor<T> {

    private final String ACCESS_DENIED_FOR;
    private final String PROCESSING_DIRECTORY;
    private final String SKIPPING_DIRECTORY;
    private final String PROCESSING_FILE;
    private final EventBus eventBus;
    private final TextFileWriter textFileWriter;

    private boolean isFileTraversalTerminated;

    @Inject
    FileListingVisitor(EventBus eventBus, ResourceBundleProvider resourceBundleProvider, TextFileWriter textFileWriter) {
        this.eventBus = eventBus;
        this.textFileWriter = textFileWriter;
        ResourceBundle fileSystemResourceBundle = resourceBundleProvider.getBundle(FILE_SYSTEM.getBundleName());
        ACCESS_DENIED_FOR    = fileSystemResourceBundle.getString("filesystem.file.visit.status.accessDenied");
        PROCESSING_DIRECTORY = fileSystemResourceBundle.getString("filesystem.file.visit.status.processingDirctory");
        SKIPPING_DIRECTORY   = fileSystemResourceBundle.getString("filesystem.file.visit.status.skippingDirectory");
        PROCESSING_FILE      = fileSystemResourceBundle.getString("filesystem.file.visit.status.processingFile");
        isFileTraversalTerminated = false;
    }

    @Override
    public FileVisitResult visitFile(T file, BasicFileAttributes unusedAttributes) throws IOException {
        final Path currentPath = (Path) file;
        eventBus.post(new SuccessfulFileVisitIncrementEvent());
        outputTraversalStatus(PROCESSING_FILE, currentPath);
        eventBus.post(new DisplayTextInsertEvent(visitStatusText(PROCESSING_FILE, currentPath)));
        return shouldFileTraversalContinue();
    }

    @Override
    public FileVisitResult preVisitDirectory(T directory, BasicFileAttributes unusedAttributes) throws IOException {
        final Path currentPath = (Path) directory;
        if (!Files.isReadable(currentPath) || Files.isSymbolicLink(currentPath)) {
            outputTraversalStatus(SKIPPING_DIRECTORY, currentPath);
            eventBus.post(new DisplayTextInsertEvent(visitStatusText(SKIPPING_DIRECTORY, currentPath)));
            return FileVisitResult.SKIP_SUBTREE;
        } else {
            eventBus.post(new SuccessfulDirectoryVisitIncrementEvent());
            outputTraversalStatus(PROCESSING_DIRECTORY, currentPath);
            eventBus.post(new DisplayTextInsertEvent(visitStatusText(PROCESSING_DIRECTORY, currentPath)));
        }
        return shouldFileTraversalContinue();
    }

    @Override
    public FileVisitResult visitFileFailed(T file, IOException ioException) {
        final Path currentPath = (Path) file;
        if (ioException instanceof AccessDeniedException) {
            outputTraversalStatus(ACCESS_DENIED_FOR, currentPath);
            eventBus.post(new FailedPathVisitEvent(currentPath));
            eventBus.post(new DisplayTextInsertEvent(visitStatusText(ACCESS_DENIED_FOR, currentPath)));
        }
        return shouldFileTraversalContinue();
    }

    private String visitStatusText(String status, Path currentPath) {
        return status + currentPath + "\n";
    }

    @Override
    public FileVisitResult postVisitDirectory(T directory, IOException exception) throws IOException {
        Objects.requireNonNull(directory);
        if (exception != null) {
            throw exception;
        }
        return FileVisitResult.CONTINUE;
    }

    // TODO: see if this can be moved to another class where it's more appropriate
    private void outputTraversalStatus(String status, Path currentPath) {
        System.out.println(status + currentPath);
        textFileWriter.println(status + currentPath);
    }

    @Subscribe
    public void terminateFileTraversalEvent(TerminateFileTraversalEvent event) {
        isFileTraversalTerminated = true;
    }

    private FileVisitResult shouldFileTraversalContinue() {
        if (!isFileTraversalTerminated) {
            return FileVisitResult.CONTINUE;
        } else {
            return FileVisitResult.TERMINATE;
        }
    }

}