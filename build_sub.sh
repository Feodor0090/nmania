#!/bin/sh -e

# VARS
RES=res
MANIFEST=manifest.mf
LIB_DIR=${WTK_HOME}/lib
CLDCAPI=${LIB_DIR}/cldcapi11.jar
MIDPAPI=${LIB_DIR}/midpapi20.jar
PREVERIFY=${WTK_HOME}/bin/preverify
PATHSEP=":"
JAVA_HOME=./j2me_compiler/jdk1.6.0_45
WTK_HOME=./j2me_compiler/WTK2.5.2
JAVAC=javac
JAR=jar
TCP=${LIB_DIR}/*
CLASSPATH=`echo $TCP | sed "s/ /:/g"`

if [ -n "${JAVA_HOME}" ] ; then
  JAVAC=${JAVA_HOME}/bin/javac
  JAR=${JAVA_HOME}/bin/jar
fi

# ACTION
echo "Working on" ${APP}
echo "Creating or cleaning directories..."
mkdir -p ./tmpclasses
mkdir -p ./classes
rm -rf ./tmpclasses/*
rm -rf ./classes/*

echo "Compiling source files..."
${JAVAC} \
    -bootclasspath ${CLDCAPI}${PATHSEP}${MIDPAPI} \
    -source 1.3 \
    -target 1.3 \
    -d ./tmpclasses \
    -classpath ./tmpclasses${PATHSEP}${CLASSPATH} \
    `find ./src -name '*'.java`
echo $CLASSPATH
echo "Preverifying class files..."
${PREVERIFY} \
    -classpath ${CLDCAPI}${PATHSEP}${MIDPAPI}${PATHSEP}${CLASSPATH}${PATHSEP}./tmpclasses \
    -d ./classes \
    ./tmpclasses

echo "Jaring preverified class files..."
${JAR} cmf ${MANIFEST} ${APP}.jar -C ./classes .

if [ -d ${RES} ] ; then
  ${JAR} uf ${APP}.jar -C ${RES} .
fi

echo "Done!" ./${APP}.jar