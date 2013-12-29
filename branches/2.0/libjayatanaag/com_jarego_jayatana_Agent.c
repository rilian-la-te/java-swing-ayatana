/*
 * Copyright (c) 2013 Jared González
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
 * File:   ayatana_Collections.c
 * Author: Jared González
 */
#include <jvmti.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <X11/Xlib.h>

#include "com_jarego_jayatana_Agent.h"

/*
 * Encabezado para validar valor de variable de ambiente
 */
int com_jarego_jayatana_Agent_CheckEnv(const char *envname, const char *envval, const int def);

/*
 * Cargar agente para integración con Ubuntu/Linux
 */
static void JNICALL
com_jarego_jayatana_Agent_threadStart(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread) {
	// recuperar información del hilo
	jvmtiError error;
	jvmtiThreadInfo info;
	error = (*jvmti_env)->GetThreadInfo(jvmti_env, thread, &info);
	if (error == JVMTI_ERROR_NONE) {
		// inicializar XInitThreads para corregir defecto en OpenJDK 6 para los hilos de AWT o
		// Java 2D
		if (strcmp(info.name, "Java2D Disposer") == 0) {
			// inicializar hilos de X, solo para OpenJDK 6
			char *version = 0;
			if ((*jvmti_env)->GetSystemProperty(
					jvmti_env, "java.vm.specification.version", &version) == JVMTI_ERROR_NONE) {
				if (strcmp(version, "1.0") == 0) {
					// TODO: Utilizando openjdk6, al actualizar el objeto
					// splashScreen (splashScreen.update) la aplicacion muere.
					// Existe un conflicto al utilizar XInitThread y pthread.
					// Error:
					//   java: pthread_mutex_lock.c:317: __pthread_mutex_lock_full: La declaración `(-(e)) != 3 || !robust' no se cumple.
					XInitThreads();
				}
				(*jvmti_env)->Deallocate(jvmti_env, (unsigned char*)version);
			}
		} else if (strcmp(info.name, "AWT-XAWT") == 0) {
			// instala la clase para control de integración Swing
			jclass clsInstallers = (*jni_env)->FindClass(
					jni_env, "com/jarego/jayatana/FeatureManager");
			if (clsInstallers != NULL) {
				jmethodID midInstallForSwing = (*jni_env)->GetStaticMethodID(
						jni_env, clsInstallers, "deployForSwing", "()V");
				(*jni_env)->CallStaticVoidMethod(jni_env, clsInstallers, midInstallForSwing);
			}
		}
	}
}

/*
 * Carga del agente de Java Ayatana
 */
JNIEXPORT jint JNICALL
Agent_OnLoad(JavaVM *vm, char *options, void *reserved) {
	if (com_jarego_jayatana_Agent_CheckEnv("XDG_CURRENT_DESKTOP", "Unity", False) ||
			com_jarego_jayatana_Agent_CheckEnv("JAYATANA_FORCE", "true", False) ||
			!com_jarego_jayatana_Agent_CheckEnv("JAYATANA", "", True)) {

		// inicializar entorno
		jvmtiEnv *jvmti_env;
		(*vm)->GetEnv(vm, (void**) &jvmti_env, JVMTI_VERSION);

		// activar capacidades
		jvmtiCapabilities capabilities;
		memset(&capabilities, 0, sizeof(jvmtiCapabilities));
		(*jvmti_env)->AddCapabilities(jvmti_env, &capabilities);

		// registrar funciones de eventos
		jvmtiEventCallbacks callbacks;
		memset(&callbacks, 0, sizeof(jvmtiEventCallbacks));
		callbacks.ThreadStart = &com_jarego_jayatana_Agent_threadStart;

		// habilitar gestor de eventos
		(*jvmti_env)->SetEventNotificationMode(jvmti_env,
				JVMTI_ENABLE, JVMTI_EVENT_THREAD_START, (jthread)NULL);

		// registrar gestor de eventos
		(*jvmti_env)->SetEventCallbacks(jvmti_env, &callbacks, (jint)sizeof(jvmtiEventCallbacks));

		// cargar ruta de clases jayatana
		if (getenv("JAYATANA_CLASSPATH") != NULL) // opción para desarrollo
			(*jvmti_env)->AddToSystemClassLoaderSearch(jvmti_env, getenv("JAYATANA_CLASSPATH"));
		else
			(*jvmti_env)->AddToSystemClassLoaderSearch(jvmti_env, "/usr/share/java/jayatana.jar");
	}
	return JVMTI_ERROR_NONE;
}

/*
 * Verificar valor de variable de ambiente.
 */
int com_jarego_jayatana_Agent_CheckEnv(const char *envname, const char *envval, const int def) {
	if (getenv(envname) == NULL) return def;
	if (strcmp(getenv(envname), envval) == 0) return 1;
	else return 0;
}

/*
 * Descargar agente
 */
JNIEXPORT void JNICALL
Agent_OnUnload(JavaVM *vm) {

}
