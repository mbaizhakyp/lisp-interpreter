#!/bin/sh
# Runs every tests/NAME.lisp, diffs its output with tests/NAME.expected, and saves the log to test-output.txt.
cd "$(dirname "$0")" && javac -d out src/*.java || exit 1
for t in tests/*.lisp; do
    echo "== $t"
    java -cp out Main "$t" > out/actual.txt
    cat out/actual.txt
    diff -u "${t%.lisp}.expected" out/actual.txt && echo "PASS $t" || echo "FAIL $t"
done 2>&1 | tee test-output.txt
! grep -q '^FAIL' test-output.txt
