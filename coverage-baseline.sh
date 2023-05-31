#!/bin/bash

echo "PID: $$"
trap 'kill -9 $$' SIGINT

rm -rf coverage/baseline || true
mkdir coverage || true
mkdir coverage/baseline || true

npm install bignumber.js

directory=$(pwd)

cd src/main/dafny_compiler/dafny/Binaries
dafny_binary=$(pwd)

cd "$directory"


for file in src/main/dafny_compiler/dafny/Test/**/*.dfy;
do
    cd "$directory"

    if egrep '/compile:1|/compile:3|/compile:4|%verify' "$file";
    then
        echo "Attempting to run $file"
        cp $file test.dfy
        cp test.dfy "$dafny_binary/test.dfy"

        cd "$dafny_binary"
        echo "Verify File"
        coverlet . --target dotnet --targetargs "Dafny.dll verify test.dfy" -f cobertura -f json --merge-with coverage.json

        # GO
        cd "$dafny_binary"
        echo "GO File"
        coverlet . --target dotnet --targetargs "Dafny.dll /deleteCodeAfterRun:1 /compile:4 /noVerify /compileTarget:go test.dfy" -f cobertura -f json --merge-with coverage.json

        # js
        cd "$dafny_binary"
        echo "JS File"
        coverlet . --target dotnet --targetargs "Dafny.dll /deleteCodeAfterRun:1 /compile:4 /noVerify /compileTarget:js test.dfy" -f cobertura -f json --merge-with coverage.json

        # java
        cd "$dafny_binary"
        echo "JAVA File"
        coverlet . --target dotnet --targetargs "Dafny.dll /deleteCodeAfterRun:1 /compile:4 /noVerify /compileTarget:java test.dfy" -f cobertura -f json --merge-with coverage.json

        # py
        cd "$dafny_binary"
        echo "PY File"
        coverlet . --target dotnet --targetargs "Dafny.dll /deleteCodeAfterRun:1 /compile:4 /noVerify /compileTarget:py test.dfy" -f cobertura -f json --merge-with coverage.json

        # cs
        cd "$dafny_binary"
        echo "CS File"
        coverlet . --target dotnet --targetargs "Dafny.dll /deleteCodeAfterRun:1 /compile:4 /noVerify /compileTarget:cs test.dfy" -f cobertura -f json --merge-with coverage.json
        cd "$directory"
        rm -rf test.dfy "$dafny_binary/test.dfy" || true
        cp coverage.cobertura.xml "$directory/coverage/baseline/"
    fi

done

