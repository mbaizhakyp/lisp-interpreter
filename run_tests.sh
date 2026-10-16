#!/bin/sh
# Runs every tests/NAME.lisp and compares its output with tests/NAME.expected.
cd "$(dirname "$0")" && sh build.sh || exit 1
fail=0
for t in tests/*.lisp; do
    name=${t%.lisp}
    if java -cp out Main "$t" | diff -u "$name.expected" - > /dev/null; then
        echo "PASS $name"
    else
        echo "FAIL $name"
        java -cp out Main "$t" | diff -u "$name.expected" -
        fail=1
    fi
done
exit $fail
