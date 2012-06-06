package org.gtk.laf.extended;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.synth.SynthLookAndFeel;

public class GTKExtendedUI {
	public static ComponentUI createUI(JComponent c) {
		ComponentUI cui = SynthLookAndFeel.createUI(c);
		if (GTKLookAndFeelExtended.getCustomFont() != null)
			c.setFont(GTKLookAndFeelExtended.getCustomFont());
		return cui;
	}
}
