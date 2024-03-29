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

package org.java.ayatana;

import java.awt.Window;
import javax.swing.*;

/**
 * Clase predeterminada para control de acciones del menu global
 * 
 * @author Jared González
 */
public class DefaultExtraMenuAction implements ExtraMenuAction {
	protected String acceleratorText;
	
	@Override
	public boolean allowDynamicMenuBar() {
		if (System.getProperties().containsKey("jayatana.dynamicMenuBar"))
			return "true".equals(System.getProperty("jayatana.dynamicMenuBar"));
		else
			return true;
	}
	@Override
	public boolean allowMenuAction(Window window, JMenuBar menubar, JMenuItem menuitem, boolean selected, boolean shortcut) {
		if (shortcut) {
			KeyStroke accelerator = menuitem.getAccelerator();
			if (accelerator != null) {
				acceleratorText = accelerator.toString();
				if (FocusManager.getCurrentManager().getFocusOwner() instanceof JComponent) {
					JComponent jcomp = (JComponent)FocusManager.getCurrentManager().getFocusOwner();
					if (jcomp.getActionForKeyStroke(accelerator) != null ||
							ApplicationMenu.getWindowRootPane(window).getActionForKeyStroke(accelerator) != null)
						return false;
				}
			} else {
				acceleratorText = null;
			}
		}
		return true;
	}
	
	@Override
	public void beforInvokeMenu(Window window, JMenuBar menubar, JMenuItem menuitem, boolean selected, boolean shortcut) {
		
	}
	@Override
	public void invokeMenu(Window window, JMenuBar menubar, JMenuItem menuitem, boolean selected, boolean shortcut) {
		
	}
	@Override
	public void afterInvokeMenu(Window window, JMenuBar menubar, JMenuItem menuitem, boolean selected, boolean shortcut) {
		
	}
}
