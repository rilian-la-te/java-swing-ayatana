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
 * File:   org_java_ayatana_Launcher.c
 * Author: Jared González
 */

#include "org_java_ayatana_Launcher.h"
#include <unity/unity/unity.h>

UnityLauncherEntry *launcher;

JNIEXPORT void JNICALL Java_org_java_ayatana_Launcher_initialize
  (JNIEnv *env, jobject that, jstring desktopfile) {
	const char *cdfn = (*env)->GetStringUTFChars(env, desktopfile, 0);
	launcher = unity_launcher_entry_get_for_desktop_id(cdfn);
	g_warning("%s", cdfn);
}

JNIEXPORT void JNICALL Java_org_java_ayatana_Launcher__1setProgressVisible
  (JNIEnv *env, jobject that, jboolean visible) {
	unity_launcher_entry_set_progress_visible(launcher, visible);
	g_warning("visible=%d %d", visible, TRUE);
}

JNIEXPORT jboolean JNICALL Java_org_java_ayatana_Launcher_isProgressVisible
  (JNIEnv *env, jobject that) {
	return unity_launcher_entry_get_progress_visible(launcher);
}

JNIEXPORT void JNICALL Java_org_java_ayatana_Launcher__1setProgressValue
  (JNIEnv *env, jobject that, jdouble value) {
	unity_launcher_entry_set_progress(launcher, value);
	g_warning("value=%f", value);
}

JNIEXPORT jdouble JNICALL Java_org_java_ayatana_Launcher_getProgressValue
  (JNIEnv *env, jobject that) {
	return unity_launcher_entry_get_progress(launcher);
}
