#include "com_jarego_jayatana_jni.h"

/**
 * Detenectar cuando la librería se carga a la maquina virtual de Java
 */
jint JNI_OnLoad(JavaVM *jvm, void *reserved) {
	jayatana_jvm = jvm;
	return JNI_VERSION_1_6;
}
