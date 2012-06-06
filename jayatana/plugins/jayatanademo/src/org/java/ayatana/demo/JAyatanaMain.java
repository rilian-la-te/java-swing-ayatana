package org.java.ayatana.demo;

import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.gtk.laf.extended.GTKLookAndFeelExtended;
import org.java.ayatana.ApplicationMenu;
import org.java.ayatana.AyatanaDesktop;
import org.java.ayatana.DesktopFile;

/**
 * Este es una demostración de la utilización de JAyatana y GTKExtended.
 * 
 * @author Jared González
 */
public class JAyatanaMain extends JFrame {
	/**
	 * Instalar menu global.
	 */
	private void installUnityGlobalMenu() {
		if (AyatanaDesktop.isSupported()) {
			ApplicationMenu.tryInstall(this);
		}
	}
	
	/**
	 * Instalar GTK Extended.
	 */
	private static void installGTKExtended() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			if (UIManager.getLookAndFeel().getClass().getName().equals(
					"com.sun.java.swing.plaf.gtk.GTKLookAndFeel")) {
				GTKLookAndFeelExtended.setCustomFont(new Font("Droid Sans", Font.PLAIN, 14));
				GTKLookAndFeelExtended.applayGTKLookAndFeelExtended();
			}
		} catch (Throwable e) {
			Logger.getLogger(JAyatanaMain.class.getName())
				.log(Level.WARNING, "Error on GTK Look And Feel Extended", e);
		}
	}
	
	/**
	 * Instalar DesktopFile.
	 */
	private static void installDesktopFile() {
		if (AyatanaDesktop.isSupported()) {
			try {
				AyatanaDesktop.tryInstallIcon("javaswing",
						DesktopFile.class.getResource("/org/java/ayatana/demo/javaswingapp.png"));

				DesktopFile df = DesktopFile.initialize(
						"jayatanademo", "jayatanademo");
				df.setName("JAyatana Demo");
				df.setCategories("Application,Development,IDE");
				df.setIcon("javaswing");
				// necesita especificar un comando de ejecución de la aplicación
				df.setCommand("foo");
				df.update();
			} catch (IOException e) {
				Logger.getLogger(JAyatanaMain.class.getName())
					.log(Level.WARNING, "Error on DesktopFile", e);
			}
		}
	}
	
	/**
	 * Eliminar DesktopFile.
	 */
	public static void uninstallDesktopFile() {
		DesktopFile.getInstance().delete();
	}
	
	/**
	 * Ejecución de la aplicación
	 * @param args argumentos de linea de comando
	 */
	public static void main(String args[]) {
		installGTKExtended();
		installDesktopFile();
		JAyatanaMain ayatanamain = new JAyatanaMain();
		ayatanamain.setVisible(true);
	}
	
	/**
	 * Contructor de la ventana
	 */
	public JAyatanaMain() throws HeadlessException {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				uninstallDesktopFile();
				System.exit(0);
			}
		});
		setTitle("JAyatana Demo");
		
		JMenuItem menuAbout = new JMenuItem("About of JAyatana");
		menuAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(JAyatanaMain.this,
						"JAyatana Demo by Jared González",
						"Jayatana Demo", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		JMenu menuHelp = new JMenu("Help");
		menuHelp.add(menuAbout);
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menuHelp);
		setJMenuBar(menuBar);
		
		JMenuItem menuAboutPopup = new JMenuItem("About of JAyatana");
		menuAboutPopup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(JAyatanaMain.this,
						"JAyatana Demo by Jared González",
						"Jayatana Demo", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		JMenuItem menuExitPopup = new JMenuItem("Exit");
		menuExitPopup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JAyatanaMain.this.dispose();
			}
		});
		final JPopupMenu popup = new JPopupMenu();
		popup.add(menuAboutPopup);
		popup.addSeparator();
		popup.add(menuExitPopup);
		
		final JPanel panContent  = new JPanel(new GridBagLayout());
		panContent.add(new JLabel("Press rigth button mouse to show popup menu"));
		panContent.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1 &&
						e.getButton() == MouseEvent.BUTTON3) {
					popup.show(panContent, e.getX(), e.getY());
				}
			}
		});
		setContentPane(panContent);
		
		setSize(400, 230);
		setLocationRelativeTo(null);
		
		installUnityGlobalMenu();
	}
}
