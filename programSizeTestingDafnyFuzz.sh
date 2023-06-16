#!/bin/bash

echo "PID: $$"
trap 'kill -9 $$' SIGINT

javac -cp src/main/java/ -d ./out/ src/main/java/Main/BaseProgram.java

rm -rf tests || true

directory=$(pwd)
t=180

x=0
sum=0

num=1000
while [ $x -lt $num ]; do
    cd "$directory"

    echo "Test number $x"
    timeout -s SIGKILL $t java -cp out/ Main.BaseProgram $x
    if [ $? -ne 0 ]
    then
        x=$(( $x + 1 ))
        continue;
    fi

    lines=$(wc -l < tests/test.dfy)
    sum=$(( $sum + $lines ))

    rm -rf tests/* || true

    x=$(( x + 1 ))
done

echo $(($sum / $num))