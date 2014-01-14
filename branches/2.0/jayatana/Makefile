# CONFIGURACION DE JAVA
# Obtener arquitectura de compilación
ARCH=$(shell getconf LONG_BIT)
ifeq (${ARCH},64)
JAVA_ARCH=amd64
else
JAVA_ARCH=i386
endif
# Obtener versión Java de compilación
ifndef JAVA_HOME
JAVA_HOME=/usr/lib/jvm/java-7-openjdk-${JAVA_ARCH}
endif

all:
	ant -DJavaHome=${JAVA_HOME}

clean:
	ant -DJavaHome=${JAVA_HOME} clean
