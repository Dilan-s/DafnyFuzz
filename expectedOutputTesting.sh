#!/bin/bash

echo "PID: $$"
trap 'kill -9 $$' SIGINT

while getopts l: flag
do
    case "${flag}" in
        l) language=${OPTARG};;
    esac
done

javac -cp src/main/java/ -d ./out/ src/main/java/Main/ExpectedProgram.java src/main/java/Main/CompareOutputs.java

rm -rf outputs || true
rm -rf tests || true
rm -rf errors || true

mkdir outputs || true
mkdir errors || true
mkdir errors/compErrors
touch "errors/compErrors/$language.txt"

directory=$(pwd)

t=180

x=0
while [ true ]; do
    cd "$directory"

    echo "Test number $x"
    timeout -s SIGKILL $t java -cp out/ Main.ExpectedProgram $x
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

            ./singleFileTest.sh -l $language -n $x -f $y -t $t

            rm -rf test.dfy
            y=$(( y + 1 ))
        done
    fi

    java -cp out/ Main.CompareOutputs $x "./outputs"
    if [ $? -eq 1 ]
    then
	echo "Test number $x" >> "errors/compErrors/$language.txt"
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
