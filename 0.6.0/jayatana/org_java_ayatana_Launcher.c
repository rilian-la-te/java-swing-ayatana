#include "org_java_ayatana_Launcher.h"

#include <unity/unity/unity.h>

UnityLauncherEntry *launcher;

JNIEXPORT void JNICALL Java_org_java_ayatana_Launcher_initialize
  (JNIEnv *env, jobject that, jstring desktopFile) {
	const char *cdfn = (*env)->GetStringUTFChars(env, desktopFile, 0);
	launcher = unity_launcher_entry_get_for_desktop_file(cdfn);
}

JNIEXPORT void JNICALL Java_org_java_ayatana_Launcher_setNativeCount
  (JNIEnv *env, jobject that, jlong count) {
	unity_launcher_entry_set_count(launcher, count);
}

JNIEXPORT void JNICALL Java_org_java_ayatana_Launcher_setNativeCountVisible
  (JNIEnv *env, jobject that, jboolean visible) {
	unity_launcher_entry_set_count_visible(launcher, visible);
}

JNIEXPORT void JNICALL Java_org_java_ayatana_Launcher_setNativeProgress
  (JNIEnv *env, jobject that, jdouble progress) {
	unity_launcher_entry_set_progress(launcher, progress);
}

JNIEXPORT void JNICALL Java_org_java_ayatana_Launcher_setNativeProgressVisible
  (JNIEnv *env, jobject that, jboolean visible) {
	unity_launcher_entry_set_progress_visible(launcher, visible);
}

JNIEXPORT jlong JNICALL Java_org_java_ayatana_Launcher_getCount
  (JNIEnv *env, jobject that) {
	return unity_launcher_entry_get_count(launcher);
}

JNIEXPORT jboolean JNICALL Java_org_java_ayatana_Launcher_isCountVisible
  (JNIEnv *env, jobject that) {
	return unity_launcher_entry_get_count_visible(launcher);
}

JNIEXPORT jdouble JNICALL Java_org_java_ayatana_Launcher_getProgress
  (JNIEnv *env, jobject that) {
	return unity_launcher_entry_get_progress(launcher);
}

JNIEXPORT jboolean JNICALL Java_org_java_ayatana_Launcher_isProgressVisible
  (JNIEnv *env, jobject that) {
	return unity_launcher_entry_get_progress_visible(launcher);
}

JNIEXPORT void JNICALL Java_org_java_ayatana_Launcher_setNativeUrgent
  (JNIEnv *env, jobject that, jboolean urgent) {
	unity_launcher_entry_set_urgent(launcher, urgent);
}

JNIEXPORT jboolean JNICALL Java_org_java_ayatana_Launcher_isUrgent
  (JNIEnv *env, jobject that) {
	return unity_launcher_entry_get_urgent(launcher);
}
