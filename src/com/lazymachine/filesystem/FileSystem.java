package com.lazymachine.filesystem;

public interface FileSystem {

    // TODO: Eventually this might be something of a functional apply method taking a filesystem start path and applying a collection of functions in order
    void scanFileSystemWithSummary(String traversalStartPath);

    // TODO: Research if and how it makes sense to provide the traversalStartPath in the constructor arguments. This allow every member variable for the FileSystemImpl instance to be final
    // TODO: Eventually this might be something of a functional apply method taking a filesystem start path and applying a collection of functions in order
    // TODO: should I get rid of this method or find a way to prevent a summary from being generated with this is called.
    void scanFileSystem(String traversalStartPath);

}
