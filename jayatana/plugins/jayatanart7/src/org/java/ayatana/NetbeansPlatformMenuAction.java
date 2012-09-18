package org.java.ayatana;

import java.awt.Window;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class NetbeansPlatformMenuAction extends DefaultExtraMenuAction {
	@Override
	public boolean allowDynamicMenuBar() {
		return false;
	}
	
	@Override
	public boolean allowMenuAction(Window window, JMenuBar menubar, JMenuItem menuitem, boolean selected, boolean shortcut) {
		if (shortcut)
			return false;
		else
			return super.allowMenuAction(window, menubar, menuitem, selected, shortcut);
	}
	
	private boolean instanceOf(Class<?> cls, String clsName) {
		if (cls.getName().equals(Object.class.getName()))
			return false;
		if (cls.getName().equals(clsName))
			return true;
		for (Class<?> c : cls.getInterfaces())
			if (c.getName().equals(clsName))
				return true;
		return instanceOf(cls.getSuperclass(), clsName);
	}
	
	@Override
	@SuppressWarnings("UseSpecificCatch")
	public void invokeMenu(Window window, JMenuBar menubar, JMenuItem menuitem, boolean selected, boolean shortcut) {
		super.invokeMenu(window, menubar, menuitem, selected, shortcut);
		if (selected) {
			if ("org.openide.awt.MenuBar$LazyMenu".equals(menuitem.getClass().getName())) {
				try {
					Method methodDoInitialize = menuitem.getClass().getDeclaredMethod(
								   "doInitialize", new Class<?>[] {});
					if (!methodDoInitialize.isAccessible())
						methodDoInitialize.setAccessible(true);
					
					Field fieldDynaModel = menuitem.getClass().getDeclaredField("dynaModel");
					if (!fieldDynaModel.isAccessible())
						fieldDynaModel.setAccessible(true);
					
					Class<?> classDynaModel = fieldDynaModel.getType();
					Method methodDynaModel = classDynaModel.getMethod(
								   "checkSubmenu", new Class<?>[] {JMenu.class});
					if (!methodDynaModel.isAccessible())
						methodDynaModel.setAccessible(true);
					
					methodDoInitialize.invoke(menuitem, new Object[] {});
					Object objectDynaModel = fieldDynaModel.get(menuitem);
					methodDynaModel.invoke(objectDynaModel, menuitem);
				} catch (Exception e) {
					Logger.getLogger(NetbeansPlatformMenuAction.class.getName())
							.log(Level.WARNING, "Error invoking LazyMenu", e);
				}
			}
			
			if (instanceOf(menuitem.getClass(), "org.openide.awt.DynamicMenuContent")) {
				try {
					Method methodSynchMenuPresenters = menuitem.getClass()
							.getDeclaredMethod("synchMenuPresenters", JComponent[].class);
					if (!methodSynchMenuPresenters.isAccessible())
						methodSynchMenuPresenters.setAccessible(true);
					methodSynchMenuPresenters.invoke(menuitem, new Object[] {null});
				} catch (Exception e) {
					Logger.getLogger(NetbeansPlatformMenuAction.class.getName())
							.log(Level.WARNING, "Error invoking DynamicMenuContent", e);
				}
			}
		}
	}
}
