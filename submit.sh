#!/bin/sh
# Runs the tests, then zips the submission into submissions/lisp-interpreter.zip.
cd "$(dirname "$0")" && sh run_tests.sh
mkdir -p submissions && rm -f submissions/lisp-interpreter.zip
zip -qr submissions/lisp-interpreter.zip README.md build.sh run_tests.sh submit.sh test-output.txt src tests
