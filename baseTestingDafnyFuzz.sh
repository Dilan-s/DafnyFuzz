#!/bin/bash

echo "PID: $$"
trap 'kill -9 $$' SIGINT

javac -cp src/main/java/ -d ./out/ src/main/java/Main/BaseProgram.java src/main/java/Main/CompareOutputs.java

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

t=180

#verrors=(244331	254096	257847	258535	262558	266809	267919	269773	278997	282797	284046)
#for z in ${verrors[@]}; do
#  x=$z
x=0
while [ true ]; do
    cd "$directory"

    echo "Test number $x"
    timeout -s SIGKILL $t java -cp out/ Main.BaseProgram $x
    if [ $? -ne 0 ]
    then
        echo "Failed to create dafny file in $t seconds"
        x=$(( $x + 1 ))
        continue;
    fi

    cd "$directory"
    y=0
    if [ "$(ls -A tests/)" ];
    then
        for file in tests/*.dfy
        do
            echo "Attempting to run $file in $language"
            cp "$file" test.dfy

            ./singleFileTest.sh -l cs -n $x -f $y -t $t
            ./singleFileTest.sh -l py -n $x -f $y -t $t
            ./singleFileTest.sh -l go -n $x -f $y -t $t
            ./singleFileTest.sh -l js -n $x -f $y -t $t
            ./singleFileTest.sh -l java -n $x -f $y -t $t

            rm -rf test.dfy
            y=$(( y + 1 ))
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

    x=$(( x + 1 ))
done
