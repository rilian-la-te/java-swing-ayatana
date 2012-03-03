package org.java.ayatana;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class ExtraMenuActionDefault implements ExtraMenuAction {
	@Override
	public boolean invokeMenu(JFrame frame, JMenuBar menubar, JMenuItem menuitem, boolean selected) {
		if (selected) {
			KeyStroke accelerator = menuitem.getAccelerator();
			if (accelerator != null) {
				String acceleratorText = accelerator.toString();
				if (FocusManager.getCurrentManager().getFocusOwner() instanceof JTextComponent) {
					if (acceleratorText.equals("pressed DELETE") ||
							acceleratorText.equals("pressed BACK_SPACE") ||
							acceleratorText.equals("ctrl pressed C") ||
							acceleratorText.equals("ctrl pressed X") ||
							acceleratorText.equals("ctrl pressed Z"))
						return false;
				} else if (acceleratorText.equals("alt pressed F4")) {
					return false;
				}
			}
		}
		return true;
	}
}
