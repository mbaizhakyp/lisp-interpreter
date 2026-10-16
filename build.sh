#!/bin/sh
# Compiles the interpreter into out/.
cd "$(dirname "$0")" && mkdir -p out && javac -d out src/*.java
