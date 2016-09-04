package com.lazymachine.filesystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.FileVisitor;

public class FileSystemModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FileSystem.class).to(FileSystemImpl.class);
        bind(FileTreeTraverser.class).to(FileTreeTraverserImpl.class);
        bind(TextFileWriter.class).to(TextFileWriterImpl.class);
        bind(FileVisitor.class).to(FileListingVisitor.class);
    }

    // This needs to be singleton so that when it is injected into another object for use it will be using the same printWriter object to write to the same file
    // Should this ALWAYS be a Singleton in the future?
    @Provides
    @Singleton PrintWriter providePrintWriter() {
        PrintWriter printWriter = null;
        try {
            // TODO: Extract this out to the resource bundle
            printWriter = new PrintWriter("C:\\TEMP\\LazymachineOutput.txt");
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return printWriter;
    }

}
