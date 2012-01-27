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

typedef struct {
	// variables de control
	long xid;
	gboolean installed;
	// servicios
	guint watcher;
	GDBusProxy *proxy;
	// variables java
	JavaVM *jvm;
	jobject that;
	// variables de menu
	DbusmenuServer *server;
	DbusmenuMenuitem *root;
	ListIndex *menus;
} JavaWindow;

typedef struct {
	JavaWindow *javawindow;
	long mid;
} JavaMenuEvent;


// variables gobales
GMainLoop *loop;
ListIndex *javawindows;



/* inicio de g_main_loop en un hilo independiente */
void *thread_g_main_loop_run(void *ptr) {
	g_main_loop_run(loop);
	return NULL;
}
/* control global de integración appmenu */
JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_initialize
  (JNIEnv *env, jclass thatclass) {
	XInitThreads();
	g_type_init();
	loop = g_main_loop_new(NULL, FALSE);
	pthread_t thread_g_main_loop_run_ptr;
	pthread_create(&thread_g_main_loop_run_ptr, NULL, thread_g_main_loop_run, NULL);
	javawindows = collection_list_index_new();
}
JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_uninitialize
  (JNIEnv *env, jclass thatclass) {
	g_main_loop_quit(loop);
	g_main_loop_unref(loop);
	collection_list_index_destory(javawindows);
}



/* Creacion del identificador de menu*/
char *get_xid_path(long xid) {
	char *xid_path;
	xid_path = (char *)malloc(sizeof(char *)*35);
	sprintf(xid_path, "/com/canonical/menu/%lx", xid);
	return xid_path;
}



/* invokar metodo install del objeto */
void java_install_with_env(JNIEnv *env, jobject that) {
	jclass thatclass = (*env)->GetObjectClass(env, that);
	jmethodID mid = (*env)->GetMethodID(env, thatclass, "install", "()V");
	(*env)->CallVoidMethod(env, that, mid);
}
void java_install(JavaWindow *javawindow) {
	JNIEnv *env = NULL;
	(*javawindow->jvm)->AttachCurrentThread(javawindow->jvm, (void**)&env, NULL);
	java_install_with_env(env, javawindow->that);
	(*javawindow->jvm)->DetachCurrentThread(javawindow->jvm);
}
/* invokar metodo uninstall del objeto*/
void java_uninstall_with_env(JNIEnv *env, jobject that) {
	jclass thatclass = (*env)->GetObjectClass(env, that);
	jmethodID mid = (*env)->GetMethodID(env, thatclass, "uninstall", "()V");
	(*env)->CallVoidMethod(env, that, mid);
}
void java_uninstall(JavaWindow *javawindow) {
	JNIEnv *env = NULL;
	(*javawindow->jvm)->AttachCurrentThread(javawindow->jvm, (void**)&env, NULL);
	java_uninstall_with_env(env, javawindow->that);
	(*javawindow->jvm)->DetachCurrentThread(javawindow->jvm);
}



/* control para menuitems */
void destroy_menuitem(DbusmenuMenuitem *parent) {
	if (parent != NULL) {
		GList *items = dbusmenu_menuitem_get_children(parent);
		if (items != NULL) {
			guint nitems = g_list_length(items);
			if (nitems > 0) {
				int i;
				DbusmenuMenuitem *item;
				for (i=0;i<nitems;i++) {
					item = (DbusmenuMenuitem *)items->data;
					destroy_menuitem(item);
					items = items->next;
				}
			}
		}
		if (dbusmenu_menuitem_get_parent(parent) != NULL)
			dbusmenu_menuitem_unparent(parent);
		g_object_unref(parent);
	}
}



/* eventos de existencia del application menu */
void on_registrar_available(GDBusConnection *connection, const gchar *name, const gchar *name_owner, gpointer user_data) {
	JavaWindow *javawindow = (JavaWindow *)user_data;
	
	GDBusProxy *proxy = g_dbus_proxy_new_for_bus_sync (
			G_BUS_TYPE_SESSION,
			G_DBUS_PROXY_FLAGS_NONE,
			NULL,
			"com.canonical.AppMenu.Registrar",
			"/com/canonical/AppMenu/Registrar",
			"com.canonical.AppMenu.Registrar",
			NULL, NULL);
	
	char *xid_path = get_xid_path(javawindow->xid);
	g_dbus_proxy_call_sync (
			proxy, "RegisterWindow",
			g_variant_new ("(uo)", javawindow->xid, xid_path),
			G_DBUS_CALL_FLAGS_NONE,
			-1, NULL, NULL);
	free(xid_path);
	
	javawindow->proxy = proxy;
	java_install(javawindow);
	javawindow->installed = TRUE;
}
void on_registrar_unavailable(GDBusConnection *connection, const gchar *name, gpointer user_data) {
	JavaWindow *javawindow = (JavaWindow *)user_data;
	if (javawindow->installed) {
		java_uninstall(javawindow);
		destroy_menuitem(javawindow->root);
		g_object_unref(javawindow->server);
	}
	javawindow->installed = FALSE;
}



/* control de existencia del application menu */
JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_registerWatcher
  (JNIEnv *env, jobject that, jlong xid) {
	JavaWindow *javawindow = (JavaWindow *)malloc(sizeof(JavaWindow));
	javawindow->menus = collection_list_index_new();
	(*env)->GetJavaVM(env, &javawindow->jvm);
	javawindow->that = (*env)->NewGlobalRef(env, that);
	javawindow->xid = xid;
	javawindow->installed = FALSE;
	
	char *xid_path = get_xid_path(javawindow->xid);
	DbusmenuServer *server = dbusmenu_server_new(xid_path);
	DbusmenuMenuitem *root = dbusmenu_menuitem_new();
	dbusmenu_server_set_root(server, root);
	javawindow->server = server;
	javawindow->root = root;
	free(xid_path);
	
	guint watcher = g_bus_watch_name(
			G_BUS_TYPE_SESSION,
			"com.canonical.AppMenu.Registrar",
			G_BUS_NAME_WATCHER_FLAGS_NONE,
	        on_registrar_available,
			on_registrar_unavailable,
			javawindow, NULL);
	
	javawindow->watcher = watcher;
	collection_list_index_add(javawindows, xid, javawindow);
}
JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_unregisterWatcher
  (JNIEnv *env, jobject that, jlong xid) { 
	JavaWindow *javawindow = (JavaWindow *)collection_list_index_remove(javawindows, xid);
	g_bus_unwatch_name(javawindow->watcher);
	if (javawindow->installed) {
		java_uninstall_with_env(env, that);
		g_object_unref(javawindow->proxy);
	}
	(*env)->DeleteGlobalRef(env, javawindow->that);
	destroy_menuitem(javawindow->root);
	g_object_unref(javawindow->server);
	collection_list_index_destory(javawindow->menus);
	free(javawindow);
}



/* obtener identificador XID de ventan */
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



/* Control de eventos de menus */
void item_destroy_data(gpointer user_data) {
	JavaMenuEvent *javamenuevent = (JavaMenuEvent *)user_data;
	free(javamenuevent);
}
void item_activated(DbusmenuMenuitem *item, guint timestamp, gpointer user_data) {
	JavaMenuEvent *javamenuevent = (JavaMenuEvent *)user_data;
	JNIEnv *env = NULL;
	(*javamenuevent->javawindow->jvm)->AttachCurrentThread(javamenuevent->javawindow->jvm, (void**)&env, NULL);
	jclass thatclass = (*env)->GetObjectClass(env, javamenuevent->javawindow->that);
	jmethodID mid = (*env)->GetMethodID(env, thatclass, "itemActivated", "(J)V");
	(*env)->CallVoidMethod(env, javamenuevent->javawindow->that, mid, javamenuevent->mid);
	(*javamenuevent->javawindow->jvm)->DetachCurrentThread(javamenuevent->javawindow->jvm);
}
void item_about_to_show(DbusmenuMenuitem *item, gpointer user_data) {
	JavaMenuEvent *javamenuevent = (JavaMenuEvent *)user_data;
	JNIEnv *env = NULL;
	(*javamenuevent->javawindow->jvm)->AttachCurrentThread(javamenuevent->javawindow->jvm, (void**)&env, NULL);
	jclass thatclass = (*env)->GetObjectClass(env, javamenuevent->javawindow->that);
	jmethodID mid = (*env)->GetMethodID(env, thatclass, "itemAboutToShow", "(J)V");
	(*env)->CallVoidMethod(env, javamenuevent->javawindow->that, mid, javamenuevent->mid);
	(*javamenuevent->javawindow->jvm)->DetachCurrentThread(javamenuevent->javawindow->jvm);
}



/* gestion de menus */
JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_addMenu
  (JNIEnv *env, jobject that, jlong xid, jlong pid, jlong mid) {
	JavaWindow *javawindow = (JavaWindow *)collection_list_index_get(javawindows, xid);
	DbusmenuMenuitem *parent;
	if (pid == -1l) {
		parent = javawindow->root;
	} else {
		parent = collection_list_index_get(javawindow->menus, pid);
	}
	DbusmenuMenuitem *item = dbusmenu_menuitem_new();
	JavaMenuEvent *javamenuevent = (JavaMenuEvent *)malloc(sizeof(JavaMenuEvent));
	javamenuevent->javawindow = javawindow;
	javamenuevent->mid = mid;
	g_signal_connect_data(G_OBJECT(item), DBUSMENU_MENUITEM_SIGNAL_ITEM_ACTIVATED,
			G_CALLBACK(item_activated), javamenuevent, (GClosureNotify)item_destroy_data, 0);
	g_signal_connect(G_OBJECT(item), DBUSMENU_MENUITEM_SIGNAL_ABOUT_TO_SHOW,
			G_CALLBACK(item_about_to_show), javamenuevent);
	dbusmenu_menuitem_child_append(parent, item);
	collection_list_index_add(javawindow->menus, mid, item);
}

JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_addSeparator
  (JNIEnv *env, jobject that, jlong xid, jlong pid, jlong mid) {
	JavaWindow *javawindow = (JavaWindow *)collection_list_index_get(javawindows, xid);
	DbusmenuMenuitem *parent;
	if (pid == -1l) {
		parent = javawindow->root;
	} else {
		parent = collection_list_index_get(javawindow->menus, pid);
	}
	DbusmenuMenuitem *item = dbusmenu_menuitem_new();
	dbusmenu_menuitem_property_set(item, DBUSMENU_MENUITEM_PROP_TYPE, DBUSMENU_CLIENT_TYPES_SEPARATOR);
	dbusmenu_menuitem_child_append(parent, item);
	collection_list_index_add(javawindow->menus, mid, item);
}

JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_setMenuItemLabel
  (JNIEnv *env, jobject that, jlong xid, jlong mid, jstring label) {
	JavaWindow *javawindow = (JavaWindow *)collection_list_index_get(javawindows, xid);
	DbusmenuMenuitem *item = collection_list_index_get(javawindow->menus, mid);
	const char *clabel = (*env)->GetStringUTFChars(env, label, 0);
	dbusmenu_menuitem_property_set(item, DBUSMENU_MENUITEM_PROP_LABEL, clabel);
}

JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_setMenuItemAccelerator
  (JNIEnv *env, jobject that, jlong xid, jlong mid, jint modifiers, jint keycode) {
	JavaWindow *javawindow = (JavaWindow *)collection_list_index_get(javawindows, xid);
	DbusmenuMenuitem *item = collection_list_index_get(javawindow->menus, mid);
	
	GVariantBuilder builder;
	g_variant_builder_init(&builder, G_VARIANT_TYPE_ARRAY);
	if ((modifiers & JK_SHIFT) == JK_SHIFT)
		g_variant_builder_add(&builder, "s",  DBUSMENU_MENUITEM_SHORTCUT_SHIFT);
	else if ((modifiers & JK_CTRL) == JK_CTRL)
		g_variant_builder_add(&builder, "s", DBUSMENU_MENUITEM_SHORTCUT_CONTROL);
	else if ((modifiers & JK_ALT) == JK_ALT)
		g_variant_builder_add(&builder, "s", DBUSMENU_MENUITEM_SHORTCUT_ALT);
		
	const char *keystring = jkeycode_to_xkey(keycode);
	g_variant_builder_add(&builder, "s", keystring);

	GVariant *inside = g_variant_builder_end(&builder);
	g_variant_builder_init(&builder, G_VARIANT_TYPE_ARRAY);
	g_variant_builder_add_value(&builder, inside);
	
	GVariant *outsidevariant = g_variant_builder_end(&builder);
	dbusmenu_menuitem_property_set_variant(item, DBUSMENU_MENUITEM_PROP_SHORTCUT, outsidevariant);
}

JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_setMenuItemToggleType
  (JNIEnv *env, jobject that, jlong xid, jlong mid, jint type) {
	JavaWindow *javawindow = (JavaWindow *)collection_list_index_get(javawindows, xid);
	DbusmenuMenuitem *item = collection_list_index_get(javawindow->menus, mid);
	if (type == org_java_ayatana_ApplicationMenu_TOGGLE_TYPE_RADIO)
		dbusmenu_menuitem_property_set (item, DBUSMENU_MENUITEM_PROP_TOGGLE_TYPE, DBUSMENU_MENUITEM_TOGGLE_RADIO);
	else
		dbusmenu_menuitem_property_set (item, DBUSMENU_MENUITEM_PROP_TOGGLE_TYPE, DBUSMENU_MENUITEM_TOGGLE_CHECK);
}

JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_setMenuItemToggleState
  (JNIEnv *env, jobject that, jlong xid, jlong mid, jint state) {
	JavaWindow *javawindow = (JavaWindow *)collection_list_index_get(javawindows, xid);
	DbusmenuMenuitem *item = collection_list_index_get(javawindow->menus, mid);
	if (state == org_java_ayatana_ApplicationMenu_TOGGLE_STATE_CHECKED)
		dbusmenu_menuitem_property_set_int(item, DBUSMENU_MENUITEM_PROP_TOGGLE_STATE, DBUSMENU_MENUITEM_TOGGLE_STATE_CHECKED);
	else
		dbusmenu_menuitem_property_set_int(item, DBUSMENU_MENUITEM_PROP_TOGGLE_STATE, DBUSMENU_MENUITEM_TOGGLE_STATE_UNCHECKED);
}

JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_setMenuItemVisible
  (JNIEnv *env, jobject that, jlong xid, jlong mid, jboolean visible) {
	JavaWindow *javawindow = (JavaWindow *)collection_list_index_get(javawindows, xid);
	DbusmenuMenuitem *item = collection_list_index_get(javawindow->menus, mid);
	dbusmenu_menuitem_property_set_bool(item, DBUSMENU_MENUITEM_PROP_VISIBLE, visible);
}

JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_setMenuItemEnable
  (JNIEnv *env, jobject that, jlong xid, jlong mid, jboolean enable) {
	JavaWindow *javawindow = (JavaWindow *)collection_list_index_get(javawindows, xid);
	DbusmenuMenuitem *item = collection_list_index_get(javawindow->menus, mid);
	dbusmenu_menuitem_property_set_bool(item, DBUSMENU_MENUITEM_PROP_ENABLED, enable);
}

JNIEXPORT void JNICALL Java_org_java_ayatana_ApplicationMenu_removeMenuItem
  (JNIEnv *env, jobject that, jlong xid, jlong mid) {
	JavaWindow *javawindow = (JavaWindow *)collection_list_index_get(javawindows, xid);
	if (javawindow != NULL) {
		DbusmenuMenuitem *item = collection_list_index_remove(javawindow->menus, mid);
		destroy_menuitem(item);
	}
}
