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
 * File:   org_java_ayatana_JavaKeycodeToDbusmenu.h
 * Author: Jared González
 */

#ifndef ORG_JAVA_AYATANA_JAVAKEYCODETODBUSMENU_H
#define	ORG_JAVA_AYATANA_JAVAKEYCODETODBUSMENU_H

#ifdef	__cplusplus
extern "C" {
#endif

#define JK_SHIFT (1 << 0)
#define JK_CTRL (1 << 1)
#define JK_ALT (1 << 3)

#define JK_A 0x41
#define JK_Z 0x5A
#define JK_F1 0x70
#define JK_F12 0x7B
#define JK_0 0x30
#define JK_9 0x39
	
char *jkeycode_to_xkey(int);

#ifdef	__cplusplus
}
#endif

#endif	/* ORG_JAVA_AYATANA_JAVAKEYCODETODBUSMENU_H */

