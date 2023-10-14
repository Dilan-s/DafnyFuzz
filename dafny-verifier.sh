#!/bin/bash

echo "PID: $$"
trap 'kill -9 $$' SIGINT

npm install bignumber.js

javac -cp src/main/java/ -d ./out/ src/main/java/Main/GenerateProgram.java src/main/java/Main/CompareOutputs.java

rm -rf outputs || true
rm -rf tests || true
rm -rf errors || true
rm -rf tests-minimized || true
rm -rf tests-incorrect || true

mkdir tests || true
mkdir tests-minimized || true
mkdir tests-incorrect || true
mkdir outputs || true
mkdir errors || true
mkdir errors/verificationErrors
mkdir errors/compErrors
touch errors/compErrors/go.txt
touch errors/compErrors/java.txt
touch errors/compErrors/js.txt
touch errors/compErrors/py.txt
touch errors/compErrors/cs.txt



directory=$(pwd)

t=180
x=0
while [ true ]; do
  cd "$directory"

  echo "Test number $x"
  echo "Test number $x" >> errors/compErrors/go.txt
  echo "Test number $x" >> errors/compErrors/js.txt
  echo "Test number $x" >> errors/compErrors/java.txt
  echo "Test number $x" >> errors/compErrors/py.txt
  echo "Test number $x" >> errors/compErrors/cs.txt

  cd "$directory"
  timeout -s SIGKILL $t java -cp out/ Main.GenerateProgram $x
  if [ $? -ne 0 ]
  then
    echo "Failed to create dafny file in $t seconds"
    x=$(( $x + 1 ))
    continue;
  fi

  cd "$directory"
  y=0
  if [ "$(ls -A tests-minimized/)" ];
  then
    for file in tests-minimized/*.dfy
    do
      echo "Expecting validation to succeed for $file"

      timeout -s SIGKILL $t Dafny verify $file > tmp.txt 2>&1
      if [ $? -eq 4 ]
      then
        echo "Verification error found in test $x - correct validation of the file $file"
        mkdir "errors/verificationErrors/$x" || true
        mkdir "errors/verificationErrors/$x/correct" || true
        cp $file "errors/verificationErrors/$x/correct/test-$y.dfy"
        cat tmp.txt > "errors/verificationErrors/$x/correct/verificationOutput-$y.txt"
      fi
      rm -rf tmp.txt || true
      y=$(( $y + 1))
    done
  fi

  cd "$directory"
  y=0
  if [ "$(ls -A tests-incorrect/)" ];
  then
    for file in tests-incorrect/*.dfy
    do
      echo "Expecting validation to fail for $file"

      timeout -s SIGKILL $t Dafny verify $file > tmp.txt 2>&1
      code=$?
      if [[ $code -ne 4 && $code -lt 5 ]]
      then
        echo "Verification error found in test $x - incorrect validation of file $file"
        mkdir "errors/verificationErrors/$x" || true
        mkdir "errors/verificationErrors/$x/incorrect" || true
        cp $file "errors/verificationErrors/$x/incorrect/test-$y.dfy"
        cat tmp.txt > "errors/verificationErrors/$x/incorrect/verificationOutput-$y.txt"
      fi
      rm -rf tmp.txt || true
      y=$(( $y + 1))
    done
  fi

  cd "$directory"
  y=0
  if [ "$(ls -A tests/)" ];
  then
    for file in tests/*.dfy
    do
      echo "Attempting to run $file"
      cp "$file" test.dfy

      # GO
      cd "$directory"
      ./singleFileTest.sh -l go -n $x -f $y -t $t

      # js
      ./singleFileTest.sh -l js -n $x -f $y -t $t

      # java
      ./singleFileTest.sh -l java -n $x -f $y -t $t

      # py
      ./singleFileTest.sh -l py -n $x -f $y -t $t

      # cs
      ./singleFileTest.sh -l cs -n $x -f $y -t $t

      rm -rf test.dfy || true
      y=$(( $y + 1))
    done
  fi
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
  rm -rf tests-minimized/* || true
  rm -rf tests-incorrect/* || true
  x=$(( $x + 1 ))

done

rm -rf test.dfy tmp.txt || true