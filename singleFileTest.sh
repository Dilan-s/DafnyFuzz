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

exitCode=0
if [ "$language" = "go" ]; then
    timeout -s SIGKILL $t Dafny /noVerify /compileTarget:go /compile:2 /compileVerbose:0 test.dfy > tmp.txt 2>>errors/compErrors/go.txt
    if [ $? -eq 0 ];
    then
        echo "Success Go"
        ./test > "outputs/output-go-$fileno.txt" 2>>errors/compErrors/go.txt
        if [ $? -eq 0 ];
        then
            echo "GoCompSuccess"
        else
            echo "GoCompFail"
        fi
        rm -rf test test-go || true

    else
        echo "Failed to convert to Go in $t seconds for test number $testno file $fileno"
        rm -rf test test-go || true
        exitCode=1
    fi
elif [ "$language" = "py" ]; then
    timeout -s SIGKILL $t Dafny /noVerify /compileTarget:py /compile:2 /compileVerbose:0 test.dfy > tmp.txt 2>>errors/compErrors/py.txt
    if [ $? -eq 0 ];
    then
        echo "Success Python"
        python3 test-py/__main__.py > "outputs/output-py-$fileno.txt" 2>>errors/compErrors/py.txt
        if [ $? -eq 0 ];
        then
            echo "PyCompSuccess"
        else
            echo "PyCompFail"
        fi
        rm -rf test-py || true

    else
        echo "Failed to convert to Python in $t seconds for test number $testno file $fileno"
        rm -rf test-py || true
        exitCode=1
    fi
elif [ "$language" = "js" ]; then
    timeout -s SIGKILL $t Dafny /noVerify /compileTarget:js /compile:2 /compileVerbose:0 test.dfy > tmp.txt 2>>errors/compErrors/js.txt
    if [ $? -eq 0 ];
    then
        echo "Success JavaScript"
	    node test.js > "outputs/output-js-$fileno.txt" 2>>errors/compErrors/js.txt

        if [ $? -eq 0 ];
        then
            echo "JsCompSuccess"
        else
            echo "JsCompFail"
        fi
	rm -rf test.js || true
        
    else
        echo "Failed to convert to JavaScript in $t seconds for test number $testno file $fileno"
        rm -rf test.js || true
        exitCode=1
    fi
elif [ "$language" = "java" ]; then
    timeout -s SIGKILL $t Dafny /noVerify /compileTarget:java /compile:2 /compileVerbose:0 /unicodeChar:0 test.dfy > tmp.txt 2>errors/compErrors/java.txt
    if [ $? -eq 0 ];
    then
        echo "Success Java"
        java -jar test.jar > "outputs/output-java-$fileno.txt" 2>>errors/compErrors/java.txt

        if [ $? -eq 0 ];
        then
            echo "JavaCompSuccess"
        else
            echo "JavaCompFail"
        fi
        rm -rf test.jar test-java || true

    else
        echo "Failed to convert to Java in $t seconds for test number $testno file $fileno"
        rm -rf test.jar test-java || true
        exitCode=1
    fi
elif [ "$language" = "cs" ]; then
    timeout -s SIGKILL $t Dafny /noVerify /compileTarget:cs /compile:2 /compileVerbose:0 test.dfy > tmp.txt 2>>errors/compErrors/cs.txt
    if [ $? -eq 0 ];
    then
        echo "Success CS"
        dotnet test.dll > "outputs/output-cs-$fileno.txt" 2>>errors/compErrors/cs.txt

        if [ $? -eq 0 ];
        then
            echo "CSCompSuccess"
        else
            echo "CSCompFail"
        fi
        rm -rf test.dll test.runtimeconfig.json || true

    else
        echo "Failed to convert to CS in $t seconds for test number $testno file $fileno"
        rm -rf test.dll test.runtimeconfig.json || true
        exitCode=1
    fi
fi

rm -rf tmp.txt || true
exit $exitCode
