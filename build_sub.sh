#!/bin/sh -e

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