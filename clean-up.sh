#!/bin/bash

rm -rf test-go test-go-run || true
rm -rf test-java || true
rm -rf test.cpp test.h DafnyRuntime.h test.h.gch DafnyRuntime.h.gch a.out || true
rm -rf test.cs || true
rm -rf test-py || true
rm -rf test.js || true
rm -rf test.a || true
rm -rf tmp.txt || true
#rm -rf errors || true
rm -rf out || true
rm -rf outputs/* || true
rm -rf tests/* || true
rm -rf tests-minimized/* || true
rm -rf test.dfy || true
rm -rf go.mod || true