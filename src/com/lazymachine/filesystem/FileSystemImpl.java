package com.lazymachine.filesystem;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.lazymachine.common.ResourceBundleProvider;
import com.lazymachine.gui.FinishUserInterfaceThreadEvent;
import com.lazymachine.gui.TearDownGuiDisplayEvent;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

import static com.lazymachine.ResourceBundleNames.FILE_SYSTEM;

@Singleton
public class FileSystemImpl implements FileSystem {

    private final FileTreeTraverser fileTreeTraverser;
    private final EventBus eventBus;
    private final TextFileWriter textFileWriter;
    private final String FAILED_PATH_VISITS_MESSAGE;
    private final String TRAVERSAL_TIME_MESSAGE;
    private final String TIME_UNIT_SECONDS;
    private final String SUCCESSFULLY_VISITED_FILES_MESSAGE;
    private final String SUCCESSFULLY_VISITED_DIRECTORIES_MESSAGE;

    // Every FileTreeTraverser instance should have the fileCount, directoryCount and the failedVisits. Then when we want to get a rollup of all the counts and failed visits we call methods on each FileTreeTraverserInstance and it gives us our rollup.
    // this is much more scalable and also removes the state from the FileSystem instance itself. If we do this then we can move the increment and count methods to each FileTreeTraverser instance
    private FileTreeTraverserThreadWatcher fileTreeTraverserThreadWatcher;
    private Long startTime;
    private boolean withSummary;

    @Inject
    FileSystemImpl(FileTreeTraverser fileTreeTraverser, EventBus eventBus, ResourceBundleProvider resourceBundleProvider, TextFileWriter textFileWriter) {
        this.fileTreeTraverser = fileTreeTraverser;
        this.textFileWriter = textFileWriter;
        this.eventBus = eventBus;
        this.withSummary = false;
        ResourceBundle fileSystemResourceBundle = resourceBundleProvider.getBundle(FILE_SYSTEM.getBundleName());
        FAILED_PATH_VISITS_MESSAGE               = fileSystemResourceBundle.getString("filesystem.failed.path.visits.message");
        TRAVERSAL_TIME_MESSAGE                   = fileSystemResourceBundle.getString("filesystem.traversal.time.message");
        TIME_UNIT_SECONDS                        = fileSystemResourceBundle.getString("filesystem.time.unit.seconds");
        SUCCESSFULLY_VISITED_FILES_MESSAGE       = fileSystemResourceBundle.getString("filesystem.successfully.visited.files.message");
        SUCCESSFULLY_VISITED_DIRECTORIES_MESSAGE = fileSystemResourceBundle.getString("filesystem.successfully.visited.directories.message");
    }

    public void scanFileSystemWithSummary(String traversalStartPath) {
        withSummary = true;
        startTime = System.currentTimeMillis(); // Timing code /may/ goes away after we have tested performance extensively
        scanFileSystem(traversalStartPath);
    }

    public void scanFileSystem(String traversalStartPath) {
        fileTreeTraverser.setTraversalStartPath(traversalStartPath);
        fileTreeTraverserThreadWatcher = new FileTreeTraverserThreadWatcher(fileTreeTraverser);
        Thread watcherThread = new Thread(fileTreeTraverserThreadWatcher);
        watcherThread.start();
    }

    private void printToFile(String string) {
        textFileWriter.println(string);
    }

    @Subscribe
    public void terminateFileTraversalEvent(TerminateFileTraversalEvent event) {
        eventBus.post(new TearDownGuiDisplayEvent());
    }

    // TODO: go throughout lazymachine and make sure we are using object Long we don't have memory concerns so that we are future proof and don't have to worry about extremely large filesystems
    // here is where we would rollup failed path visits across multiple FileTreeTraversers
    private List<Path> rolledUpFailedPathVisits() {
        return fileTreeTraverser.getFailedPathVisits();
    }

    private void outputTraversalSummary(List<String> summaryRollup) {
        System.out.println("\n");
        for (String summary: summaryRollup) {
            System.out.println(summary);
            printToFile(summary + "\n");
        }
        textFileWriter.flush();
    }

    private void outputFileTraversalSummaryToFile() {
        printToFile(SUCCESSFULLY_VISITED_FILES_MESSAGE + successfullyVisitedFileCountRollup());
        printToFile(SUCCESSFULLY_VISITED_DIRECTORIES_MESSAGE + successfullyVisitedDirectoryCountRollup());
        textFileWriter.flush();
    }

    // This is where we would rollup successfully visited file counts
    private Long successfullyVisitedFileCountRollup() {
        return fileTreeTraverser.getSuccessfullyVisitedFileCount();
    }

    // this is where we would rollup successfully visited file counts
    private Long successfullyVisitedDirectoryCountRollup() {
        return fileTreeTraverser.getSuccessfullyVisitedDirectoryCount();
    }

    private class FileTreeTraverserThreadWatcher implements Runnable {

        private final Thread traverserThread;

        FileTreeTraverserThreadWatcher(FileTreeTraverser fileTreeTraverser) {
            traverserThread = new Thread(fileTreeTraverser);
        }

        @Override
        public void run() {
            traverserThread.start();
            try {
                traverserThread.join();
                eventBus.post(new FinishUserInterfaceThreadEvent());
                if (withSummary == true) {
                    outputTraverserThreadSummary();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void outputTraverserThreadSummary() {
            Long endTime = System.currentTimeMillis();
            Long duration = endTime - startTime;
            String failedPathVisits    = FAILED_PATH_VISITS_MESSAGE + rolledUpFailedPathVisits();
            String systemTraversalTime = TRAVERSAL_TIME_MESSAGE + duration / 1000 + TIME_UNIT_SECONDS;
            outputTraversalSummary(Lists.newArrayList(failedPathVisits, systemTraversalTime));
            //	TODO: RESEARCH THIS -->	    gui.tearDownDisplay(); // This might be doing something to kill the trayicon app. check this out
            outputFileTraversalSummaryToFile();
            textFileWriter.close();
        }

    }

}
