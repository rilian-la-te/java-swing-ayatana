/*
 * Copyright (c) 2012 Jared González
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
package org.nbs.java.ayatana;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.gtk.laf.extended.GTKLookAndFeelExtended;
import org.java.ayatana.ApplicationMenu;
import org.java.ayatana.AyatanaDesktop;
import org.java.ayatana.DesktopFile;
import org.openide.modules.ModuleInstall;
import org.openide.windows.WindowManager;

/**
 * Clase para instalar la extension
 *
 * @author Jared González
 */
public class Installer extends ModuleInstall {
	static {
		System.setProperty("jayatana.ignoreEndorsed", "true");
	}
	
	@Override
	public void restored() {
		if (UIManager.getLookAndFeel().getClass().getName().equals(
				"com.sun.java.swing.plaf.gtk.GTKLookAndFeel")) {
			try {
				GTKLookAndFeelExtended.applayGTKLookAndFeelExtended();
			} catch (Throwable e) {
				Logger.getLogger(Installer.class.getName())
						.log(Level.INFO, "Error on GTK Look And Feel Extended", e);
			}
		}
		
		if (!"false".equals(System.getProperty("netbeans.jayatana.desktopfile"))) {
			if (AyatanaDesktop.isSupported()) {
				try {
					String desktopFileId;
					if ((desktopFileId = System.getProperty("netbeans.jayatana.desktopfile.id")) == null) {
						String productVersion = System.getProperty(
							"netbeans.productversion", "Netbeans IDE 7.0");
						desktopFileId = "netbeans-"+productVersion.split(" +")[2];
					}
					String iconName;
					if ((iconName = System.getProperty("netbeans.jayatana.desktopfile.icon")) == null) {
						AyatanaDesktop.tryInstallIcon("netbeans",
								Installer.class.getResource(
									"/org/nbs/java/ayatana/netbeans.png"));
						iconName = "netbeans";
					}
					if (!new File(System.getProperty("user.home"),
							".local/share/applications/" + desktopFileId + ".desktop").exists())
						desktopFileId = "netbeans";
					final DesktopFile desktopFile = DesktopFile
							.initialize(desktopFileId, desktopFileId);
					desktopFile.setIcon(iconName);
					if (System.getProperty("netbeans.jayatana.desktopfile.name") != null)
						desktopFile.setName(System.getProperty("netbeans.jayatana.desktopfile.name"));
					if (System.getProperty("netbeans.jayatana.desktopfile.comment") != null)
						desktopFile.setComment(System.getProperty("netbeans.jayatana.desktopfile.comment"));
					if (System.getProperty("netbeans.jayatana.desktopfile.categories") != null)
						desktopFile.setCategories(System.getProperty("netbeans.jayatana.desktopfile.categories").split(","));
					if (System.getProperty("netbeans.jayatana.desktopfile.command") != null)
						desktopFile.setCommand(System.getProperty("netbeans.jayatana.desktopfile.command"));
					desktopFile.update();
					
					EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
							JFrame frame = (JFrame)WindowManager.getDefault().getMainWindow();
							frame.addPropertyChangeListener("iconImage", new PropertyChangeListener() {
								@Override
								public void propertyChange(PropertyChangeEvent evt) {
									if (AyatanaDesktop.isSupported()) {
										DesktopFile.setStartupWMClassToToolKit();
									}
								}
							});
						}
					});
				} catch (IOException e) {
					Logger.getLogger(Installer.class.getName())
							.log(Level.WARNING, "Can't install desktop file", e);
				}
			}
		}
		
		WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
			@Override
			public void run() {
				if (AyatanaDesktop.isSupported()) {
					JFrame frame = (JFrame)WindowManager.getDefault().getMainWindow();
					ApplicationMenu.tryInstall(frame, new NbsExtraMenuAction());
				}
			}
		});
	}
}
