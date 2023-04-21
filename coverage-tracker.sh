#!/bin/bash

echo "PID: $$"
trap 'kill -9 $$' SIGINT

npm install bignumber.js

javac -cp src/main/java/ -d ./out/ src/main/java/Main/GenerateProgram.java src/main/java/Main/CompareOutputs.java

rm -rf outputs || true
rm -rf tests || true
rm -rf errors || true

mkdir tests || true
mkdir outputs || true
mkdir errors || true


directory=$(pwd)

cd src/main/dafny_compiler/dafny/Binaries
dafny_binary=$(pwd)

cd "$directory"

t=180
x=60
while [ $x -le 60 ]; do
  cd "$directory"

  echo "Test number $x"

  cd "$directory"
  timeout $t java -cp out/ Main.GenerateProgram $x
  if [ $? -ne 0 ]
  then
    echo "Failed to create dafny file in $t seconds"
    x=$(( $x + 1 ))
    continue;
  fi
#  cp test.dfy tests/"test$x.dfy"
  # css
  #./src/main/dafny_compiler/dafny/Binaries/Dafny /noVerify /compileTarget:cs /spillTargetCode:3 test.dfy

  y=0
  for file in tests/*.dfy
  do
    echo "Attempting to run $file"

    # GO
    cd "$dafny_binary"
    echo "coverlet . --target \"dotnet\" --targetargs \"Dafny.dll /noVerify /compile:2 /compileTarget:go ../../../../../"$file" /out:/tmp/dv\" -f json --merge-with \"../../../../../coverage/\"" | bash
    timeout $t ./src/main/dafny_compiler/dafny/Binaries/Dafny /noVerify /compileTarget:go /compile:2 /compileVerbose:0 test.dfy
    if [ $? -eq 0 ]
    then
      cd "$directory"
      echo "Created Go files"
      ./test > "outputs/output-go-$y.txt"
      rm -rf test-go || true
      rm -rf test || true
    else
      echo "Failed to convert to Go in $t seconds"
    fi


    # js
    cd "$directory"
    timeout $t ./src/main/dafny_compiler/dafny/Binaries/Dafny /noVerify /compileTarget:js /compile:2 /compileVerbose:0 test.dfy > tmp.txt 2>&1
    if [ $? -eq 0 ]
    then
      echo "Created JS files"
      node test.js > "outputs/output-js-$y.txt"
      rm -rf test.js || true
    else
      echo "Failed to convert to JS in $t seconds"
    fi

    # java
    cd "$directory"
    timeout $t ./src/main/dafny_compiler/dafny/Binaries/Dafny /noVerify /compileTarget:java /compile:2 /compileVerbose:0 test.dfy  > tmp.txt 2>&1
    if [ $? -eq 0 ]
    then
      echo "Created Java files"
      java -jar test.jar > "outputs/output-java-$y.txt"
      rm -rf test.jar || true
      rm -rf test-java || true
    else
      echo "Failed to convert to Java in $t seconds"
    fi

    # py
    cd "$directory"
    timeout $t ./src/main/dafny_compiler/dafny/Binaries/Dafny /noVerify /compileTarget:py /compile:2 /compileVerbose:0 test.dfy > tmp.txt  2>&1
    if [ $? -eq 0 ]
    then
      echo "Created Python files"
      python3 test-py/test.py > "outputs/output-py-$y.txt"
      rm -rf test-py || true
    else
      echo "Failed to convert to Python in $t seconds"
    fi

    # cpp
  #  ./src/main/dafny_compiler/dafny/Binaries/Dafny /noVerify /compileTarget:cpp /spillTargetCode:3 test.dfy > tmp.txt 2>&1
  #  echo "Created C++ files"
  #  g++ test.cpp test.h DafnyRuntime.h  > tmp.txt 2>&1
  #  ./a.out > outputs/output-cpp.txt
    rm -rf test.dfy || true
    rm -rf tmp.txt || true
    y=$(( $y + 1))
    cd "$directory"
  done
  java -cp out/ Main.CompareOutputs $x "./outputs"
  if [ $? -eq 1 ]
  then
    mkdir "errors/$x"
    mkdir "errors/$x/outputs"
    mkdir "errors/$x/tests"
    cp outputs/* "errors/$x/outputs"
    cp tests/* "errors/$x/tests"
  fi

  rm -rf tests/* || true
  rm -rf outputs/* || true
  x=$(( $x + 1 ))

done

