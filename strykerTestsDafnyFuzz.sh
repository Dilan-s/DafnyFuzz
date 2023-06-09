#!/bin/bash

echo "PID: $$"
trap 'kill -9 $$' SIGINT

npm install bignumber.js

testDir=$(pwd)

while getopts l: flag
do
    case "${flag}" in
        l) testDir=${OPTARG};;
    esac
done

javac -cp src/main/java/ -d ./out/ src/main/java/Main/ExpectedProgramGeneration.java src/main/java/Main/CompareOutputs.java

rm -rf outputs || true
rm -rf tests || true
rm -rf errors || true

mkdir tests || true
mkdir outputs || true
mkdir errors || true

rm -rf "$testDir/StrykerTests" || true
mkdir "$testDir/StrykerTests" || true


t=180
x=0
while [ true ]; do
    exitCode=0
    join='>'

    echo "Test number $x"
    timeout -s SIGKILL $t java -cp out/ Main.ExpectedProgramGeneration $x
    if [ $? -ne 0 ]
    then
        echo "Failed to create dafny file in $t seconds"
        x=$(( $x + 1 ))
        continue;
    fi

    cp tests/test.dfy test.dfy

    successCount=0

    timeout --foreground -s SIGKILL $t Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:cs test.dfy > tmp.txt
    if [ $? -eq 0 ];
    then
        echo "Successfully ran command 'Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:cs test.dfy'"
        echo "// RUN: %dafny /noVerify /deleteCodeAfterRun:1 /compile:4 /compileTarget:cs \"%s\" > \"%t\"" > "$testDir/StrykerTests/test-$x-cs.dfy"
        echo "// RUN: %diff \"%s.expect\" \"%t\"" >> "$testDir/StrykerTests/test-$x-cs.dfy"
        cat test.dfy >> "$testDir/StrykerTests/test-$x-cs.dfy"
        cat tmp.txt > "$testDir/StrykerTests/test-$x-cs.dfy.expect"
    else
        echo "Failed to run command 'Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:cs test.dfy'"
    fi

    timeout --foreground -s SIGKILL $t Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:js test.dfy > tmp.txt
    if [ $? -eq 0 ];
    then
        echo "Successfully ran command 'Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:js test.dfy'"
        echo "// RUN: %dafny /noVerify /deleteCodeAfterRun:1 /compile:4 /compileTarget:js \"%s\" > \"%t\"" > "$testDir/StrykerTests/test-$x-js.dfy"
        echo "// RUN: %diff \"%s.expect\" \"%t\"" >> "$testDir/StrykerTests/test-$x-js.dfy"
        cat test.dfy >> "$testDir/StrykerTests/test-$x-js.dfy"
        cat tmp.txt > "$testDir/StrykerTests/test-$x-js.dfy.expect"
    else
        echo "Failed to run command 'Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:js test.dfy'"
    fi

    timeout --foreground -s SIGKILL $t Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:go test.dfy > tmp.txt
    if [ $? -eq 0 ];
    then
        echo "Successfully ran command 'Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:go test.dfy'"
        echo "// RUN: %dafny /noVerify /deleteCodeAfterRun:1 /compile:4 /compileTarget:go \"%s\" > \"%t\"" > "$testDir/StrykerTests/test-$x-go.dfy"
        echo "// RUN: %diff \"%s.expect\" \"%t\"" >> "$testDir/StrykerTests/test-$x-go.dfy"
        cat test.dfy >> "$testDir/StrykerTests/test-$x-go.dfy"
        cat tmp.txt > "$testDir/StrykerTests/test-$x-go.dfy.expect"
    else
        echo "Failed to run command 'Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:go test.dfy'"
    fi

    timeout --foreground -s SIGKILL $t Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:py test.dfy > tmp.txt
    if [ $? -eq 0 ];
    then
        echo "Successfully ran command 'Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:py test.dfy'"
        echo "// RUN: %dafny /noVerify /deleteCodeAfterRun:1 /compile:4 /compileTarget:py \"%s\" > \"%t\"" > "$testDir/StrykerTests/test-$x-py.dfy"
        echo "// RUN: %diff \"%s.expect\" \"%t\"" >> "$testDir/StrykerTests/test-$x-py.dfy"
        cat test.dfy >> "$testDir/StrykerTests/test-$x-py.dfy"
        cat tmp.txt > "$testDir/StrykerTests/test-$x-py.dfy.expect"
    else
        echo "Failed to run command 'Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:py test.dfy'"
    fi

    timeout --foreground -s SIGKILL $t Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:java test.dfy > tmp.txt
    if [ $? -eq 0 ];
    then
        echo "Successfully ran command 'Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:java test.dfy'"
        echo "// RUN: %dafny /noVerify /deleteCodeAfterRun:1 /compile:4 /compileTarget:java \"%s\" > \"%t\"" > "$testDir/StrykerTests/test-$x-java.dfy"
        echo "// RUN: %diff \"%s.expect\" \"%t\"" >> "$testDir/StrykerTests/test-$x-java.dfy"
        cat test.dfy >> "$testDir/StrykerTests/test-$x-java.dfy"
        cat tmp.txt > "$testDir/StrykerTests/test-$x-java.dfy.expect"
    else
        echo "Failed to run command 'Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:java test.dfy'"
    fi

    rm -rf tmp.txt test.dfy tests/* || true
    x=$(( x + 1 ))
done