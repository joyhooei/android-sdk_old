#!/bin/sh

# usage: merge_dex.sh file.dex file1.jar file2.jar ...

dexfile=$1

echo 'dex2jar'
d2j-dex2jar.sh $dexfile -o tmp.jar

echo 'merge jar'
rm -r tmp
mkdir tmp
cd tmp

shift
while test ${#} -gt 0
do
  echo $1
  jar -xf ../$1
  shift
done

cd ..
jar -cvf tmp.jar -C tmp .
rm -r tmp

echo 'jar2dex'
d2j-jar2dex.sh tmp.jar -o $dexfile
