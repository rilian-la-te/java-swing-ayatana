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
@Override
	public void restored() {
		if (UIManager.getLookAndFeel().getClass().getName().equals(
				"com.sun.java.swing.plaf.gtk.GTKLookAndFeel")) {
			GTKLookAndFeelExtended.applayGTKLookAndFeelExtended();
		}
		
		if (AyatanaDesktop.isSupported()) {
			try {
				String productVersion = System.getProperty(
						"netbeans.productversion", "Netbeans IDE 7.0");
				String desktopFileName = "netbeans-"+productVersion.split(" +")[2];
				AyatanaDesktop.tryInstallIcon("netbeans",
						Installer.class.getResource(
							"/org/nbs/java/ayatana/netbeans.png"));
				final DesktopFile desktopFile =
						DesktopFile.initialize(desktopFileName, desktopFileName);
				desktopFile.setIcon("netbeans");
				desktopFile.update();
			} catch (IOException e) {
				Logger.getLogger(Installer.class.getName())
						.log(Level.WARNING, "Can't install desktop file", e);
			}
		}
		
		WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
			@Override
			public void run() {
				if (AyatanaDesktop.isSupported()) {
					JFrame frame = (JFrame)WindowManager.getDefault().getMainWindow();
					ApplicationMenu.tryInstall(frame, new NbsExtraMenuAction());
					
					/*final LauncherAdapter launcherAdapter = new LauncherAdapter();
					ProgressHandle handle = ProgressHandleFactory.createHandle("JAyatana");
					handle.start(100);
					handle.progress(50);
					Controller controller = Controller.getDefault();
					Component component = controller.getVisualComponent();
					if (component instanceof JPanel) {
						JPanel panel = (JPanel)component;
						panel.addContainerListener(new ContainerListener() {
							@Override
							public void componentAdded(ContainerEvent e) {
								if(e.getChild() instanceof JProgressBar) {
									JProgressBar progress = (JProgressBar)e.getChild();
									progress.addChangeListener(launcherAdapter);
									progress.addPropertyChangeListener(launcherAdapter);
									Launcher.getInstance().setProgressVisible(true);
								}
							}
							@Override
							public void componentRemoved(ContainerEvent e) {
								if(e.getChild() instanceof JProgressBar) {
									Launcher.getInstance().setProgressVisible(false);
									JProgressBar progress = (JProgressBar)e.getChild();
									progress.removePropertyChangeListener(launcherAdapter);
									progress.removeChangeListener(launcherAdapter);
								}
							}
						});
					}
					handle.finish();*/
				}
			}
		});
	}
}
