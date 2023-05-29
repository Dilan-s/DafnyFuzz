#!/bin/bash

echo "PID: $$"
trap 'kill -9 $$' SIGINT

npm install bignumber.js

rm -rf outputs || true
rm -rf errors || true
rm -rf tests || true
rm -rf tests-minimized || true
rm -rf tests-incorrect || true

mkdir outputs || true
mkdir errors || true
mkdir tests || true
mkdir tests-minimized || true
mkdir tests-incorrect || true


javac -cp src/main/java/ -d ./out/ src/main/java/Main/GenerateProgram.java src/main/java/Main/CompareOutputs.java

directory=$(pwd)

cd src/main/dafny_compiler/dafny/Binaries
dafny_binary=$(pwd)

cd "$directory"

x=0
while [ $x -le 0 ]; do
  cd "$directory"

  echo "Test number $x"
  java -cp out/ Main.GenerateProgram $x
  if [ $? -ne 0 ]
  then
    echo "Failed to create dafny file in $t seconds"
    x=$(( $x + 1 ))
    continue;
  fi
  # css
  #./src/main/dafny_compiler/dafny/Binaries/Dafny /noVerify /compileTarget:cs /spillTargetCode:3 test.dfy
  
  for file in tests/*.dfy
  do
    echo "Attempting to run $file"
    cp $file test.dfy
    cp test.dfy "$dafny_binary/test.dfy"

    cd "$dafny_binary"
    echo "Verify File"
    coverlet . --target dotnet --targetargs "Dafny.dll verify test.dfy" -f cobertura -f json --merge-with coverage.json

    # GO
    cd "$dafny_binary"
    echo "GO File"
    coverlet . --target dotnet --targetargs "Dafny.dll /deleteCodeAfterRun:1 /compile:4 /noVerify /compileTarget:go test.dfy" -f cobertura -f json --merge-with coverage.json

    # js
    cd "$dafny_binary"
    echo "JS File"
    coverlet . --target dotnet --targetargs "Dafny.dll /deleteCodeAfterRun:1 /compile:4 /noVerify /compileTarget:js test.dfy" -f cobertura -f json --merge-with coverage.json

    # java
    cd "$dafny_binary"
    echo "JAVA File"
    coverlet . --target dotnet --targetargs "Dafny.dll /deleteCodeAfterRun:1 /compile:4 /noVerify /compileTarget:java test.dfy" -f cobertura -f json --merge-with coverage.json

    # py
    cd "$dafny_binary"
    echo "PY File"
    coverlet . --target dotnet --targetargs "Dafny.dll /deleteCodeAfterRun:1 /compile:4 /noVerify /compileTarget:py test.dfy" -f cobertura -f json --merge-with coverage.json

    # cs
    cd "$dafny_binary"
    echo "CS File"
    coverlet . --target dotnet --targetargs "Dafny.dll /deleteCodeAfterRun:1 /compile:4 /noVerify /compileTarget:cs test.dfy" -f cobertura -f json --merge-with coverage.json


    cd "$directory"
    rm -rf test.dfy "$dafny_binary/test.dfy" || true

  done

  cd "$dafny_binary"
  cp coverage.cobertura.xml "$directory/coverage/"

  rm -rf tests/* || true
  x=$(( $x + 1 ))

done

