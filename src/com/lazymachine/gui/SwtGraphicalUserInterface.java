package com.lazymachine.gui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.lazymachine.filesystem.TerminateFileTraversalEvent;
import net.jcip.annotations.GuardedBy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

// TODO: Further refactor this into smaller methods and other cohesive classes to try to aid in unit testing
@Singleton
public class SwtGraphicalUserInterface implements GraphicalUserInterface {

    private final Display display;
    private final Shell shell;
    private final Text text;
    private final GC graphicsContext;
    private final EventBus eventBus;
    private final Tray tray;

    @GuardedBy("this") private boolean isFinished;
    private Image highlightedTrayIcon; // both images are not thread safe!
    private Image trayIcon;

    @Inject
    public SwtGraphicalUserInterface(Display display, Shell shell, Text text, GC graphicsContext, EventBus eventBus) {
        this.display = display;
        this.shell = shell;
        this.text = text;
        this.graphicsContext = graphicsContext;
        this.eventBus = eventBus;
        this.isFinished = false;
        this.tray = display.getSystemTray(); // Should we encapsulate the providing of the system tray into another object and have that object deal with the NPE if we don't have a display or a system tray? Perhaps this object can be injected directly into this class using guice?
    }

    public void startDisplayWith(int columns, int rows) {
        setupTrayIcon();
        text.setSize(text.computeSize(width(columns), height(rows)));
        terminateFileTraversalOnCloseEvent();
        shell.pack(); // This auto resizes the widget (shell windows) to its preferred size. It only uses as much space as it needs. Find a method name to call this for he SGUI interface.
        shell.open();
        listenToDisplayEvents();
    }

    private void terminateFileTraversalOnCloseEvent() {
        shell.addListener(SWT.Close, new Listener() {
            public void handleEvent(Event event) {
                eventBus.post(new TerminateFileTraversalEvent());
            }
        });
    }

    private int width(int columns) {
        FontMetrics fontMetrics = graphicsContext.getFontMetrics();
        return columns * fontMetrics.getAverageCharWidth();
    }

    private int height(int rows) {
        return rows * text.getLineHeight();
    }

    // TODO: This is not called when the file tree traversal completes and the app terminates itself and so other parts of the display are not closed!!!! This is probably why the tray icon remains even after closing the window!!!! THIS IS A MEMORY LEAK!!! FIX THIS!!!!!
    // This is recommended to be run by any client of GUI when shutting down after using it.
    public void cleanUp() {
        if (display.isDisposed()) {
            display.dispose();
        }
        highlightedTrayIcon.dispose();
        trayIcon.dispose();
        graphicsContext.dispose();
        text.dispose();
        shell.dispose();
        tray.dispose();
        System.err.println("cleanUp()");
    }

    public void insertText(String textToInsert) {
        text.insert(textToInsert);
    }

    // Should this be synchronized? A: Probably not because it is only called from one place but double check to be sure
    synchronized private boolean isEventLoopFinished() {
        return isFinished;
    }

    @Subscribe
    public void finishUserInterfaceThreadEvent(FinishUserInterfaceThreadEvent event) {
        System.err.println("finishUserInterfaceThreadEvent handler executed");
        finishUserInterfaceThread();
    }

    // 1) Do we need this method? 2) does the method need to be synchronized?
    private synchronized void finishUserInterfaceThread() {
        isFinished = true;
    }

    // TODO: find a better name for this method. Also, how can we make isFinished a local variable or just thread safe?
    // Is there another way other than an isFinished variable to use as a listener on whether the background thread has been completed?
    // Rename this method to something like startEventDisplayLoop() or listenToDisplayEvents()
    public void listenToDisplayEvents() {
        while (!isEventLoopFinished()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        tearDownDisplay();
    }

    private void setupTrayIcon() {
        if (tray == null) { // This would be handled in the object that provides the system tray
            System.out.println ("The system tray is not available");
        } else {
            final TrayItem trayItem = new TrayItem (tray, SWT.NONE);
            trayItem.setToolTipText("Lazymachine");
            trayItem.addListener(SWT.Show, onEventPrintTextFrom("show"));
            trayItem.addListener(SWT.Hide, onEventPrintTextFrom("hide"));
            trayItem.addListener(SWT.Selection, onEventPrintTextFrom("selection"));
            trayItem.addListener(SWT.DefaultSelection, onEventPrintTextFrom("default selection"));
            final Menu menu = new Menu (shell, SWT.POP_UP);
            for (int i = 0; i < 8; i++) {
                MenuItem mi = new MenuItem (menu, SWT.PUSH);
                mi.setText ("Item" + i);
                mi.addListener (SWT.Selection, new Listener () {
                    @Override
                    public void handleEvent (Event event) {
                        System.out.println("selection " + event.widget);
                    }
                });
                if (i == 0) menu.setDefaultItem(mi);
            }
            trayItem.addListener(SWT.MenuDetect, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    menu.setVisible(true);
                }
            });
            trayIcon = new Image(display, 16, 16);
            highlightedTrayIcon = new Image (display, 16, 16);
            GC trayIconGraphicsContext = new GC(trayIcon);
            trayIconGraphicsContext.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
            trayIconGraphicsContext.fillRectangle(trayIcon.getBounds());
            trayIconGraphicsContext.dispose();
            trayItem.setImage(trayIcon);
            trayItem.setHighlightImage(highlightedTrayIcon);
        }
    }

    private Listener onEventPrintTextFrom(final String text) {
        return new Listener() {
            @Override public void handleEvent(Event event) {
                System.out.println(text);
            }
        };
    }

    @Subscribe
    public void tearDownGuiDisplayEvent(TearDownGuiDisplayEvent event) {
        System.err.println("tearDownGuiDisplayEvent handler executed");
        tearDownDisplay();
    }

    @Subscribe
    public void displayTextInsertEvent(final DisplayTextInsertEvent event) {
        display.asyncExec(new Runnable() {
            @Override public void run() {
                insertText(event.getText());
            }
        });
    }

    // can this be replaced by cleanUp?
    public void tearDownDisplay() {
        if (!shell.isDisposed()) { // Why are we checking if the shell is disposed? Maybe we should close the shell anyway just to be safe?
            try {
                Thread.sleep(500);
            } catch (Throwable th) {
                th.printStackTrace();
            } finally {
                cleanUp();
            }
        }
    }

}
