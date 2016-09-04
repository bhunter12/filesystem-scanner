package com.lazymachine.gui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

@Singleton
public class SystemTrayImpl implements SystemTray {

    private final Display display;
    private final Shell shell;
    private final Tray tray;
    private final TrayItem trayItem;

    private Image highlightedTrayIcon; // these are not thread safe!
    private Image trayIcon;
    private Menu menu;

    @Inject
    public SystemTrayImpl(Display display, Shell shell) {
        this.display = display;
        this.shell = shell;
        this.tray = display.getSystemTray();
        this.trayItem = new TrayItem(tray, SWT.NONE);
    }

    private void showSystemTray() {

        trayItem.setToolTipText("Lazymachine");
        menu = buildMenuItems();
        addListenersForTrayItem(trayItem);
        trayIcon =            new Image(display, 16, 16);
        highlightedTrayIcon = new Image(display, 16, 16);
        GC trayIconGraphicsContext = new GC(trayIcon); // TODO: Should this reuse the existing GC used for the SWT GUI?
        trayIconGraphicsContext.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
        trayIconGraphicsContext.fillRectangle(trayIcon.getBounds());
        trayIconGraphicsContext.dispose();
        trayItem.setImage(trayIcon);
        trayItem.setHighlightImage(highlightedTrayIcon);
    }

    private void addListenersForTrayItem(TrayItem trayItem) {
        trayItem.addListener(SWT.Show, onEventPrintTextFrom("show"));
        trayItem.addListener(SWT.Hide, onEventPrintTextFrom("hide"));
        trayItem.addListener(SWT.Selection, onEventPrintTextFrom("selection"));
        trayItem.addListener(SWT.DefaultSelection, onEventPrintTextFrom("default selection"));
        trayItem.addListener(SWT.MenuDetect, new Listener() {
            @Override
            public void handleEvent(Event event) {
                menu.setVisible(true);
            }
        });
    }

    private Listener onEventPrintTextFrom(final String text) {
        return new Listener() {
            @Override public void handleEvent(Event event) {
                System.out.println(text);
            }
        };
    }

    private Menu buildMenuItems() {
        final Menu menu = new Menu(shell, SWT.POP_UP);
        for (int i = 0; i < 8; i++) {
            MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
            menuItem.setText("Item" + i);
            menuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    System.out.println("selection " + event.widget);
                }
            });
            if (i == 0) menu.setDefaultItem(menuItem);
        }
        return menu;
    }

}