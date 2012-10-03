/*
 * Copyright (c) 2012 Jared González.
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
package com.jarego.java.ayatana.demo;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * 
 * @author
 * Jared González
 */
public class DemoMain {
    static {
        // java ayatana properties
        System.setProperty("jayatana.startupWMClass", "jayatana-demo");
        // aditiona properties
        System.setProperty("swing.aatext", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.defaultlaf", UIManager.getSystemLookAndFeelClassName());
    }
    
    public static void main(String args[]) {
        JFrame jframe = new JFrame();
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setTitle("Java Ayatana Demo");
        jframe.add(new JButton("Button"));
        jframe.setSize(400, 280);
        jframe.setLocationRelativeTo(null);
        jframe.setVisible(true);
    }
}
