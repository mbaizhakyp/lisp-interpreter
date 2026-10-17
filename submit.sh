#!/bin/sh
# Not part of the submission: builds, runs the tests as in the README, and zips submissions/lisp-interpreter.zip.
cd "$(dirname "$0")" && javac -d out src/*.java || exit 1
for t in tests/*.lisp; do
    echo "== $t"; java -cp out Main < $t | tee out/actual.txt
    diff ${t%.lisp}.expected out/actual.txt && echo "PASS $t" || echo "FAIL $t"
done > test-output.txt
grep -E '^(PASS|FAIL)' test-output.txt
mkdir -p submissions && rm -f submissions/lisp-interpreter.zip
zip -qr submissions/lisp-interpreter.zip README.md test-output.txt src tests
