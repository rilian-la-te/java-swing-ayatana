/*
 * Copyright (c) 2012 Jared González.
 *
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.jarego.java.ayatana;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.lang.reflect.Field;
import javax.swing.UIManager;

/**
 *
 * @author
 * Jared
 * González
 */
public final class AyatanaInstaller {
    public static final String PROPERTY_DESKTOP_FILE = "jayatana.desktopFile";
    public static final String PROPERTY_STARTWMCLASS = "jayatana.startupWMClass";
    public static final String ENVVAR_DESKTOP_FILE = "JAYATANA_DESKTOPFILE";
    public static final String ENVVAR_STARTUPWMCLASS = "JAYATANA_STARTUPWMCLASS";
    
    private static String desktopFile;
    private static String startupWMClass;
    private static boolean installed = false;

    private static WindowCreationListener windowCreationListener;
    private static WindowOpenedListener windowOpenedListener;
    private static LookAndFeelChangeListener lookAndFeelChangeListener;
    
    public static void install() {
        if (!installed) {
            windowCreationListener = new WindowCreationListener();
            windowOpenedListener = new WindowOpenedListener();
            lookAndFeelChangeListener = new LookAndFeelChangeListener();
            Toolkit.getDefaultToolkit().addAWTEventListener(windowCreationListener, AWTEvent.HIERARCHY_EVENT_MASK);
            Toolkit.getDefaultToolkit().addAWTEventListener(windowOpenedListener, AWTEvent.WINDOW_EVENT_MASK);
            UIManager.addPropertyChangeListener(lookAndFeelChangeListener);
            installed = true;
        }
    }
    public static boolean isInstalled() {
        return installed;
    }
    static void uninstall() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(windowCreationListener);
        Toolkit.getDefaultToolkit().removeAWTEventListener(windowOpenedListener);
        UIManager.removePropertyChangeListener(lookAndFeelChangeListener);
    }

    public static void setDesktopFile(String desktopFile) {
        AyatanaInstaller.desktopFile = desktopFile;
    }
    public static String getDesktopFile() {
        if (desktopFile != null)
            return desktopFile;
        if (System.getProperty(PROPERTY_DESKTOP_FILE) != null)
            return System.getProperty(PROPERTY_DESKTOP_FILE);
        if (System.getenv(ENVVAR_DESKTOP_FILE) != null)
            return System.getenv(ENVVAR_DESKTOP_FILE);
        return null;
    }

    public static void setStartupWMClass(String startupWMClass) {
        AyatanaInstaller.startupWMClass = startupWMClass;
    }
    public static String getStartupWMClass() {
        if (startupWMClass != null)
            return startupWMClass;
        if (System.getProperty(PROPERTY_STARTWMCLASS) != null)
            return System.getProperty(PROPERTY_STARTWMCLASS);
        if (System.getenv(ENVVAR_STARTUPWMCLASS) != null)
            return System.getenv(ENVVAR_STARTUPWMCLASS);
        return "java-application";
    }
    
    static void installStartupWMClass() {
        try {
            System.setProperty("java.awt.WM_CLASS", getStartupWMClass());
            Toolkit xToolkit = Toolkit.getDefaultToolkit();
            Field awtAppClassNameField = xToolkit.getClass().getDeclaredField("awtAppClassName");
            awtAppClassNameField.setAccessible(true);
            awtAppClassNameField.set(xToolkit, getStartupWMClass());
            awtAppClassNameField.setAccessible(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
