package jdev.java.ayatana;

import java.awt.EventQueue;

import java.io.IOException;

import javax.swing.JFrame;

import oracle.ide.Addin;
import oracle.ide.Ide;

import org.java.ayatana.ApplicationMenu;
import org.java.ayatana.AyatanaDesktop;
import org.java.ayatana.DefaultExtraMenuAction;
import org.java.ayatana.DesktopFile;

public class JAyatanaAddin implements Addin {
	static {
		System.setProperty("jayatana.ignoreEndorsed", "true");
	}
	
    public void initialize() {
        if (AyatanaDesktop.isSupported()) {
            try {
                if ("oracle.sqldeveloper".equals(System.getProperty("ide.product"))) {
                    final String IDPRODUCT = "orasqldeveloper";
                    AyatanaDesktop.tryInstallIcon(IDPRODUCT, JAyatanaAddin.class
                        .getResource("/jdev/java/ayatana/sqldeveloper.png"));
                    final DesktopFile desktopFile =
                        DesktopFile.initialize(IDPRODUCT, IDPRODUCT);
                    desktopFile.setName("SQL Developer");
                    desktopFile.setComment("Oracle SQL Developer");
                    desktopFile.setCategories("Ide", "Development");
                    desktopFile.setCommand("bash "+System.getProperty("oracle.home")+"/sqldeveloper.sh");
                    desktopFile.setIcon(IDPRODUCT);
                    desktopFile.update();
                } else if ("oracle.jdeveloper".equals(System.getProperty("ide.product"))) {
                    String IDPRODUCTVERSION = System.getProperty("ide.build");
                    int begin = IDPRODUCTVERSION.indexOf("_");
                    int end = IDPRODUCTVERSION.indexOf("_", begin + 1);
                    IDPRODUCTVERSION = IDPRODUCTVERSION.substring(begin + 1, end);
                    final String IDPRODUCT = "orajdeveloper";
                    AyatanaDesktop.tryInstallIcon(IDPRODUCT, JAyatanaAddin.class
                        .getResource("/jdev/java/ayatana/jdeveloper.png"));
                    final DesktopFile desktopFile = DesktopFile.initialize(
                        IDPRODUCT + "-" +IDPRODUCTVERSION, IDPRODUCT + "-" +IDPRODUCTVERSION);
                    desktopFile.setName("JDeveloper " + IDPRODUCTVERSION);
                    desktopFile.setComment("Oracle JDeveloper "+IDPRODUCTVERSION);
                    desktopFile.setCategories("Ide", "Development");
                    desktopFile.setCommand("bash "+System.getProperty("oracle.home")+"/jdev/bin/jdev");
                    desktopFile.setIcon(IDPRODUCT);
                    desktopFile.update();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ApplicationMenu.tryInstall((JFrame)Ide.getMainWindow());
                }
            });
        }
    }
}
