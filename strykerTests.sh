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
while [ $x -le 10 ]; do
    exitCode=0

    echo "Test number $x"
    timeout -s SIGKILL $t java -cp out/ Main.ExpectedProgramGeneration $x
    if [ $? -ne 0 ]
    then
        echo "Failed to create dafny file in $t seconds"
        x=$(( $x + 1 ))
        continue;
    fi

    cp tests/test.dfy test.dfy
    touch "test-$x.dfy"
    touch "test-$x.dfy.expect"

    successCount=0
    timeout --foreground -s SIGKILL $t Dafny /compile:0 test.dfy > tmp.txt
    if [ $? -eq 0 ];
    then
        echo "Successfully ran command 'Dafny /compile:0 test.dfy'"
        echo "// RUN: %dafny /compile:0 \"%s\" >> \"%t\"" >> "test-$x.dfy"
        cat tmp.txt >> "test-$x.dfy.expect"
        successCount=$(( successCount + 1))
    else
        echo "Failed to run command 'Dafny /compile:0 test.dfy'"
    fi

    timeout --foreground -s SIGKILL $t Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:cs test.dfy > tmp.txt
    if [ $? -eq 0 ];
    then
        echo "Successfully ran command 'Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:cs test.dfy'"
        echo "// RUN: %dafny /noVerify /deleteCodeAfterRun:1 /compile:4 /compileTarget:cs \"%s\" >> \"%t\"" >> "test-$x.dfy"
        cat tmp.txt >> "test-$x.dfy.expect"
        successCount=$(( successCount + 1))
    else
        echo "Failed to run command 'Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:cs test.dfy'"
    fi

    timeout --foreground -s SIGKILL $t Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:js test.dfy > tmp.txt
    if [ $? -eq 0 ];
    then
        echo "Successfully ran command 'Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:js test.dfy'"
        echo "// RUN: %dafny /noVerify /deleteCodeAfterRun:1 /compile:4 /compileTarget:js \"%s\" >> \"%t\"" >> "test-$x.dfy"
        cat tmp.txt >> "test-$x.dfy.expect"
        successCount=$(( successCount + 1))
    else
        echo "Failed to run command 'Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:js test.dfy'"
    fi

    timeout --foreground -s SIGKILL $t Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:go test.dfy > tmp.txt
    if [ $? -eq 0 ];
    then
        echo "Successfully ran command 'Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:go test.dfy'"
        echo "// RUN: %dafny /noVerify /deleteCodeAfterRun:1 /compile:4 /compileTarget:go \"%s\" >> \"%t\"" >> "test-$x.dfy"
        cat tmp.txt >> "test-$x.dfy.expect"
        successCount=$(( successCount + 1))
    else
        echo "Failed to run command 'Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:go test.dfy'"
    fi

    timeout --foreground -s SIGKILL $t Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:py test.dfy > tmp.txt
    if [ $? -eq 0 ];
    then
        echo "Successfully ran command 'Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:py test.dfy'"
        echo "// RUN: %dafny /noVerify /deleteCodeAfterRun:1 /compile:4 /compileTarget:py \"%s\" >> \"%t\"" >> "test-$x.dfy"
        cat tmp.txt >> "test-$x.dfy.expect"
        successCount=$(( successCount + 1))
    else
        echo "Failed to run command 'Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:py test.dfy'"
    fi

    timeout --foreground -s SIGKILL $t Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:java test.dfy > tmp.txt
    if [ $? -eq 0 ];
    then
        echo "Successfully ran command 'Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:java test.dfy'"
        echo "// RUN: %dafny /noVerify /deleteCodeAfterRun:1 /compile:4 /compileTarget:java \"%s\" >> \"%t\"" >> "test-$x.dfy"
        cat tmp.txt >> "test-$x.dfy.expect"
        successCount=$(( successCount + 1))
    else
        echo "Failed to run command 'Dafny /noVerify /deleteCodeAfterRun:1 /compileVerbose:0 /compile:4 /compileTarget:java test.dfy'"
    fi
    echo "// RUN: %diff \"%s.expect\" \"%t\"" >> "test-$x.dfy"
    cat test.dfy >> "test-$x.dfy"
    if [ $successCount -gt 0 ];
    then
        mv "test-$x.dfy" "$testDir/StrykerTests/test-$x.dfy"
        mv "test-$x.dfy.expect" "$testDir/StrykerTests/test-$x.dfy.expect"
    fi

    rm -rf tmp.txt test.dfy tests/* "test-$x.dfy" "test-$x.dfy.expect" || true
    x=$(( x + 1 ))
done