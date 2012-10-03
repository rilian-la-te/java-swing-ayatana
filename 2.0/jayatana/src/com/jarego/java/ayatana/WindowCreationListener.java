/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jarego.java.ayatana;

import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.HierarchyEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;

/**
 *
 * @author
 * danjared
 */
class WindowCreationListener implements AWTEventListener {
    @Override
    public void eventDispatched(AWTEvent event) {
        HierarchyEvent hEvent = (HierarchyEvent)event;
        if (hEvent.getID() == HierarchyEvent.HIERARCHY_CHANGED)
            if ((hEvent.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) == HierarchyEvent.PARENT_CHANGED) {
                if ((hEvent.getChanged() instanceof JRootPane &&
                        (hEvent.getChangedParent() instanceof JFrame ||
                            hEvent.getChangedParent() instanceof JDialog)) ||
                        (hEvent.getChanged() instanceof Applet ||
                            hEvent.getChanged() instanceof AppletContext)) {
                    AyatanaInstaller.installStartupWMClass();
                    Toolkit.getDefaultToolkit().removeAWTEventListener(this);
                }
            }
    }
}
