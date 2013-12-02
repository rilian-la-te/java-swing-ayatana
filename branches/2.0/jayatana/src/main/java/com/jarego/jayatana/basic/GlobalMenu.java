/*
 * Copyright (c) 2013 Jared González
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
package com.jarego.jayatana.basic;

import java.awt.Window;

public abstract class GlobalMenu {
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() { GlobalMenu.uninitialize(); }
		});
		GlobalMenu.initialize();
	}
	
	native public static void initialize();
	native public static void uninitialize();
	
	native public static long getWindowXID(Window window);
	native public static void setWindowXID(long windowXID);
	
	native public void registerWatcher(long windowXID);
	native public void unregisterWatcher(long windowXID);
	
	abstract protected void register();
	abstract protected void unregister();
	
	native public void addMenu(long windowXID, int menuId, String label, boolean enabled);
	native public void addMenuItem(long windowXID, int menuId, String label, boolean enabled, int modifiers, int keycode);
	native public void addMenuItemRadio(long windowXID, int menuId, String label, boolean enabled, int modifiers, int keycode, boolean selected);
	native public void addMenuItemCheck(long windowXID, int menuId, String label, boolean enabled, int modifiers, int keycode, boolean selected);
	native public void addSeparator(long windowXID);
	native public void removeAllMenus(long windowXID);
	
	abstract protected void menuActivated(int menuId);
	abstract protected void menuAboutToShow(int menuId);
	abstract protected void menuAfterClose(int menuId);
}