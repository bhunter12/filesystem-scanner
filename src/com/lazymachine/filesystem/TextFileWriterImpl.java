package com.lazymachine.filesystem;

import java.io.IOException;
import java.io.PrintWriter;

import com.google.inject.Inject;
import com.google.inject.Singleton;

// THis needs to be a singleton for now. Should it always be a singleton? It does have state since the buffer can hold the text to be output and must be flushed and closed
// There is a problem that if this is a singleton the stream can be closed underneath
@Singleton
class TextFileWriterImpl implements TextFileWriter {

	private final PrintWriter printWriter;
	
	@Inject
	TextFileWriterImpl(PrintWriter printWriter) {
		this.printWriter = printWriter;
	}
	
	@Override
	public void println(String string) {
		printWriter.println(string);
	}

	@Override
	public void flush() {
		printWriter.flush();
	}

	@Override
	public void close() {
		printWriter.close();
	}

	@Override
	public void println(IOException exception) {
		printWriter.println(exception);
	}

}
