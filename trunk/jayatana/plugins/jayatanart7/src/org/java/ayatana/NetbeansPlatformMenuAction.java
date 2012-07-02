package org.java.ayatana;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class NetbeansPlatformMenuAction extends DefaultExtraMenuAction {
	private boolean initializeLazyMenu = false;
	private Method methodDoInitialize;
	private Method methodDynaModel;
	private Field fieldDynaModel;
	
	private boolean initializeDynamicMenu = false;
	private Method methodSynchMenuPresenters;
	
	private void initializeLazyMenu(Class<?> classMenu) throws NoSuchMethodException, NoSuchFieldException {
		methodDoInitialize = classMenu.getDeclaredMethod("doInitialize", new Class<?>[] {});
		if (!methodDoInitialize.isAccessible())
			methodDoInitialize.setAccessible(true);

		fieldDynaModel = classMenu.getDeclaredField("dynaModel");
		if (!fieldDynaModel.isAccessible())
			fieldDynaModel.setAccessible(true);

		Class<?> classDynaModel = fieldDynaModel.getType();
		methodDynaModel = classDynaModel.getMethod("checkSubmenu", new Class<?>[] {JMenu.class});
		if (!methodDynaModel.isAccessible())
			methodDynaModel.setAccessible(true);
	}
	
	private void initializeDynamicMenu(Class<?> classMenu) throws NoSuchMethodException {
		methodSynchMenuPresenters = classMenu.getDeclaredMethod("synchMenuPresenters", JComponent[].class);
	}
	
	@Override
	public boolean allowMenuAction(JFrame frame, JMenuBar menubar, JMenuItem menuitem, boolean selected, boolean shortcut) {
		if (shortcut)
			return false;
		else
			return super.allowMenuAction(frame, menubar, menuitem, selected, shortcut);
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
	public void invokeMenu(JFrame frame, JMenuBar menubar, JMenuItem menuitem, boolean selected, boolean shortcut) {
		super.invokeMenu(frame, menubar, menuitem, selected, shortcut);
		if (selected) {
			if ("org.openide.awt.MenuBar$LazyMenu".equals(menuitem.getClass().getName())) {
				try {
					if (!initializeLazyMenu) {
						initializeLazyMenu(menuitem.getClass());
						initializeLazyMenu = true;
					}
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
					if (!initializeDynamicMenu) {
						initializeDynamicMenu(menuitem.getClass());
						initializeDynamicMenu = true;
					}
					methodSynchMenuPresenters.invoke(menuitem, new Object[] {null});
				} catch (Exception e) {
					Logger.getLogger(NetbeansPlatformMenuAction.class.getName())
							.log(Level.WARNING, "Error invoking DynamicMenuContent", e);
				}
			}
		}
	}
}
