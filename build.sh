#!/bin/bash

# Define RELEASE=1 before running to build a release.

echo "Downloading and updating compiler..."
if git clone https://github.com/Feodor0090/j2me_compiler.git 2>/dev/null ; then
	echo "Done."
else
	echo "Already downloaded."
fi
cd j2me_compiler
git pull
cd ..

cp Application\ Descriptor manifest.mf

WORK_DIR=`dirname $0`
cd ${WORK_DIR}

chmod +x ./build_sub.sh
mkdir -p jar

if [[ ${RELEASE} == "1" ]]
then
  echo Filtering sid data...
  for file in `find ./ -type f -name "*.java"`
    do cat $file | grep -v "?sid" > ./temp.txt
    cat ./temp.txt > $file
  done
  rm ./temp.txt
else
  echo -en "Commit: " >> manifest.mf
  git rev-parse --short HEAD >> manifest.mf
fi

APP=nmania_debug ./build_sub.sh

echo Filtering debug data...
for file in `find ./ -type f -name "*.java"`
	do cat $file | grep -v "GL.Log" | grep -v "nmania.GL" | grep -v "?dbg" > ./temp.txt
	cat ./temp.txt > $file
done
rm ./temp.txt

APP=nmania ./build_sub.sh

echo Filtering full data...
for file in `find ./ -type f -name "*.java"`
	do cat $file | grep -v "// ?full" > ./temp.txt
	cat ./temp.txt > $file
done
rm ./temp.txt
rm ./res/sfx/*

APP=nmania_lite ./build_sub.sh


cp nmania.jar ./jar/nmania.jar
cp nmania_debug.jar ./jar/nmania_debug.jar
cp nmania_lite.jar ./jar/nmania_lite.jar
