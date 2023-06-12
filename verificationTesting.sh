#!/bin/bash

echo "PID: $$"
trap 'kill -9 $$' SIGINT


javac -cp src/main/java/ -d ./out/ src/main/java/Main/VerificationProgramGeneration.java

rm -rf tests-minimized || true
rm -rf tests-incorrect || true
rm -rf errors || true

mkdir tests-minimized || true
mkdir tests-incorrect || true
mkdir errors || true
mkdir errors/verificationErrors
mkdir errors/verificationErrors/incorrect
mkdir errors/verificationErrors/correct


t=360
x=0
while [ true ];
do
    cd "$directory"

    echo "Test number $x"
    timeout -s SIGKILL $t java -cp out/ Main.VerificationProgramGeneration $x
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
        for file in tests-minimized/*
        do
            echo "Expecting validation to succeed for $file"

            timeout -s SIGKILL $t Dafny verify $file > tmp.txt 2>&1
            if [ $? -eq 4 ]
            then
            echo "Verification error found in test $x - correct validation of the file $file"
            mkdir "errors/verificationErrors/correct/$x" || true
            cp $file "errors/verificationErrors/correct/$x/test-$y.dfy"
            cat tmp.txt > "errors/verificationErrors/correct/$x/verificationOutput-$y.txt"
            fi
            rm -rf test.dfy || true
            rm -rf tmp.txt || true
            y=$(( $y + 1))
        done
    fi

    cd "$directory"
    y=0
    if [ "$(ls -A tests-incorrect/)" ];
    then
	for file in tests-incorrect/*
	do
	    echo "Expecting validation to fail for $file"
	    cp "$file" test.dfy
	
	    timeout -s SIGKILL $t Dafny verify test.dfy > tmp.txt 2>&1
	    code=$?
	    if [[ $code -ne 4 && $code -lt 5 ]]
	    then
		echo "Verification error found in test $x - incorrect validation of file $file"
		mkdir "errors/verificationErrors/incorrect/$x" || true
		cp $file "errors/verificationErrors/incorrect/$x/test-$y.dfy"
		cat tmp.txt > "errors/verificationErrors/incorrect/$x/verificationOutput-$y.txt"
	    fi
	    rm -rf test.dfy || true
	    rm -rf tmp.txt || true
	    y=$(( $y + 1))
	done
    fi
    
    rm -rf tests-minimized/* || true
    rm -rf tests-incorrect/* || true
    x=$(( $x + 1 ))    
done
