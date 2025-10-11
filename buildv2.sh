#!/bin/bash

rm -rf "bin"
mkdir "bin"
cp -r "src" "bin/src"
cp -r "res" "bin/res"
cp "Application Descriptor" "bin/Application Descriptor"
cp "proguard.cfg" "bin/proguard.cfg"
cp ".classpath" "bin/.classpath"

cd bin || exit

if [[ ${RELEASE} == "1" ]]
then
  echo Filtering sid data...
  for file in `find ./ -type f -name "*.java"`
  do
    if grep -q "??sidonly" "$file"
    then
      rm "$file"
    else
      cat "$file" | grep -v "?sid" > ./temp.txt
      cat ./temp.txt > "$file"
    fi
  done
  for file in `find ./ -type f -name "*.java"`
  do
    if grep -q "??dbgonly" $file
    then
      rm "$file"
    else
      cat "$file" | grep -v "GL.Log" | grep -v "nmania.GL" | grep -v "?dbg" > ./temp.txt
      cat ./temp.txt > "$file"
    fi
  done
  rm ./temp.txt
  jarname="nmania_obf.jar"
  kembuild --obf
else
  echo -en "Commit: " >> "Application Descriptor"
  git rev-parse --short HEAD >> "Application Descriptor"
  jarname="nmania_sid.jar"
  kembuild
fi

cd ..
mv bin/deployed/bin.jar "$jarname"
rm -rf "bin"
mkdir "bin"
