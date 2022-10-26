#!/bin/sh -e

# STATIC VARS
JAVA_HOME=./j2me_compiler/jdk1.6.0_45
WTK_HOME=./j2me_compiler/WTK2.5.2
PROGUARD=./j2me_compiler/proguard/bin/proguard.sh
RES=res
MANIFEST=manifest.mf
PATHSEP=":"
JAVAC=javac
JAR=jar

# DYNAMIC VARS
LIB_DIR=${WTK_HOME}/lib
CLDCAPI=${LIB_DIR}/cldcapi11.jar
MIDPAPI=${LIB_DIR}/midpapi20.jar
PREVERIFY=${WTK_HOME}/bin/preverify
TCP=${LIB_DIR}/*
CLASSPATH=`echo $TCP | sed "s/ /:/g"`
PGLIBS=`echo $TCP | sed "s/ /;/g"`

if [ -n "${JAVA_HOME}" ] ; then
  JAVAC=${JAVA_HOME}/bin/javac
  JAR=${JAVA_HOME}/bin/jar
fi

# ACTION
echo "Working on" ${APP}
pwd
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

echo "Build done!" ./${APP}.jar
cp ./${APP}.jar ./jar/${APP}.jar

echo "Optimizing..."
chmod +x ${PROGUARD}
touch cf.cfg

cat proguard.basecfg > cf.cfg
echo "-injars ./${APP}.jar" >> cf.cfg
echo "-outjar ./jar/${APP}_opt.jar" >> cf.cfg
echo "-printseeds ./jar/${APP}_opt_seeds.txt" >> cf.cfg
echo "-printmapping ./jar/${APP}_opt_map.txt" >> cf.cfg
echo "-libraryjars ${PGLIBS}" >> cf.cfg

${PROGUARD} @cf.cfg

cat proguard.basecfg > cf.cfg
echo "-injars ./${APP}.jar" >> cf.cfg
echo "-outjar ./jar/${APP}_obf.jar" >> cf.cfg
echo "-printseeds ./jar/${APP}_obf_seeds.txt" >> cf.cfg
echo "-printmapping ./jar/${APP}_obf_map.txt" >> cf.cfg
echo "-libraryjars ${CLASSPATH}" >> cf.cfg
echo "-dontoptimize" >> cf.cfg

${PROGUARD} @cf.cfg