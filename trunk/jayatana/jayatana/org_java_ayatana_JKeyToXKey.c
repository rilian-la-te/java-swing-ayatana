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
 * 
 * File:   org_java_ayatana_JavaKeycodeToDbusmenu.c
 * Author: Jared González
 */
#include "org_java_ayatana_JKeyToXKey.h"
#include <X11/Xlib.h>
#include <X11/keysym.h>
#include <glib.h>

int jkeycode_to_xkey_map(int keycode) {
	switch (keycode) {
		default:
			return 0;
	}
}

char *jkeycode_to_xkey(int keycode) {
	int code = 0;
	
	if (keycode >= JK_A && keycode <= JK_Z)
		code = keycode - JK_A + XK_A;
	else if (keycode >= JK_F1 && keycode <= JK_F12)
		code = keycode - JK_F1 + XK_F1;
	else if (keycode >= JK_0 && keycode <= JK_9)
		code = keycode - JK_0 + XK_0;
	else
		code = jkeycode_to_xkey_map(keycode);
	
	if (code == 0)
		return NULL;
	
	return XKeysymToString(code);
}
