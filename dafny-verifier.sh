#!/bin/bash

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

npm install bignumber.js



mkdir outputs || true
mkdir errors || true

x=1
while [ $x -le 2 ]; do

  echo "Test number $x"

  java -javaagent:/home/dilan/.local/share/JetBrains/Toolbox/apps/IDEA-U/ch-0/212.5457.46/lib/idea_rt.jar=46671:/home/dilan/.local/share/JetBrains/Toolbox/apps/IDEA-U/ch-0/212.5457.46/bin -Dfile.encoding=UTF-8 -classpath /home/dilan/dafny-verifier/target/classes Main.GenerateProgram > test.dfy

  # css
  #./src/main/dafny_compiler/dafny/Binaries/Dafny /noVerify /compileTarget:cs /spillTargetCode:3 test.dfy

  # GO
  ./src/main/dafny_compiler/dafny/Binaries/Dafny /noVerify /compileTarget:go /spillTargetCode:3 test.dfy > tmp.txt 2>&1
  echo "Created Go files"
  mkdir test-go-run
  cp -R test-go/* test-go-run/
  cd test-go-run/src
  go mod init src  > tmp.txt 2>&1
  find . \( -type d -name .git -prune \) -o -type f -print0 | xargs -0 sed -i 's/_System "System_"/_System "src\/System_"/g'  > tmp.txt 2>&1
  find . \( -type d -name .git -prune \) -o -type f -print0 | xargs -0 sed -i 's/_dafny "dafny"/_dafny "src\/dafny"/g'  > tmp.txt 2>&1
  go build test.go  > tmp.txt 2>&1
  cd ../..
  ./test-go-run/src/test > outputs/output-go.txt
  rm -rf test-go-run || true


  # js
  ./src/main/dafny_compiler/dafny/Binaries/Dafny /noVerify /compileTarget:js /spillTargetCode:3 test.dfy > tmp.txt 2>&1
  echo "Created JS files"
  node test.js > outputs/output-js.txt

  # java
  ./src/main/dafny_compiler/dafny/Binaries/Dafny /noVerify /compileTarget:java /spillTargetCode:3 test.dfy > tmp.txt 2>&1
  echo "Created Java files"
  javac -cp src/main/dafny_compiler/dafny/Binaries/DafnyRuntime.jar test-java/test.java test-java/*/*.java > tmp.txt 2>&1
  cd test-java
  java -cp ../src/main/dafny_compiler/dafny/Binaries/DafnyRuntime.jar:. test > ../outputs/output-java.txt
  cd ..

  # py
  ./src/main/dafny_compiler/dafny/Binaries/Dafny /noVerify /compileTarget:py /spillTargetCode:3 test.dfy > tmp.txt  2>&1
  echo "Created Python files"
  python3 test-py/test.py > outputs/output-py.txt

  # cpp
  ./src/main/dafny_compiler/dafny/Binaries/Dafny /noVerify /compileTarget:cpp /spillTargetCode:3 test.dfy > tmp.txt 2>&1
  echo "Created C++ files"
  g++ test.cpp test.h DafnyRuntime.h  > tmp.txt 2>&1
  ./a.out > outputs/output-cpp.txt

  rm -rf test-go test-go-run || true
  rm -rf test-java || true
  rm -rf test.cpp test.h DafnyRuntime.h test.h.gch DafnyRuntime.h.gch a.out || true
  rm -rf test.cs || true
  rm -rf test-py || true
  rm -rf test.js || true
  rm -rf test.a || true
  rm -rf tmp.txt || true

  java -javaagent:/home/dilan/.local/share/JetBrains/Toolbox/apps/IDEA-U/ch-0/212.5457.46/lib/idea_rt.jar=40973:/home/dilan/.local/share/JetBrains/Toolbox/apps/IDEA-U/ch-0/212.5457.46/bin -Dfile.encoding=UTF-8 -classpath /home/dilan/dafny-verifier/target/classes Main.CompareOutputs $x

  rm -rf outputs/*

  x=$(( $x + 1 ))

done