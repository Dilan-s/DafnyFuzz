#!/bin/bash

fileno=0
t=180
while getopts l:n:f:t: flag
do
    case "${flag}" in
        l) language=${OPTARG};;
        n) testno=${OPTARG};;
        f) fileno=${OPTARG};;
        t) t=${OPTARG};;
    esac
done

if [ "$language" = "go" ]; then
    timeout -s SIGKILL $t Dafny /noVerify /compileTarget:go /compile:2 /compileVerbose:0 test.dfy > tmp.txt 2>>errors/compErrors/go.txt
    if [ $? -eq 0 ];
    then
        ./test > "outputs/output-go-$fileno.txt" 2>>errors/compErrors/go.txt
        rm -rf test test-go || true
        echo "Success Go"
    else
        echo "Failed to convert to Go in $t seconds for test number $testno file $fileno"
        rm -rf test test-go || true
    fi
elif [ "$language" = "py" ]; then
    timeout -s SIGKILL $t Dafny /noVerify /compileTarget:py /compile:2 /compileVerbose:0 test.dfy > tmp.txt 2>>errors/compErrors/py.txt
    if [ $? -eq 0 ];
    then
        python3 test-py/test.py > "outputs/output-py-$fileno.txt" 2>>errors/compErrors/py.txt
        rm -rf test-py || true
        echo "Success Python"
    else
        echo "Failed to convert to Python in $t seconds for test number $testno file $fileno"
        rm -rf test-py || true
    fi
elif [ "$language" = "js" ]; then
    timeout -s SIGKILL $t Dafny /noVerify /compileTarget:js /compile:2 /compileVerbose:0 test.dfy > tmp.txt 2>>errors/compErrors/js.txt
    if [ $? -eq 0 ];
    then
        node test.js > "outputs/output-js-$fileno.txt" 2>>errors/compErrors/js.txt
        rm -rf test.js || true
        echo "Success JavaScript"
    else
        echo "Failed to convert to JavaScript in $t seconds for test number $testno file $fileno"
        rm -rf test.js || true
    fi
elif [ "$language" = "java" ]; then
    timeout -s SIGKILL $t Dafny /noVerify /compileTarget:java /compile:2 /compileVerbose:0 test.dfy > tmp.txt 2>&1
    if [ $? -eq 0 ];
    then
        java -jar test.jar > "outputs/output-java-$fileno.txt" 2>>errors/compErrors/java.txt
        rm -rf test.jar test-java || true
        echo "Success Java"
    else
        echo "Failed to convert to Java in $t seconds for test number $testno file $fileno"
        rm -rf test.jar test-java || true
    fi
fi

rm -rf tmp.txt || true
