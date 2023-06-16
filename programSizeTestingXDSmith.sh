#!/bin/bash

echo "PID: $$"
trap 'kill -9 $$' SIGINT


directory=$(pwd)
cd src/main/dafny_compiler/dafny/Binaries
dafny_dir=$(pwd)
cd "$directory"
cd src/main/xdsmith/xdsmith/work-dir
xd_dir=$(pwd)

t=180
x=0
sum=0

num=1000
while [ $x -lt $num ]; do
    cd "$directory"

    cd "$xd_dir"
    echo "Test number $x"
    timeout --foreground 300 racket ../xdsmith/fuzzer.rkt --timeout 300 --dafny-syntax true --seed $x > test.dfy
    if [ $? -ne 0 ]
    then
        x=$(( $x + 1 ))
        continue;
    fi

    lines=$(wc -l < "$xd_dir/test.dfy")
    sum=$(( $sum + $lines ))

    rm -rf "$xd_dir/test.dfy" || true

    x=$(( x + 1 ))
done

echo $(($sum / $num))