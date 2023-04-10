#!/bin/bash

echo "PID: $$"
trap 'kill -9 $$' SIGINT

rm -rf test.dfy || true
rm -rf test-go test-go-run || true
rm -rf test-java || true
rm -rf test.cpp test.h DafnyRuntime.h || true
rm -rf test.cs || true
rm -rf test-py || true
rm -rf test.js || true
rm -rf outputs || true
rm -rf test.a || true
rm -rf errors || true
rm -rf tmp.txt || true
rm -rf out || true
rm -rf tests || true

npm install bignumber.js

javac -cp src/main/java/ -d ./out/ src/main/java/Main/GenerateProgram.java src/main/java/Main/CompareOutputs.java


mkdir outputs || true
mkdir errors || true

directory=$(pwd)

t=180
x=1
while [ true ]; do
  cd "$directory"

  rm -rf test-go test-go-run || true
  rm -rf test-java || true
  rm -rf test.cpp test.h DafnyRuntime.h test.h.gch DafnyRuntime.h.gch a.out || true
  rm -rf test.cs || true
  rm -rf test-py || true
  rm -rf test.js || true
  rm -rf test.a || true
  rm -rf tmp.txt || true
  rm -rf outputs/* || true
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

  cd "$directory"
  y=0
  for file in tests/*.dfy
  do
    cp "$file" test.dfy
  # GO
    cd "$directory"
    timeout $t ./src/main/dafny_compiler/dafny/Binaries/Dafny /noVerify /compileTarget:go /spillTargetCode:3 test.dfy > tmp.txt 2>&1
    if [ $? -eq 0 ]
    then
      echo "Created Go files"
      mkdir test-go-run
      cp -R test-go/* test-go-run/
      cd test-go-run/src
      go mod init src  > tmp.txt 2>&1
      cd ..
      find ./src \( -type d -name .git -prune \) -o -type f -print0 | xargs -0 sed -i 's/_System "System_"/_System "src\/System_"/g'  > tmp.txt 2>&1
      find ./src \( -type d -name .git -prune \) -o -type f -print0 | xargs -0 sed -i 's/_dafny "dafny"/_dafny "src\/dafny"/g'  > tmp.txt 2>&1
      cd src
      go build test.go  > tmp.txt 2>&1
      if [ $? -eq 0 ]
      then
        cd ../..
        ./test-go-run/src/test > "outputs/output-go-$y.txt"
        rm -rf test-go-run || true
      else
        echo "Failed to generate Go program"
      fi
    else
      echo "Failed to convert to GO in $t seconds"
    fi


    # js
    cd "$directory"
    timeout $t ./src/main/dafny_compiler/dafny/Binaries/Dafny /noVerify /compileTarget:js /spillTargetCode:3 test.dfy > tmp.txt 2>&1
    if [ $? -eq 0 ]
    then
      echo "Created JS files"
      node test.js > "outputs/output-js-$y.txt"
    else
      echo "Failed to convert to JS in $t seconds"
    fi

    # java
    cd "$directory"
    timeout $t ./src/main/dafny_compiler/dafny/Binaries/Dafny /noVerify /compileTarget:java /spillTargetCode:3 test.dfy > tmp.txt 2>&1
    if [ $? -eq 0 ]
    then
      echo "Created Java files"
      javac -cp src/main/dafny_compiler/dafny/Binaries/DafnyRuntime.jar test-java/test.java test-java/*/*.java > tmp.txt 2>&1
      if [ $? -eq 0 ]
      then
        cd test-java
        java -cp ../src/main/dafny_compiler/dafny/Binaries/DafnyRuntime.jar:. test > "../outputs/output-java-$y.txt"
        cd ..
      else
        echo "Failed to generate Java program"
      fi
    else
      echo "Failed to convert to Java in $t seconds"
    fi

    # py
    cd "$directory"
    timeout $t ./src/main/dafny_compiler/dafny/Binaries/Dafny /noVerify /compileTarget:py /spillTargetCode:3 test.dfy > tmp.txt  2>&1
    if [ $? -eq 0 ]
    then
      echo "Created Python files"
      python3 test-py/test.py > "outputs/output-py-$y.txt"
    else
      echo "Failed to convert to Python in $t seconds"
    fi

    # cpp
  #  ./src/main/dafny_compiler/dafny/Binaries/Dafny /noVerify /compileTarget:cpp /spillTargetCode:3 test.dfy > tmp.txt 2>&1
  #  echo "Created C++ files"
  #  g++ test.cpp test.h DafnyRuntime.h  > tmp.txt 2>&1
  #  ./a.out > outputs/output-cpp.txt
    rm -rf test.dfy || true
    y=$(( $y + 1))
  done
  java -cp out/ Main.CompareOutputs $x
  x=$(( $x + 1 ))

  rm -rf test-go test-go-run || true
  rm -rf test-java || true
  rm -rf test.cpp test.h DafnyRuntime.h test.h.gch DafnyRuntime.h.gch a.out || true
  rm -rf test.cs || true
  rm -rf test-py || true
  rm -rf test.js || true
  rm -rf test.a || true
  rm -rf tests || true
  rm -rf tmp.txt || true
  rm -rf outputs/* || true



done

