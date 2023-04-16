#!/bin/bash

echo $@
exit 0

echo "PID: $$"
trap 'kill -9 $$' SIGINT

orig=$(pwd)
directory="/home/dilan/dafny-verifier"

cd $directory
javac -cp src/main/java/ -d ./out/ src/main/java/Main/GenerateProgram.java src/main/java/Main/CompareOutputs.java


Dafny /noVerify /compile:3 /compileVerbose:0 test.dfy
if [ $? -ne 0 ]
then
  cd $orig
  exit 1
fi

timeout $t ./src/main/dafny_compiler/dafny/Binaries/Dafny /noVerify /compileTarget:go /spillTargetCode:3 test.dfy > tmp.txt 2>&1
