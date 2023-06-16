#!/bin/bash

echo "PID: $$"
trap 'kill -9 $$' SIGINT

javac -cp src/main/java/ -d ./out/ src/main/java/Main/CompareOutputs.java

rm -rf outputs || true
rm -rf tests || true
rm -rf errors || true

mkdir outputs || true
mkdir errors || true
mkdir errors/compErrors
touch "errors/compErrors/cs.txt"
touch "errors/compErrors/py.txt"
touch "errors/compErrors/go.txt"
touch "errors/compErrors/js.txt"
touch "errors/compErrors/java.txt"

directory=$(pwd)
cd src/main/dafny_compiler/dafny/Binaries
dafny_dir=$(pwd)
cd "$directory"
cd src/main/xdsmith/xdsmith/work-dir
xd_dir=$(pwd)

cd "$directory"
t=180

x=0
while [ true ]; do
    cd "$directory"

    cd "$xd_dir"
    echo "Test number $x"
    timeout --foreground 300 racket ../xdsmith/fuzzer.rkt --timeout 300 --dafny-syntax true --seed $x --with-print-constrained true > test.dfy
    if [ $? -ne 0 ]
    then
        echo "Failed to create dafny file in $t seconds"
        x=$(( $x + 1 ))
        continue;
    fi

    cd "$directory"
    cp "$xd_dir/test.dfy" "$directory/test.dfy"
    y=0
    ./singleFileTest.sh -l cs -n $x -f $y -t $t
    ./singleFileTest.sh -l py -n $x -f $y -t $t
    ./singleFileTest.sh -l go -n $x -f $y -t $t
    ./singleFileTest.sh -l js -n $x -f $y -t $t
    ./singleFileTest.sh -l java -n $x -f $y -t $t
    rm -rf test.dfy

    java -cp out/ Main.CompareOutputs $x "./outputs"
    if [ $? -eq 1 ]
    then
        mkdir "errors/$x"
        mkdir "errors/$x/outputs"
        mkdir "errors/$x/tests"
        cp outputs/* "errors/$x/outputs"
        cp "$xd_dir/test.dfy" "errors/$x/tests/"
    fi

    rm -rf outputs/* || true
    rm -rf "$xd_dir/test.dfy" "$directory/test.dfy"

    x=$(( x + 1 ))
done
