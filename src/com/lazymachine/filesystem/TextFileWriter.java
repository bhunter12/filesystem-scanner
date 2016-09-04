package com.lazymachine.filesystem;

import java.io.IOException;

interface TextFileWriter {

	void println(String string);
	
	void flush();
	
	void close();

	void println(IOException e);
	
}
