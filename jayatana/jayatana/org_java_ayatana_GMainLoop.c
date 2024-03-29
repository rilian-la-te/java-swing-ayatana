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
 * File:   org_java_ayatana_GMainLoop.c
 * Author: Jared González
 */

#include "org_java_ayatana_GMainLoop.h"

#include <pthread.h>
#include <glib.h>
#include <gio/gio.h>
#include <X11/Xlib.h>

GMainLoop *loop;

void *g_main_loop_thread( void *ptr ) {
	loop = g_main_loop_new(NULL, FALSE);
	g_main_loop_run(loop);
	return NULL;
}

JNIEXPORT void JNICALL Java_org_java_ayatana_GMainLoop_runGMainLoop
  (JNIEnv *env, jclass thatclass) {
	g_type_init();
	g_thread_init(NULL);
	XInitThreads();
	g_thread_create(g_main_loop_thread, NULL, TRUE, NULL);
}

JNIEXPORT void JNICALL Java_org_java_ayatana_GMainLoop_quitGMainLoop
  (JNIEnv *env, jclass thatclass) {
	g_main_loop_quit(loop);
}
