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

package org.java.ayatana.demo;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.util.ResourceBundle;
import javax.swing.*;
import org.java.ayatana.Ayatana;

/**
 * Clase de un editor simple de texto
 * 
 * @author Jared González
 */
public class BasicTextEditor extends JFrame implements ActionListener, ItemListener {
	private static final ResourceBundle bundle = ResourceBundle.getBundle(
			"org.java.ayatana.demo.Bundle");
	
	private JMenuBar menubar;
	private File file;
	private JTextArea textarea;
	
	/**
	 * Crear un editor de texto básico
	 * 
	 */
	public BasicTextEditor() {
		this(null);
	}
	
	/**
	 * Crea un editor de texto básico a partir de archivo
	 * 
	 * @param file Archivo de texto
	 */
	public BasicTextEditor(File file) {
		this.file = file;
		
		menubar = new JMenuBar();
		menubar.add(getMenuFile());
		menubar.add(getMenuEdit());
		menubar.add(getMenuHelp());
		
		textarea = new JTextArea();
		textarea.setTabSize(4);
		textarea.setLineWrap(true);
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(getToolBar(), BorderLayout.PAGE_START);
		contentPane.add(new JScrollPane(textarea), BorderLayout.CENTER);
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle(bundle.getString("title"));
		this.setJMenuBar(menubar);
		this.setContentPane(contentPane);
		this.setSize(500, 350);
		
		Ayatana.tryInstallApplicationMenu(this);
	}
	
	/**
	 * Cargar texto predeterminado
	 */
	public void loadDefaultText() {
		if (file == null) {
			try {
				InputStream input = getClass().getResourceAsStream("/org/java/ayatana/demo/default.txt");
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				String line;
				while ((line = reader.readLine()) != null) {
					textarea.append(line + "\n");
				}
				reader.close();
				input.close();
			} catch (IOException e) {}
		}
	}
	
	/**
	 * Crear menu de archivo
	 * 
	 * @return menu
	 */
	private JMenu getMenuFile() {
		JMenuItem menunew = new JMenuItem(bundle.getString("menu_new"));
		menunew.setAccelerator(KeyStroke.getKeyStroke(bundle.getString("menu_new_ac")));
		menunew.setActionCommand("new");
		menunew.addActionListener(this);
		JMenuItem menuopen = new JMenuItem(bundle.getString("menu_open"));
		menuopen.setAccelerator(KeyStroke.getKeyStroke(bundle.getString("menu_open_ac")));
		menuopen.setActionCommand("open");
		menuopen.addActionListener(this);
		JMenuItem menusave = new JMenuItem(bundle.getString("menu_save"));
		menusave.setAccelerator(KeyStroke.getKeyStroke(bundle.getString("menu_save_ac")));
		menusave.setActionCommand("save");
		menusave.addActionListener(this);
		JMenuItem menusaveas = new JMenuItem(bundle.getString("menu_save_as"));
		menusaveas.setAccelerator(KeyStroke.getKeyStroke(bundle.getString("menu_save_as_ac")));
		menusaveas.setActionCommand("saveas");
		menusaveas.addActionListener(this);
		JMenuItem menuclose = new JMenuItem(bundle.getString("menu_close"));
		menuclose.setAccelerator(KeyStroke.getKeyStroke(bundle.getString("menu_close_ac")));
		menuclose.setActionCommand("close");
		menuclose.addActionListener(this);
		JMenuItem menuexit = new JMenuItem(bundle.getString("menu_exit"));
		menuexit.setAccelerator(KeyStroke.getKeyStroke(bundle.getString("menu_exit_ac")));
		menuexit.setActionCommand("exit");
		menuexit.addActionListener(this);
		
		JMenu menufile = new JMenu(bundle.getString("menu_file"));
		menufile.setMnemonic(bundle.getString("menu_file_mn").charAt(0));
		menufile.add(menunew);
		menufile.add(menuopen);
		menufile.add(menusave);
		menufile.add(menusaveas);
		menufile.addSeparator();
		menufile.add(menuclose);
		menufile.add(menuexit);
		
		return menufile;
	}
	
	/**
	 * Crea el menu de edición
	 * 
	 * @return menu
	 */
	private JMenu getMenuEdit() {
		JMenuItem menuundo = new JMenuItem(bundle.getString("menu_undo"));
		menuundo.setAccelerator(KeyStroke.getKeyStroke(bundle.getString("menu_undo_ac")));
		menuundo.setActionCommand("undo");
		menuundo.addActionListener(this);
		JMenuItem menuredo = new JMenuItem(bundle.getString("menu_redo"));
		menuredo.setAccelerator(KeyStroke.getKeyStroke(bundle.getString("menu_redo_ac")));
		menuredo.setActionCommand("redo");
		menuredo.addActionListener(this);
		JMenuItem menucut = new JMenuItem(bundle.getString("menu_cut"));
		menucut.setAccelerator(KeyStroke.getKeyStroke(bundle.getString("menu_cut_ac")));
		menucut.setActionCommand("cut");
		menucut.addActionListener(this);
		JMenuItem menucopy = new JMenuItem(bundle.getString("menu_copy"));
		menucopy.setAccelerator(KeyStroke.getKeyStroke(bundle.getString("menu_copy_ac")));
		menucopy.setActionCommand("copy");
		menucopy.addActionListener(this);
		JMenuItem menupaste = new JMenuItem(bundle.getString("menu_paste"));
		menupaste.setAccelerator(KeyStroke.getKeyStroke(bundle.getString("menu_paste_ac")));
		menupaste.setActionCommand("paste");
		menupaste.addActionListener(this);
		JMenuItem menuselall = new JMenuItem(bundle.getString("menu_selall"));
		menuselall.setAccelerator(KeyStroke.getKeyStroke(bundle.getString("menu_selall_ac")));
		menuselall.setActionCommand("selall");
		menuselall.addActionListener(this);
		
		JMenu menuedit = new JMenu(bundle.getString("menu_edit"));
		menuedit.setMnemonic(bundle.getString("menu_edit_mn").charAt(0));
		menuedit.add(menuundo);
		menuedit.add(menuredo);
		menuedit.addSeparator();
		menuedit.add(menucut);
		menuedit.add(menucopy);
		menuedit.add(menupaste);
		menuedit.addSeparator();
		menuedit.add(menuselall);
		menuedit.addSeparator();
		menuedit.add(getMenuLaF());
		return menuedit;
	}
	
	private JMenu getMenuLaF() {
		JMenu menulaf = new JMenu(bundle.getString("menu_laf"));
		menulaf.setMnemonic(bundle.getString("menu_laf_mn").charAt(0));
		ButtonGroup bg = new ButtonGroup();
		for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			JRadioButtonMenuItem menul = new JRadioButtonMenuItem(info.getName());
			menul.setActionCommand("laf" + info.getClassName());
			menul.addItemListener(this);
			if (info.getClassName().equals(UIManager.getLookAndFeel().getClass().getName()))
				menul.setSelected(true);
			bg.add(menul);
			menulaf.add(menul);
		}
		return menulaf;
	}
	
	/**
	 * Crear el menu de ayuda
	 * 
	 * @return menu
	 */
	private JMenu getMenuHelp() {
		JMenuItem menuAbout = new JMenuItem(bundle.getString("menu_about"));
		menuAbout.setAccelerator(KeyStroke.getKeyStroke(bundle.getString("menu_about_ac")));
		menuAbout.setActionCommand("about");
		menuAbout.addActionListener(this);
		
		JMenu menuhelp = new JMenu(bundle.getString("menu_help"));
		menuhelp.add(menuAbout);
		return menuhelp;
	}
	
	/**
	 * Crea la barra de herramientas
	 * 
	 * @return barra de herramientas
	 */
	private JToolBar getToolBar() {
		JButton btnnew = createToolBarButton("Add16.gif", null);
		btnnew.setActionCommand("new");
		btnnew.addActionListener(this);
		JButton btnopen = createToolBarButton("Open16.gif", null);
		btnopen.setActionCommand("open");
		btnopen.addActionListener(this);
		JButton btnsave = createToolBarButton("Save16.gif", null);
		btnsave.setActionCommand("save");
		btnsave.addActionListener(this);
		
		JToolBar toolbar = new JToolBar();
		toolbar.add(btnnew);
		toolbar.add(btnopen);
		toolbar.add(btnsave);
		
		return toolbar;
	}
	
	/**
	 * Crea un boton para la barra de herramientas
	 * 
	 * @param icon nombre de archivo de icono
	 * @param tooltip mensaje del boton
	 * @return 
	 */
	private JButton createToolBarButton(String icon, String tooltip) {
		JButton btn = new JButton(new ImageIcon(
				getClass().getResource("/org/java/ayatana/demo/icons/"+icon)));
		btn.setFocusable(false);
		btn.setOpaque(false);
		return btn;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("new".equals(e.getActionCommand())) {
			BasicTextEditor textEditor = new BasicTextEditor();
			textEditor.setVisible(true);
		} else if ("open".equals(e.getActionCommand())) {
			JFileChooser fc = new JFileChooser(System.getProperty("user.home"));
			fc.showOpenDialog(this);
			File f = fc.getSelectedFile();
			if (f != null) {
				try {
					textarea.setText("");
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(new FileInputStream(f)));
					String line;
					while ((line = reader.readLine()) != null)
						textarea.append(line + "\n");
					reader.close();
				} catch (IOException err) {}
			}
		} else if ("close".equals(e.getActionCommand())) {
			this.dispose();
		} else if ("exit".equals(e.getActionCommand())) {
			for (Frame frame : Frame.getFrames())
				frame.dispose();
		}
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() instanceof AbstractButton) {
			AbstractButton ab = (AbstractButton)e.getSource();
			if (ab.getActionCommand() != null && ab.getActionCommand().startsWith("laf")) {
				try {
					UIManager.setLookAndFeel(ab.getActionCommand().substring(3));
					SwingUtilities.updateComponentTreeUI(this);
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
		}
	}
}
