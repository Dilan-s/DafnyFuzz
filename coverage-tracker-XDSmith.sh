#!/bin/bash

echo "PID: $$"
trap 'kill -9 $$' SIGINT

npm install bignumber.js

directory=$(pwd)
rm -rf coverage || true
mkdir coverage


cd src/main/dafny_compiler/dafny/Binaries
dafny_dir=$(pwd)

cd "$directory"
cd src/main/xdsmith/xdsmith/work-dir
xd_dir=$(pwd)

cd "$directory"
x=0
while [ true ]; do
  cd "$directory"

  cd "$xd_dir"
  echo "Test number $x"
  timeout --foreground 300 racket ../xdsmith/fuzzer.rkt --with-print-constrained true --timeout 300 --dafny-syntax true --seed $x > test.dfy
  if [ $? -ne 0 ]
  then
    echo "Failed to create dafny file in $t seconds"
    x=$(( $x + 1 ))
    continue;
  fi

  cp "$xd_dir/test.dfy" "$dafny_dir/test.dfy"

  # GO
  cd "$dafny_dir"
  echo "GO File"
  coverlet . --target dotnet --targetargs "Dafny.dll /deleteCodeAfterRun:1 /compile:4 /noVerify /compileTarget:go test.dfy" -f cobertura -f json --merge-with coverage.json --include "Microsoft.Dafny.Compilers"

  # js
  cd "$dafny_dir"
  echo "JS File"
  coverlet . --target dotnet --targetargs "Dafny.dll /deleteCodeAfterRun:1 /compile:4 /noVerify /compileTarget:js test.dfy" -f cobertura -f json --merge-with coverage.json --include "Microsoft.Dafny.Compilers"

  # java
  cd "$dafny_dir"
  echo "JAVA File"
  coverlet . --target dotnet --targetargs "Dafny.dll /deleteCodeAfterRun:1 /compile:4 /noVerify /compileTarget:java test.dfy" -f cobertura -f json --merge-with coverage.json --include "Microsoft.Dafny.Compilers"

  # py
  cd "$dafny_dir"
  echo "PY File"
  coverlet . --target dotnet --targetargs "Dafny.dll /deleteCodeAfterRun:1 /compile:4 /noVerify /compileTarget:py test.dfy" -f cobertura -f json --merge-with coverage.json --include "Microsoft.Dafny.Compilers"

  # cs
  cd "$dafny_dir"
  echo "CS File"
  coverlet . --target dotnet --targetargs "Dafny.dll /deleteCodeAfterRun:1 /compile:4 /noVerify /compileTarget:cs test.dfy" -f cobertura -f json --merge-with coverage.json --include "Microsoft.Dafny.Compilers"


  cd "$directory"
  rm -rf "$xd_dir/test.dfy" "$dafny_dir/test.dfy" || true


  cd "$dafny_dir"
  cp coverage.cobertura.xml "$directory/coverage/"

  x=$(( $x + 1 ))
done

