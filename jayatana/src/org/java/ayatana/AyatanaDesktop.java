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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.ImageIcon;

final public class AyatanaDesktop {
	/**
	 * Converite un arreglo de bytes a una cadena hexadecimal
	 * 
	 * @param digest arreglo de bytes
	 * @return cadena hexadecimal
	 */
	private static String toHexadecimal(byte[] digest) {
		String hash = "";
		for (byte aux : digest) {
			int b = aux & 0xff;
			if (Integer.toHexString(b).length() == 1)
				hash += "0";
			hash += Integer.toHexString(b);
		}
		return hash;
	}
	
	/**
	 * Generar un checksum de una entrada
	 * 
	 * @param input flujo de entrada
	 * @return checksum hexadecimal
	 */
	public static String getMD5Checksum(InputStream input) {
		try {
			byte buff[] = new byte[1024];
			int read;
			MessageDigest md = MessageDigest.getInstance("MD5");
			while ((read = input.read(buff)) > 0)
				md.update(buff, 0, read);
			return toHexadecimal(md.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Incida si el sistema es soportado para uso de Unity
	 * 
	 * @return <code>True</code> si es soportado
	 */
	public static boolean isSupported() {
		if (!System.getProperty("os.name").contains("Linux"))
			return false;
		if (!"Unity".equals(System.getenv("XDG_CURRENT_DESKTOP")))
			return false;
		String version = System.getProperty("java.version");
		version = version.substring(0, version.indexOf(".", version.indexOf(".")+1));
		try {
			float iversion = Float.parseFloat(version);
			if (System.getProperty("java.vm.name").contains("OpenJDK") &&
					iversion < 1.7f)
				return false;
			else if (iversion < 1.6f)
				return false;
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	/**
	 * Trata de instalar un icono, en el tema "hicolor".
	 * 
	 * @param name nombre del icono
	 * @param urlIcon url del icono
	 * @return <Code>True</code> si puede instalar el icono
	 */
	public static boolean tryInstallIcon(String name, URL urlIcon) {
		return tryInstallIcon(name, "hicolor", urlIcon);
	}
	/**
	 * Trata de instalar un icono.
	 * 
	 * @param name nombre del icono
	 * @param theme tema del icono
	 * @param urlIcon url del icono
	 * @return 
	 */
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
				iconTargetMD5 = getMD5Checksum(fis);
				fis.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			String iconSourceMD5;
			try {
				InputStream inputSource = urlIcon.openStream();
				iconSourceMD5 = getMD5Checksum(inputSource);
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
				try {
					Runtime.getRuntime().exec("xdg-icon-resource forceupdate");
				} catch (IOException e) {}
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
			try {
				Runtime.getRuntime().exec("xdg-icon-resource forceupdate");
			} catch (IOException e) {
				//ignorar
			}
			return true;
		}
	}
}
