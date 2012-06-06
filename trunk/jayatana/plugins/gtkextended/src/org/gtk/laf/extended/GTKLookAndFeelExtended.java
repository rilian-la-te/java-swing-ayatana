package org.gtk.laf.extended;

import com.sun.java.swing.plaf.gtk.GTKLookAndFeel;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.BorderFactory;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthStyleFactory;

public class GTKLookAndFeelExtended extends GTKLookAndFeel implements
		PropertyChangeListener {
	private static Font customFont = null;
	
	@Override
	public String getID() {
		return "GTK extended";
	}
	@Override
	public String getName() {
		return "GTK look and feel extended";
	}
	@Override
	public String getDescription() {
		return "GTK look and feel extended";
	}

	public static Font getCustomFont() {
		return customFont;
	}
	public static void setCustomFont(Font customFont) {
		GTKLookAndFeelExtended.customFont = customFont;
	}

	@Override
	public void initialize() {
		super.initialize();
		applayGTKLookAndFeelExtendImp();
		UIManager.addPropertyChangeListener(this);
	}
	@Override
	public void uninitialize() {
		UIManager.removePropertyChangeListener(this);
		unapplyGTKLookAndFeelExtended();
		super.uninitialize();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("lookAndFeel".equals(evt.getPropertyName())) {
			installGTKCustomFontAditionals();
		}
	}
	
	public static void applayGTKLookAndFeelExtended() {
		installGTKMenuBorders();
		loadDefaultFont();
		installGTKCustomFonts();
		installGTKCustomFontAditionals();
	}
	private static void applayGTKLookAndFeelExtendImp() {
		installGTKMenuBorders();
		loadDefaultFont();
		installGTKCustomFonts();
	}
	public static void unapplyGTKLookAndFeelExtended() {
		try {
			LookAndFeel laf = UIManager.getLookAndFeel();
			Class<?> lafClass = laf.getClass();
			if (lafClass.getName().equals(GTKLookAndFeelExtended.class.getName())) {
				Method m = lafClass.getSuperclass().getDeclaredMethod(
						"getGTKStyleFactory", new Class<?>[] {});
				m.setAccessible(true);
				setStyleFactory((SynthStyleFactory)m.invoke(laf, new Object[] {}));
				m.setAccessible(false);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void installGTKMenuBorders() {
		if (!"false".equals(System.getProperty("laf.extended.gtkmenuborder")))
			try {
				changeGtkYThikcness(getStyle(null, Region.POPUP_MENU), 1);
				changeGtkXThikcness(getStyle(null, Region.POPUP_MENU), 1);
				changeGtkYThikcness(getStyle(null, Region.POPUP_MENU_SEPARATOR), 1);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
	}
	
	private static void installGTKCustomFonts() {
		if (getCustomFont() != null) {
			try {
				changeGtkFont(getStyle(null, Region.ARROW_BUTTON), getCustomFont());
				changeGtkFont(getStyle(null, Region.BUTTON), getCustomFont());
				changeGtkFont(getStyle(null, Region.CHECK_BOX), getCustomFont());
				changeGtkFont(getStyle(null, Region.CHECK_BOX_MENU_ITEM), getCustomFont());
				changeGtkFont(getStyle(null, Region.COLOR_CHOOSER), getCustomFont());
				changeGtkFont(getStyle(null, Region.COMBO_BOX), getCustomFont());
				changeGtkFont(getStyle(null, Region.DESKTOP_ICON), getCustomFont());
				changeGtkFont(getStyle(null, Region.DESKTOP_PANE), getCustomFont());
				changeGtkFont(getStyle(null, Region.EDITOR_PANE), getCustomFont());
				changeGtkFont(getStyle(null, Region.FILE_CHOOSER), getCustomFont());
				changeGtkFont(getStyle(null, Region.FORMATTED_TEXT_FIELD), getCustomFont());
				changeGtkFont(getStyle(null, Region.INTERNAL_FRAME), getCustomFont());
				changeGtkFont(getStyle(null, Region.INTERNAL_FRAME_TITLE_PANE), getCustomFont());
				changeGtkFont(getStyle(null, Region.LABEL), getCustomFont());
				changeGtkFont(getStyle(null, Region.LIST), getCustomFont());
				changeGtkFont(getStyle(null, Region.MENU), getCustomFont());
				changeGtkFont(getStyle(null, Region.MENU_BAR), getCustomFont());
				changeGtkFont(getStyle(null, Region.MENU_ITEM), getCustomFont());
				changeGtkFont(getStyle(null, Region.MENU_ITEM_ACCELERATOR), getCustomFont());
				changeGtkFont(getStyle(null, Region.OPTION_PANE), getCustomFont());
				changeGtkFont(getStyle(null, Region.PANEL), getCustomFont());
				changeGtkFont(getStyle(null, Region.PASSWORD_FIELD), getCustomFont());
				changeGtkFont(getStyle(null, Region.POPUP_MENU), getCustomFont());
				changeGtkFont(getStyle(null, Region.POPUP_MENU_SEPARATOR), getCustomFont());
				changeGtkFont(getStyle(null, Region.PROGRESS_BAR), getCustomFont());
				changeGtkFont(getStyle(null, Region.RADIO_BUTTON), getCustomFont());
				changeGtkFont(getStyle(null, Region.RADIO_BUTTON_MENU_ITEM), getCustomFont());
				changeGtkFont(getStyle(null, Region.ROOT_PANE), getCustomFont());
				changeGtkFont(getStyle(null, Region.SCROLL_BAR), getCustomFont());
				changeGtkFont(getStyle(null, Region.SCROLL_BAR_THUMB), getCustomFont());
				changeGtkFont(getStyle(null, Region.SCROLL_BAR_TRACK), getCustomFont());
				changeGtkFont(getStyle(null, Region.SCROLL_PANE), getCustomFont());
				changeGtkFont(getStyle(null, Region.SEPARATOR), getCustomFont());
				changeGtkFont(getStyle(null, Region.SLIDER), getCustomFont());
				changeGtkFont(getStyle(null, Region.SLIDER_THUMB), getCustomFont());
				changeGtkFont(getStyle(null, Region.SLIDER_TRACK), getCustomFont());
				changeGtkFont(getStyle(null, Region.SPINNER), getCustomFont());
				changeGtkFont(getStyle(null, Region.SPLIT_PANE), getCustomFont());
				changeGtkFont(getStyle(null, Region.SPLIT_PANE_DIVIDER), getCustomFont());
				changeGtkFont(getStyle(null, Region.TABBED_PANE), getCustomFont());
				changeGtkFont(getStyle(null, Region.TABBED_PANE_CONTENT), getCustomFont());
				changeGtkFont(getStyle(null, Region.TABBED_PANE_TAB), getCustomFont());
				changeGtkFont(getStyle(null, Region.TABBED_PANE_TAB_AREA), getCustomFont());
				changeGtkFont(getStyle(null, Region.TABLE), getCustomFont());
				changeGtkFont(getStyle(null, Region.TABLE_HEADER), getCustomFont());
				changeGtkFont(getStyle(null, Region.TEXT_AREA), getCustomFont());
				changeGtkFont(getStyle(null, Region.TEXT_FIELD), getCustomFont());
				changeGtkFont(getStyle(null, Region.TEXT_PANE), getCustomFont());
				changeGtkFont(getStyle(null, Region.TOGGLE_BUTTON), getCustomFont());
				changeGtkFont(getStyle(null, Region.TOOL_BAR), getCustomFont());
				changeGtkFont(getStyle(null, Region.TOOL_BAR_CONTENT), getCustomFont());
				changeGtkFont(getStyle(null, Region.TOOL_BAR_DRAG_WINDOW), getCustomFont());
				changeGtkFont(getStyle(null, Region.TOOL_BAR_SEPARATOR), getCustomFont());
				changeGtkFont(getStyle(null, Region.TOOL_TIP), getCustomFont());
				changeGtkFont(getStyle(null, Region.TREE), getCustomFont());
				changeGtkFont(getStyle(null, Region.TREE_CELL), getCustomFont());
				changeGtkFont(getStyle(null, Region.VIEWPORT), getCustomFont());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private static void installGTKCustomFontAditionals() {
		if (getCustomFont() != null) {
			try {
				BorderFactory.createTitledBorder("");
				UIManager.put("ButtonUI", GTKExtendedUI.class.getName());
				UIManager.put("ToggleButtonUI", GTKExtendedUI.class.getName());
				UIManager.put("CheckBoxUI", GTKExtendedUI.class.getName());
				UIManager.put("RadioButtonUI", GTKExtendedUI.class.getName());
				UIManager.put("TitledBorder.font", getCustomFont());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	private static void loadDefaultFont() {
		if (System.getProperty("laf.extended.gtkfont") != null && customFont == null) {
			try {
				String param[] = System.getProperty("laf.extended.gtkfont").split(":");
				customFont = new Font(
						param[0],
						("BOLD".equals(param[1]) ? Font.BOLD : Font.PLAIN),
						Integer.parseInt(param[2]));
			} catch (Exception e) {
				customFont = null;
			}
		}
	}
	
	private static void changeGtkYThikcness(Object style, int border)
			throws Exception {
		Field field = style.getClass().getDeclaredField("yThickness");
		field.setAccessible(true);
		field.setInt(style, Math.max(border, field.getInt(style)));
		field.setAccessible(false);
	}
	private static void changeGtkXThikcness(Object style, int border)
			throws Exception {
		Field field = style.getClass().getDeclaredField("xThickness");
		field.setAccessible(true);
		field.setInt(style, Math.max(border, field.getInt(style)));
		field.setAccessible(false);
	}
	private static void changeGtkFont(Object style, Font newFont)
			throws Exception {
		Field fieldFont = style.getClass().getDeclaredField("font");
		fieldFont.setAccessible(true);
		fieldFont.set(style, newFont);
		fieldFont.setAccessible(false);
	}
}
