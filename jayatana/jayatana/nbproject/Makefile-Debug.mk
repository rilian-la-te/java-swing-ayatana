#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
GREP=grep
NM=nm
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++
CXX=g++
FC=gfortran
AS=as

# Macros
CND_PLATFORM=GNU-Linux-x86
CND_CONF=Debug
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/ayatana_JVM.o \
	${OBJECTDIR}/org_java_ayatana_ApplicationMenu.o \
	${OBJECTDIR}/ayatana_Collections.o \
	${OBJECTDIR}/org_java_ayatana_JKeyToXKey.o \
	${OBJECTDIR}/org_java_ayatana_GMainLoop.o


# C Compiler Flags
CFLAGS=`pkg-config --cflags glib-2.0 gio-2.0 dbusmenu-glib-0.4 xt` 

# CC Compiler Flags
CCFLAGS=
CXXFLAGS=

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=-L${JAVA_HOME}/jre/lib/${JAVA_ARCH} -ljawt `pkg-config --libs glib-2.0 gio-2.0 dbusmenu-glib-0.4 xt`  

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libjayatana.so

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libjayatana.so: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${LINK.c} -shared -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libjayatana.so -fPIC ${OBJECTFILES} ${LDLIBSOPTIONS} 

${OBJECTDIR}/ayatana_JVM.o: ayatana_JVM.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.c) -g -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux `pkg-config --cflags glib-2.0 gio-2.0 dbusmenu-glib-0.4 xt`    -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/ayatana_JVM.o ayatana_JVM.c

${OBJECTDIR}/org_java_ayatana_ApplicationMenu.o: org_java_ayatana_ApplicationMenu.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.c) -g -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux `pkg-config --cflags glib-2.0 gio-2.0 dbusmenu-glib-0.4 xt`    -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/org_java_ayatana_ApplicationMenu.o org_java_ayatana_ApplicationMenu.c

${OBJECTDIR}/ayatana_Collections.o: ayatana_Collections.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.c) -g -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux `pkg-config --cflags glib-2.0 gio-2.0 dbusmenu-glib-0.4 xt`    -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/ayatana_Collections.o ayatana_Collections.c

${OBJECTDIR}/org_java_ayatana_JKeyToXKey.o: org_java_ayatana_JKeyToXKey.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.c) -g -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux `pkg-config --cflags glib-2.0 gio-2.0 dbusmenu-glib-0.4 xt`    -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/org_java_ayatana_JKeyToXKey.o org_java_ayatana_JKeyToXKey.c

${OBJECTDIR}/org_java_ayatana_GMainLoop.o: org_java_ayatana_GMainLoop.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.c) -g -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux `pkg-config --cflags glib-2.0 gio-2.0 dbusmenu-glib-0.4 xt`    -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/org_java_ayatana_GMainLoop.o org_java_ayatana_GMainLoop.c

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libjayatana.so

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
