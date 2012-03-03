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
 * File:   org_java_ayatana_ApplicationMenu.c
 * Author: Jared González
 */

#include "org_java_ayatana_ApplicationMenu.h"
#include <jawt_md.h>
#include <stdlib.h>
#include <strings.h>
#include <pthread.h>
#include <glib.h>
#include <gio/gio.h>
#include <libdbusmenu-glib/server.h>
#include <libdbusmenu-glib/client.h>
#include "org_java_ayatana_Collections.h"
#include "org_java_ayatana_JKeyToXKey.h"



//estrucutra de instancia
typedef struct {
	jlong windowxid;
	JavaVM *jvm;
	jobject that;
	gchar *windowxidpath;
	guint watcher;
	gboolean installed;
	DbusmenuServer *menuserver;
	DbusmenuMenuitem *menuroot;
	DbusmenuMenuitem *menucurrent;
} JavaInstance;
ListIndex *jinstances;
ListIndex *jinstancesstack;



// g_main_loop
GMainLoop *loop;
// hilo g_main_loop
void *pthread_g_main_loop(void *ptr) {
	loop = g_main_loop_new(NULL, FALSE);
	g_main_loop_run(loop);
	return NULL;
}


/* control global de integración appmenu */
JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_nativeInitialize
  (JNIEnv *env, jclass thatclass) {
	XInitThreads();
	g_type_init();
	jinstances = collection_list_index_new();
	jinstancesstack = collection_list_index_new();
	pthread_t thread;
	pthread_create(&thread, NULL, pthread_g_main_loop, NULL);
}
JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_nativeUninitialize
  (JNIEnv *env, jclass thatclass) {
	g_main_loop_quit(loop);
	collection_list_index_destory(jinstancesstack);
	collection_list_index_destory(jinstances);
}



/* obtención del windowxid de una ventana Java */
JNIEXPORT jlong JNICALL Java_org_java_ayatana_ApplicationMenu_getWindowXID
  (JNIEnv *env, jobject that, jobject window) {
	JAWT awt;
	JAWT_DrawingSurface* ds;
	JAWT_DrawingSurfaceInfo* dsi;
	JAWT_X11DrawingSurfaceInfo* dsi_x11;
	jint dsLock;
	Drawable drawable = -1l;
	awt.version = JAWT_VERSION_1_4;
	if (JAWT_GetAWT(env, &awt) != 0) {
		ds = awt.GetDrawingSurface(env, window);
		if (ds != NULL) {
			dsLock = ds->Lock(ds);
			if ((dsLock & JAWT_LOCK_ERROR) == 0) {
				dsi = ds->GetDrawingSurfaceInfo(ds);
				dsi_x11 = (JAWT_X11DrawingSurfaceInfo*)dsi->platformInfo;
				drawable = dsi_x11->drawable;
				ds->FreeDrawingSurfaceInfo(dsi);
				ds->Unlock(ds);
			}
		}
		awt.FreeDrawingSurface(ds);
	}
	return (long)drawable;
}



/* Creacion del identificador de menu*/
char *get_windowxid_path(long xid) {
	char *xid_path;
	xid_path = (char *)malloc(sizeof(char *)*50);
	sprintf(xid_path, "/com/canonical/menu/%lx", xid);
	return xid_path;
}
/* control para eliminar menus */
void destroy_menu(DbusmenuMenuitem *parent) {
	if (parent != NULL) {
		GList *items = dbusmenu_menuitem_take_children(parent);
		if (items != NULL) {
			do {
				DbusmenuMenuitem *item = (DbusmenuMenuitem *)items->data;
				destroy_menu(item);
				items = items->next;
			} while (items != NULL);
		}
		if (dbusmenu_menuitem_get_parent(parent) != NULL)
			dbusmenu_menuitem_unparent(parent);
		g_object_unref(parent);
	}
}
/* control para eliminar menus */
void destroy_menu_items(DbusmenuMenuitem *parent) {
	if (parent != NULL) {
		GList *items = dbusmenu_menuitem_take_children(parent);
		if (items != NULL) {
			do {
				DbusmenuMenuitem *item = (DbusmenuMenuitem *)items->data;
				destroy_menu(item);
				items = items->next;
			} while (items != NULL);
		}
	}
}
/* eventos de existencia del application menu */
void on_registrar_available(GDBusConnection *connection, const gchar *name, const gchar *name_owner, gpointer user_data) {
	JavaInstance *jinstance = (JavaInstance *)user_data;
	if (!jinstance->installed) {
		// generar base de menu
		jinstance->windowxidpath = get_windowxid_path(jinstance->windowxid);
		DbusmenuServer *menuserver = dbusmenu_server_new(jinstance->windowxidpath);
		DbusmenuMenuitem *menuroot = dbusmenu_menuitem_new();
		dbusmenu_server_set_root(menuserver, menuroot);
		jinstance->menuserver = menuserver;
		jinstance->menuroot = menuroot;
		jinstance->menucurrent = menuroot;
		// registar menu de aplicaciones
		GDBusProxy *proxy = g_dbus_proxy_new_for_bus_sync (
				G_BUS_TYPE_SESSION,
				G_DBUS_PROXY_FLAGS_NONE,
				NULL,
				"com.canonical.AppMenu.Registrar",
				"/com/canonical/AppMenu/Registrar",
				"com.canonical.AppMenu.Registrar",
				NULL, NULL);
		g_dbus_proxy_call_sync(
				proxy, "RegisterWindow",
				g_variant_new("(uo)", jinstance->windowxid, jinstance->windowxidpath),
				G_DBUS_CALL_FLAGS_NONE,
				-1, NULL, NULL);
		// instalar java
		JNIEnv *env = NULL;
		(*jinstance->jvm)->AttachCurrentThread(jinstance->jvm, (void**)&env, NULL);
		jclass thatclass = (*env)->GetObjectClass(env, jinstance->that);
		jmethodID mid = (*env)->GetMethodID(env, thatclass, "install", "()V");
		(*env)->CallVoidMethod(env, jinstance->that, mid);
		(*jinstance->jvm)->DetachCurrentThread(jinstance->jvm);
		// marcar como instalado
		jinstance->installed = TRUE;
	}
}
/* eventos de existencia del application menu */
void on_registrar_unavailable(GDBusConnection *connection, const gchar *name, gpointer user_data) {
	JavaInstance *jinstance = (JavaInstance *)user_data;
	if (jinstance->installed) {
		// eliminar menus
		destroy_menu(jinstance->menuroot);
		g_object_unref(jinstance->menuserver);
		// desinstalar java
		JNIEnv *env = NULL;
		(*jinstance->jvm)->AttachCurrentThread(jinstance->jvm, (void**)&env, NULL);
		jclass thatclass = (*env)->GetObjectClass(env, jinstance->that);
		jmethodID mid = (*env)->GetMethodID(env, thatclass, "uninstall", "()V");
		(*env)->CallVoidMethod(env, jinstance->that, mid);
		(*jinstance->jvm)->DetachCurrentThread(jinstance->jvm);
		// eliminar path
		free(jinstance->windowxidpath);
		// marcar como desinstalado
		jinstance->installed = FALSE;
	}
}
JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_registerWatcher
  (JNIEnv *env, jobject that, jlong windowxid) { 
	JavaInstance *jinstance = (JavaInstance *)malloc(sizeof(JavaInstance));
	jinstance->windowxid = windowxid;
	jinstance->installed = FALSE;
	// registro de variables java
	(*env)->GetJavaVM(env, &jinstance->jvm);
	jinstance->that = (*env)->NewGlobalRef(env, that);
	// revisor de menu de aplicaciones
	guint watcher = g_bus_watch_name(
			G_BUS_TYPE_SESSION,
			"com.canonical.AppMenu.Registrar",
			G_BUS_NAME_WATCHER_FLAGS_NONE,
	        on_registrar_available,
			on_registrar_unavailable,
			jinstance, NULL);
	jinstance->watcher = watcher;
	// agregar instancia
	collection_list_index_add(jinstances, windowxid, jinstance);
}
JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_unregisterWatcher
  (JNIEnv *env, jobject that, jlong windowxid) {
	JavaInstance *jinstance = (JavaInstance *)collection_list_index_remove(jinstances, windowxid);
	g_bus_unwatch_name(jinstance->watcher);
	if (jinstance->installed) {
		// eliminar menus
		destroy_menu(jinstance->menuroot);
		g_object_unref(jinstance->menuserver);
		// desinstalar
		jclass thatclass = (*env)->GetObjectClass(env, jinstance->that);
		jmethodID mid = (*env)->GetMethodID(env, thatclass, "uninstall", "()V");
		(*env)->CallVoidMethod(env, jinstance->that, mid);
		// eliminar path
		free(jinstance->windowxidpath);
	}
	// deregistro de variables java
	(*env)->DeleteGlobalRef(env, jinstance->that);
	//eliminar instancia
	free(jinstance);
}


/* evento después de mostrar el menu */
void item_event(DbusmenuMenuitem *item) {
	gboolean select;
	dbusmenu_menuitem_property_set_bool(item, "jayatana-select",
			(select = !dbusmenu_menuitem_property_get_bool(item, "jayatana-select")));
	if (!select) {
		JNIEnv *env = NULL;
		JavaInstance *jinstance = (JavaInstance *)collection_list_index_get_last(jinstancesstack);
		(*jinstance->jvm)->AttachCurrentThread(jinstance->jvm, (void**)&env, NULL);
		jclass thatclass = (*env)->GetObjectClass(env, jinstance->that);
		jmethodID mid = (*env)->GetMethodID(env, thatclass, "itemAfterShow", "(I)V");
		(*env)->CallVoidMethod(env, jinstance->that, mid,
				dbusmenu_menuitem_property_get_int(item, "jayatana-hashcode"));
		(*jinstance->jvm)->DetachCurrentThread(jinstance->jvm);
	}
}
/* evento de actviar el menu*/
void item_activated (DbusmenuMenuitem *item, guint timestamp, gpointer user_data) {
	JavaInstance *jinstance = (JavaInstance *)user_data;
	// invocar event de menu
	JNIEnv *env = NULL;
	(*jinstance->jvm)->AttachCurrentThread(jinstance->jvm, (void**)&env, NULL);
	jclass thatclass = (*env)->GetObjectClass(env, jinstance->that);
	jmethodID mid = (*env)->GetMethodID(env, thatclass, "itemActivated", "(I)V");
	(*env)->CallVoidMethod(env, jinstance->that, mid,
			dbusmenu_menuitem_property_get_int(item, "jayatana-hashcode"));
	(*jinstance->jvm)->DetachCurrentThread(jinstance->jvm);
	
	DbusmenuMenuitem *parent = dbusmenu_menuitem_get_parent(item);
	while (parent != NULL && parent != jinstance->menuroot) {
		item_event(parent);
		parent = dbusmenu_menuitem_get_parent(parent);
	}
}
/* evento antes de mostrar el menu*/
void item_about_to_show(DbusmenuMenuitem *item, gpointer user_data) {
	JavaInstance *jinstance = (JavaInstance *)user_data;
	collection_list_index_add_last(jinstancesstack, jinstance);
	//inicializar menu
	jinstance->menucurrent = item;
	destroy_menu_items(item);
	// invocar generacion de menus
	JNIEnv *env = NULL;
	(*jinstance->jvm)->AttachCurrentThread(jinstance->jvm, (void**)&env, NULL);
	jclass thatclass = (*env)->GetObjectClass(env, jinstance->that);
	jmethodID mid = (*env)->GetMethodID(env, thatclass, "itemAboutToShow", "(I)V");
	(*env)->CallVoidMethod(env, jinstance->that, mid,
			dbusmenu_menuitem_property_get_int(item, "jayatana-hashcode"));
	(*jinstance->jvm)->DetachCurrentThread(jinstance->jvm);
}
/* agrega un menu padre */
JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_addMenu
  (JNIEnv *env, jobject that, jlong windowxid, jint hashcode, jstring label, jboolean enabled) {
	JavaInstance *jinstance = (JavaInstance *)collection_list_index_get(jinstances, windowxid);
	DbusmenuMenuitem *item = dbusmenu_menuitem_new();
	const char *cclabel = (*env)->GetStringUTFChars(env, label, 0);
	dbusmenu_menuitem_property_set(item, DBUSMENU_MENUITEM_PROP_LABEL, cclabel);
	dbusmenu_menuitem_property_set(item, DBUSMENU_MENUITEM_PROP_CHILD_DISPLAY,
			DBUSMENU_MENUITEM_CHILD_DISPLAY_SUBMENU);
	dbusmenu_menuitem_property_set_bool(item, DBUSMENU_MENUITEM_PROP_ENABLED, (gboolean)enabled);
	dbusmenu_menuitem_property_set_int(item, "jayatana-hashcode", hashcode);
	dbusmenu_menuitem_property_set_bool(item, "jayatana-select", FALSE);
	g_signal_connect(G_OBJECT(item), DBUSMENU_MENUITEM_SIGNAL_ABOUT_TO_SHOW,
			G_CALLBACK(item_about_to_show), jinstance);
	g_signal_connect(G_OBJECT(item), DBUSMENU_MENUITEM_SIGNAL_EVENT,
			G_CALLBACK(item_event), NULL);
	dbusmenu_menuitem_child_append(jinstance->menucurrent, item);
	
	DbusmenuMenuitem *foo = dbusmenu_menuitem_new();
	dbusmenu_menuitem_property_set(foo, DBUSMENU_MENUITEM_PROP_LABEL, "");
	dbusmenu_menuitem_child_append(item, foo);
}
/* elimina todos los menu */
JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_removeAllMenus
  (JNIEnv *env, jobject that, jlong windowxid) {
	JavaInstance *jinstance = (JavaInstance *)collection_list_index_get(jinstances, windowxid);
	destroy_menu_items(jinstance->menuroot);
}
/* establece el acelerador de menu */
void set_menuitem_shortcut(DbusmenuMenuitem *item, jint modifiers, jint keycode) {
	GVariantBuilder builder;
	g_variant_builder_init(&builder, G_VARIANT_TYPE_ARRAY);
	if ((modifiers & JK_SHIFT) == JK_SHIFT)
		g_variant_builder_add(&builder, "s",  DBUSMENU_MENUITEM_SHORTCUT_SHIFT);
	if ((modifiers & JK_CTRL) == JK_CTRL)
		g_variant_builder_add(&builder, "s", DBUSMENU_MENUITEM_SHORTCUT_CONTROL);
	if ((modifiers & JK_ALT) == JK_ALT)
		g_variant_builder_add(&builder, "s", DBUSMENU_MENUITEM_SHORTCUT_ALT);
	const char *keystring = jkeycode_to_xkey(keycode);
	g_variant_builder_add(&builder, "s", keystring);
	GVariant *inside = g_variant_builder_end(&builder);
	g_variant_builder_init(&builder, G_VARIANT_TYPE_ARRAY);
	g_variant_builder_add_value(&builder, inside);
	GVariant *outsidevariant = g_variant_builder_end(&builder);
	dbusmenu_menuitem_property_set_variant(item, DBUSMENU_MENUITEM_PROP_SHORTCUT, outsidevariant);
}
/* agrega un menu hoja */
JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_addMenuItem
  (JNIEnv *env, jobject that, jlong windowxid, jint hashcode, jstring label, jboolean enabled, jint modifiers, jint keycode) {
	JavaInstance *jinstance = (JavaInstance *)collection_list_index_get(jinstances, windowxid);
	DbusmenuMenuitem *item = dbusmenu_menuitem_new();
	const char *cclabel = (*env)->GetStringUTFChars(env, label, 0);
	dbusmenu_menuitem_property_set(item, DBUSMENU_MENUITEM_PROP_LABEL, cclabel);
	dbusmenu_menuitem_property_set_bool(item, DBUSMENU_MENUITEM_PROP_ENABLED, (gboolean)enabled);
	dbusmenu_menuitem_property_set_int(item, "jayatana-hashcode", hashcode);
	g_signal_connect(G_OBJECT(item), DBUSMENU_MENUITEM_SIGNAL_ITEM_ACTIVATED,
			G_CALLBACK(item_activated), jinstance);
	if (modifiers > -1 && keycode > -1)
		set_menuitem_shortcut(item, modifiers, keycode);
	dbusmenu_menuitem_child_append(jinstance->menucurrent, item);
}
/* agrega un menu opcional hoja */
JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_addMenuItemRadio
  (JNIEnv *env, jobject that, jlong windowxid, jint hashcode, jstring label, jboolean enabled, jint modifiers, jint keycode, jboolean selected) {
	JavaInstance *jinstance = (JavaInstance *)collection_list_index_get(jinstances, windowxid);
	DbusmenuMenuitem *item = dbusmenu_menuitem_new();
	const char *cclabel = (*env)->GetStringUTFChars(env, label, 0);
	dbusmenu_menuitem_property_set(item, DBUSMENU_MENUITEM_PROP_LABEL, cclabel);
	dbusmenu_menuitem_property_set_bool(item, DBUSMENU_MENUITEM_PROP_ENABLED, (gboolean)enabled);
	dbusmenu_menuitem_property_set_int(item, "jayatana-hashcode", hashcode);
	g_signal_connect(G_OBJECT(item), DBUSMENU_MENUITEM_SIGNAL_ITEM_ACTIVATED,
			G_CALLBACK(item_activated), jinstance);
	if (modifiers > -1 && keycode > -1)
		set_menuitem_shortcut(item, modifiers, keycode);
	dbusmenu_menuitem_property_set (item, DBUSMENU_MENUITEM_PROP_TOGGLE_TYPE, DBUSMENU_MENUITEM_TOGGLE_RADIO);
	dbusmenu_menuitem_property_set_int(item, DBUSMENU_MENUITEM_PROP_TOGGLE_STATE,
			selected ? DBUSMENU_MENUITEM_TOGGLE_STATE_CHECKED : DBUSMENU_MENUITEM_TOGGLE_STATE_UNCHECKED);
	dbusmenu_menuitem_child_append(jinstance->menucurrent, item);
}
/* agrega un menu verificación hoja */
JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_addMenuItemCheck
  (JNIEnv *env, jobject that, jlong windowxid, jint hashcode, jstring label, jboolean enabled, jint modifiers, jint keycode, jboolean selected) {
	JavaInstance *jinstance = (JavaInstance *)collection_list_index_get(jinstances, windowxid);
	DbusmenuMenuitem *item = dbusmenu_menuitem_new();
	const char *cclabel = (*env)->GetStringUTFChars(env, label, 0);
	dbusmenu_menuitem_property_set(item, DBUSMENU_MENUITEM_PROP_LABEL, cclabel);
	dbusmenu_menuitem_property_set_bool(item, DBUSMENU_MENUITEM_PROP_ENABLED, (gboolean)enabled);
	dbusmenu_menuitem_property_set_int(item, "jayatana-hashcode", hashcode);
	g_signal_connect(G_OBJECT(item), DBUSMENU_MENUITEM_SIGNAL_ITEM_ACTIVATED,
			G_CALLBACK(item_activated), jinstance);
	if (modifiers > -1 && keycode > -1)
		set_menuitem_shortcut(item, modifiers, keycode);
	dbusmenu_menuitem_property_set (item, DBUSMENU_MENUITEM_PROP_TOGGLE_TYPE, DBUSMENU_MENUITEM_TOGGLE_CHECK);
	dbusmenu_menuitem_property_set_int(item, DBUSMENU_MENUITEM_PROP_TOGGLE_STATE,
			selected ? DBUSMENU_MENUITEM_TOGGLE_STATE_CHECKED : DBUSMENU_MENUITEM_TOGGLE_STATE_UNCHECKED);
	dbusmenu_menuitem_child_append(jinstance->menucurrent, item);
}
/* agrega un separador */
JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_addMenuItemSeparator
  (JNIEnv *env, jobject that, jlong windowxid) {
	JavaInstance *jinstance = (JavaInstance *)collection_list_index_get(jinstances, windowxid);
	DbusmenuMenuitem *item = dbusmenu_menuitem_new();
	dbusmenu_menuitem_property_set(item, DBUSMENU_MENUITEM_PROP_TYPE, DBUSMENU_CLIENT_TYPES_SEPARATOR);
	dbusmenu_menuitem_child_append(jinstance->menucurrent, item);
}
