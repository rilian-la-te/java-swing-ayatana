#ifndef COM_JAREGO_JAVA_AYATANA_AGENT_H
#define	COM_JAREGO_JAVA_AYATANA_AGENT_H

#ifdef	__cplusplus
extern "C" {
#endif

#include <jni.h>
    
JNIEXPORT jint JNICALL 
Agent_OnLoad(JavaVM *vm, char *options, void *reserved);

JNIEXPORT void JNICALL 
Agent_OnUnload(JavaVM *vm);

#ifdef	__cplusplus
}
#endif

#endif	/* COM_JAREGO_JAVA_AYATANA_AGENT_H */

