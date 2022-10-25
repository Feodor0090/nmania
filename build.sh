#!/bin/sh -e

echo "Downloading and updating compiler..."
if git clone https://github.com/vipaoL/j2me_compiler.git 2>/dev/null ; then
	echo "Done."
else
	echo "Already downloaded."
fi
cd j2me_compiler
git pull
cd ..
PATHSEP=":"
JAVA_HOME=./j2me_compiler/jdk1.6.0_45
WTK_HOME=./j2me_compiler/WTK2.5.2

cp Application\ Descriptor manifest.mf

######## CONFIG ########
RES=res
MANIFEST=manifest.mf
LIB_DIR=${WTK_HOME}/lib
TCP=${LIB_DIR}/*
CLDCAPI=${LIB_DIR}/cldcapi11.jar
MIDPAPI=${LIB_DIR}/midpapi20.jar
PREVERIFY=${WTK_HOME}/bin/preverify
JAVAC=javac
JAR=jar
CLASSPATH=`echo $TCP | sed "s/ /:/g"`

if [ -n "${JAVA_HOME}" ] ; then
  JAVAC=${JAVA_HOME}/bin/javac
  JAR=${JAVA_HOME}/bin/jar
fi

WORK_DIR=`dirname $0`
cd ${WORK_DIR}

chmod +x ./build_sub.sh

APP=nmania_debug   # Output jar name

./build_sub.sh

# filtering debug data
IFS=$'\n'
for file in `find ./ -type f -name "*"` do 
	cat $file | grep -v "GL.Log" | grep -v "// ?dbg" > temp.txt
	temp.txt > $file
done
rm ./temp.txt

APP=nmania

./build_sub.sh

# filtering full data
IFS=$'\n'
for file in `find ./ -type f -name "*"` do 
	cat $file | grep -v "// ?lite" > temp.txt
	temp.txt > $file
done
rm ./temp.txt
rm ./res/sfx/*

APP=nmania_lite

./build_sub.sh

mkdir -p jar
cp nmania.jar ./jar/nmania.jar
cp nmania_debug.jar ./jar/nmania_debug.jar
cp nmania_lite.jar ./jar/nmania_lite.jar