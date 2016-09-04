package com.lazymachine;

import java.io.IOException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.lazymachine.common.CommonModule;
import com.lazymachine.filesystem.FileSystemModule;
import com.lazymachine.gui.GuiModule;


public class Launcher {

	public static void main(String[] arguments) {
		
		Injector injector = Guice.createInjector(new MainModule(), new CommonModule(), new FileSystemModule(), new GuiModule());
	
		Coordinator coordinator = injector.getInstance(Coordinator.class);

		/// Only used for creating dependency graph Image///////
//		Grapher grapher = new Grapher();
//		try {
//			grapher.graph("C:\\TEMP\\lazymachineGraph.dot", injector);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		//////////////
		
		coordinator.start();
	}
	
}
