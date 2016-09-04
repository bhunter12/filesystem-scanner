package com.lazymachine.filesystem;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.lazymachine.gui.DisplayTextInsertEvent;
import net.jcip.annotations.GuardedBy;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

// Right now this is a singleton but in the future, could we have multiple FileTreeWalkers?
@Singleton
class FileTreeTraverserImpl implements FileTreeTraverser {

    private final FileListingVisitor fileListingVisitor;
    private final EventBus eventBus;
    private final TextFileWriter textFileWriter;

    @GuardedBy("this") private long fileCount;
    @GuardedBy("this") private long directoryCount;
    @GuardedBy("this") private List<Path> failedVisits;
    private String traversalStartPath; // This may be need to be updated if we have more than one fileTreeTraverser but at least this is the single point of reference because this class is Singleton and DEFINITELY has state! (At least for now)

    @Inject
    public FileTreeTraverserImpl(FileListingVisitor fileListingVisitor, EventBus eventBus, TextFileWriter textFileWriter) {
        this.fileListingVisitor = fileListingVisitor;
        this.eventBus = eventBus;
        this.textFileWriter = textFileWriter;
        this.fileCount = 0L;
        this.directoryCount = 0L;
        this.failedVisits = new ArrayList<Path>();
    }

    public void setTraversalStartPath(String traversalStartPath) {
        this.traversalStartPath = traversalStartPath;
    }

    @Override
    public void run() {
        traverseFileTree(traversalStartPath);
    }

    // TODO: use something like JMockit to be able to mock static methods and increase code coverage to 100%!
    public boolean traverseFileTree(String startPath) {
        try {
            Files.walkFileTree(Paths.get(startPath), fileListingVisitor);
        } catch (IOException e) {
            eventBus.post(new DisplayTextInsertEvent(e + "\n"));
            textFileWriter.println(e);
        }
        return true; // TODO: make sure this returns a type so we can test it? Get rid of the hardcoded boolean return value
    }

    synchronized public Long getSuccessfullyVisitedFileCount() {
        return fileCount;
    }

    // Should we keep this method and should it be synchronized? What if we have more than one FileTreeTraverser?
    synchronized public Long getSuccessfullyVisitedDirectoryCount() {
        return directoryCount;
    }

    synchronized public List<Path> getFailedPathVisits() {
        return failedVisits;
    }

    @Subscribe
    public void fileVisitIncrementEvent(SuccessfulFileVisitIncrementEvent event) {
        incrementSuccessfullyVisitedFileCount();
    }

    // Should we keep this method and should it be syncrhonized? What if we have more than one FileTreeTraverser?
    synchronized private void incrementSuccessfullyVisitedFileCount() {
        ++fileCount;
    }

    @Subscribe
    public void directoryVisitIncrementEvent(SuccessfulDirectoryVisitIncrementEvent event) {
        incrementSuccessfullyVisitedDirectoryCount();
    }

    // Should we keep this method and should it be synchronized? What if we have more than one FileTreeTraverser?
    synchronized private void incrementSuccessfullyVisitedDirectoryCount() {
        ++directoryCount;
    }

    @Subscribe
    public void failedPathVisitEvent(FailedPathVisitEvent event) {
        addFailedPathVisit(event.getFailedPath());
    }

    // Should we keep this method and should it be synchronized? What if we have more than one FileTreeTraverser?
    synchronized private void addFailedPathVisit(Path failedVisit) {
        failedVisits.add(failedVisit);
    }

}
