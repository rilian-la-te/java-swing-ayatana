#include "com_jarego_java_ayatana_agent.h"

#include <jvmti.h>
#include <stdio.h>
#include <string.h>

static void JNICALL
vmInit(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread) {
    jclass clsAyatanaInstaller = (*jni_env)->FindClass(jni_env, "com/jarego/java/ayatana/AyatanaInstaller");
    jmethodID midInstall = (*jni_env)->GetStaticMethodID(jni_env, clsAyatanaInstaller, "install", "()V");
    (*jni_env)->CallStaticVoidMethod(jni_env, clsAyatanaInstaller, midInstall);
}

JNIEXPORT jint JNICALL
Agent_OnLoad(JavaVM *vm, char *options, void *reserved) {
    jvmtiEnv *jvmti;
    (*vm)->GetEnv(vm, (void**) &jvmti, JVMTI_VERSION);

    jvmtiEventCallbacks callbacks;
    memset(&callbacks, 0, sizeof (jvmtiEventCallbacks));
    callbacks.VMInit = vmInit;
    (*jvmti)->SetEventCallbacks(jvmti, &callbacks, sizeof (jvmtiEventCallbacks));

    jvmtiCapabilities capabilities;
    memset(&capabilities, 0, sizeof (jvmtiCapabilities));
    (*jvmti)->AddCapabilities(jvmti, &capabilities);

    (*jvmti)->SetEventNotificationMode(jvmti, JVMTI_ENABLE, JVMTI_EVENT_VM_INIT, NULL);
    return 0;
}

JNIEXPORT void JNICALL
Agent_OnUnload(JavaVM *vm) {

}
