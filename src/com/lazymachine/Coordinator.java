package com.lazymachine;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.lazymachine.common.ResourceBundleProvider;
import com.lazymachine.filesystem.FileSystem;
import com.lazymachine.gui.GraphicalUserInterface;

import java.util.ResourceBundle;

import static com.lazymachine.ResourceBundleNames.LAZYMACHINE;

// This class is a coordinator between all of the different modules used for lazymachine such as Filesystem, GUI , Hashing, Encryption and Http. It should coordinate the
// scanning and hashing of files and folders and the uploading and downloading of files and metadata to the lazymachine servers.
@Singleton
public class Coordinator {

	private final GraphicalUserInterface gui;
	private final FileSystem filesystem;
	private final Integer ROWS;
	private final Integer COLUMNS;
	private final String START_PATH;

	@Inject
	public Coordinator(GraphicalUserInterface gui, FileSystem filesystem, ResourceBundleProvider resourceBundleProvider) {
		this.gui = gui;
		this.filesystem = filesystem;
		ResourceBundle lazymachineResourceBundle = resourceBundleProvider.getBundle(LAZYMACHINE.getBundleName());
		ROWS       = new Integer(lazymachineResourceBundle.getString("lazymachine.gui.rows.size"));
		COLUMNS    = new Integer(lazymachineResourceBundle.getString("lazymachine.gui.columns.size"));
		START_PATH = lazymachineResourceBundle.getString("lazymachine.filesystem.scan.startPath");
	}

	public void start() {
		try {
			filesystem.scanFileSystemWithSummary(START_PATH);
			gui.startDisplayWith(COLUMNS, ROWS);
		} catch (Exception e) {
			//	TODO: log the exception
			gui.cleanUp();
		}

	}

}
