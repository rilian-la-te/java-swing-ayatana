package org.java.ayatana;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class DefaultExtraMenuAction implements ExtraMenuAction {
	@Override
	public boolean allowDynamicMenuBar() {
		return false;
	}
	@Override
	public boolean allowMenuAction(JFrame frame, JMenuBar menubar, JMenuItem menuitem, boolean selected) {
		if (selected) {
			KeyStroke accelerator = menuitem.getAccelerator();
			if (accelerator != null) {
				String acceleratorText = accelerator.toString();
				if (FocusManager.getCurrentManager().getFocusOwner() instanceof JTextComponent) {
					if (acceleratorText.equals("pressed DELETE") ||
							acceleratorText.equals("pressed BACK_SPACE") ||
							acceleratorText.equals("ctrl pressed C") ||
							acceleratorText.equals("ctrl pressed X") ||
							acceleratorText.equals("ctrl pressed Z") ||
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
	public void beforInvokeMenu(JFrame frame, JMenuBar menubar, JMenuItem menuitem, boolean selected) {
		
	}
	@Override
	public void invokeMenu(JFrame frame, JMenuBar menubar, JMenuItem menuitem, boolean selected) {
		
	}
	@Override
	public void afterInvokeMenu(JFrame frame, JMenuBar menubar, JMenuItem menuitem, boolean selected) {
		
	}
}
