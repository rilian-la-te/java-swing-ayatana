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

import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 * Clase predeterminada para control de acciones del menu global
 * 
 * @author Jared González
 */
public class DefaultExtraMenuAction implements ExtraMenuAction {
	@Override
	public boolean allowDynamicMenuBar() {
		return false;
	}
	@Override
	public boolean allowMenuAction(JFrame frame, JMenuBar menubar, JMenuItem menuitem, boolean selected, boolean shortcut) {
		if (selected && shortcut) {
			KeyStroke accelerator = menuitem.getAccelerator();
			if (accelerator != null) {
				String acceleratorText = accelerator.toString();
				if (FocusManager.getCurrentManager().getFocusOwner() instanceof JTextComponent) {
					if (acceleratorText.equals("pressed DELETE") ||
							acceleratorText.equals("pressed BACK_SPACE") ||
							acceleratorText.equals("ctrl pressed C") ||
							acceleratorText.equals("ctrl pressed X") ||
							acceleratorText.equals("ctrl pressed V"))
						return false;
				} else if (acceleratorText.equals("alt pressed F4")) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public void beforInvokeMenu(JFrame frame, JMenuBar menubar, JMenuItem menuitem, boolean selected, boolean shortcut) {
		
	}
	@Override
	public void invokeMenu(JFrame frame, JMenuBar menubar, JMenuItem menuitem, boolean selected, boolean shortcut) {
		
	}
	@Override
	public void afterInvokeMenu(JFrame frame, JMenuBar menubar, JMenuItem menuitem, boolean selected, boolean shortcut) {
		
	}
}
