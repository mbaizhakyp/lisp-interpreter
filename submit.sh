#!/bin/sh
# Not part of the submission: runs the tests, then zips the submission into submissions/lisp-interpreter.zip.
cd "$(dirname "$0")" && sh run_tests.sh
mkdir -p submissions && rm -f submissions/lisp-interpreter.zip
zip -qr submissions/lisp-interpreter.zip README.md run_tests.sh test-output.txt src tests
