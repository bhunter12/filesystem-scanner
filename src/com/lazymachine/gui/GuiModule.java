package com.lazymachine.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class GuiModule extends AbstractModule {

	@Override
	protected void configure() { 
		bind(GraphicalUserInterface.class).to(SwtGraphicalUserInterface.class).in(Singleton.class); // TODO: what does .in(Singleton.class do?
		bind(SystemTray.class).to(SystemTrayImpl.class);
	} 

	@Provides
	@Singleton
	Display provideDisplay() {
		return new Display();
	}
	
	@Provides
	@Singleton
	Shell provideShell(Display display) {
		return new Shell(display);
	}
	
	@Provides
	@Singleton
	Text provideText(Shell shell) {
		return new Text(shell, SWT.V_SCROLL);
	}
	
	@Provides
	@Singleton
	GC provideGraphicsContext(Text text) {
		return new GC(text);
	}
	
}
