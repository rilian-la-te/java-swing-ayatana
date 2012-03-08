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

import java.io.*;
import java.net.URL;
import javax.swing.ImageIcon;

final public class AyatanaDesktop {
	public static boolean isSupported() {
		if (!System.getProperty("os.name").startsWith("Linux"))
			return false;
		if (!"gnome".equals(System.getProperty("sun.desktop")))
			return false;
		if (!"ubuntu".equals(System.getenv("DESKTOP_SESSION")))
			return false;
		return true;
	}
	public static boolean tryInstallIcon(String name, URL urlIcon) {
		return tryInstallIcon(name, "hicolor", urlIcon);
	}
	public static boolean tryInstallIcon(String name, String theme, URL urlIcon) {
		if (!AyatanaDesktop.isSupported())
			return false;
		
		ImageIcon icon = new ImageIcon(urlIcon);
		if (icon.getIconHeight() != icon.getIconWidth())
			throw new RuntimeException("the icon is not 1:1");
		
		switch (icon.getIconWidth()) {
			case 16:
			case 24:
			case 32:
			case 48:
			case 128:
			case 256:
				break;
			default:
				throw new RuntimeException("invalid size icon, only support "
						+ "16x16 24x24 32x32 48x48 128x128 256x256");
		}
		
		String urlIconName = urlIcon.toString();
		String extensionIconName = urlIconName.substring(urlIconName.lastIndexOf(".")+1);
		
		File iconFile = new File(System.getProperty("user.home"),
				".local/share/icons/"+theme+"/"
				+icon.getIconWidth()+"x"+icon.getIconWidth()
				+"/apps/"+name+"."+extensionIconName);
		
		if (iconFile.exists() && iconFile.isFile()) {
			String iconTargetMD5;
			try {
				FileInputStream fis = new FileInputStream(iconFile);
				iconTargetMD5 = AyatanaLibrary.getMD5Checksum(fis);
				fis.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			String iconSourceMD5;
			try {
				InputStream inputSource = urlIcon.openStream();
				iconSourceMD5 = AyatanaLibrary.getMD5Checksum(inputSource);
				inputSource.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			if (!iconSourceMD5.equals(iconTargetMD5)) {
				iconFile.getParentFile().mkdirs();
				try {
					InputStream input = urlIcon.openStream();
					FileOutputStream fos = new FileOutputStream(iconFile);
					byte buff[] = new byte[1024];
					int read;
					while ((read = input.read(buff)) > 0)
						fos.write(buff, 0, read);
					fos.flush();
					fos.close();
					input.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				
				return true;
			} else {
				return false;
			}
		} else {
			iconFile.getParentFile().mkdirs();
			try {
				InputStream input = urlIcon.openStream();
				FileOutputStream fos = new FileOutputStream(iconFile);
				byte buff[] = new byte[1024];
				int read;
				while ((read = input.read(buff)) > 0)
					fos.write(buff, 0, read);
				fos.flush();
				fos.close();
				input.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			return true;
		}
	}
}
