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
import java.util.Properties;

/**
 * Clase para carga de librería libjayatana.so
 * 
 * @author Jared González
 */
final public class AyatanaLibrary {
	public static final String LIB_VERSION = "1.2.0";
	public static final String JNI_VERSION = "1.2.0";
	private static boolean loaded = false;
	private static boolean successful = false;
	
	/**
	 * Obtiene la versión de Ubuntu
	 * @return
	 * @throws IOException 
	 */
	private static String getUbuntuVersion() throws IOException {
		Properties prop = new Properties();
		FileInputStream fis = new FileInputStream("/etc/lsb-release");
		try {
			prop.load(fis);
		} finally {
			fis.close();
		}
		return prop.getProperty("DISTRIB_RELEASE");
	}
	
	/**
	 * Carga la library libjayatana.so.
	 */
	public static boolean load() {
		if (!loaded) {
			try {
				final File targetDirectory = new File(
						System.getProperty("user.home"), ".java/jayatana/"+JNI_VERSION+"/"+
						System.getProperty("os.arch"));
				final File targetLibrary = new File(targetDirectory, "libjayatana.so");
				final String sourceLibrary = "/native/"+getUbuntuVersion()+"/"+
						System.getProperty("os.arch")+"/libjayatana.so";
				
				if (targetLibrary.exists()) {
					FileInputStream fis = new FileInputStream(targetLibrary);
					String chksum = AyatanaDesktop.getMD5Checksum(fis);
					fis.close();
					InputStream input = AyatanaLibrary.class.getResourceAsStream(sourceLibrary);
					if (input == null)
						throw new Exception("not library exists");
					String chksumint = AyatanaDesktop.getMD5Checksum(input);
					input.close();
					
					if (!chksumint.equals(chksum)) {
						targetLibrary.delete();
						input = AyatanaLibrary.class.getResourceAsStream(sourceLibrary);
						if (input == null)
							throw new Exception("not library exists");
						FileOutputStream fos = new FileOutputStream(targetLibrary);
						byte buff[] = new byte[1024];
						int read;
						while ((read = input.read(buff)) > 0)
							fos.write(buff, 0, read);
						fos.flush();
						fos.close();
						input.close();
					}
				} else {
					targetDirectory.mkdirs();
					InputStream input = AyatanaLibrary.class.getResourceAsStream(sourceLibrary);
					if (input == null)
						throw new Exception("not library exists");
					FileOutputStream fos = new FileOutputStream(targetLibrary);
					byte buff[] = new byte[1024];
					int read;
					while ((read = input.read(buff)) > 0)
						fos.write(buff, 0, read);
					fos.flush();
					fos.close();
					input.close();
				}
				try {
					System.loadLibrary("awt");
				} catch (UnsatisfiedLinkError e) {
					if (!e.getMessage().contains("loaded in another classloader"))
						throw e;
				}
				try {
					System.loadLibrary("jawt");
				} catch (UnsatisfiedLinkError e) {
					if (!e.getMessage().contains("loaded in another classloader"))
						throw e;
				}
				System.load(targetLibrary.getCanonicalPath());
				successful = true;
			} catch (Exception e) {
				successful = false;
			}
			loaded = true;
		}
		return successful;
	}
}
