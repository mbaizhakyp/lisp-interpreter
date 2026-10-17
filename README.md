# CS503 Lisp Interpreter

- **Student:** Margulan Baizhakyp
- **Language:** Java 17 or newer

## Summary

An interpreter for the S-expression language from CS503 Project 1 (parts 1.1 to 1.8). It reads S-expressions from the keyboard or a file and prints them; later parts add evaluation.

| Part | Status |
|---|---|
| 1.1 Print Read S-Expressions | Done |
| 1.2 List Ops | Not started |
| 1.3 Variables | Not started |
| 1.4 Logical | Not started |
| 1.5 Math | Not started |
| 1.6 Local Environment | Not started |
| 1.7 Function calls | Not started |
| 1.8 def | Not started |

## Build

```sh
sh build.sh                    # compiles src/*.java into out/
java -cp out Main              # interactive (prompt "=> ")
java -cp out Main file.lisp    # run a file
```

## Test

```sh
sh run_tests.sh    # runs each tests/NAME.lisp, diffs against tests/NAME.expected, saves everything to test-output.txt
sh submit.sh       # runs the tests, then builds submissions/lisp-interpreter.zip
```

## Directory structure

```
README.md          this file
build.sh           compile script
run_tests.sh       test runner
submit.sh          builds the submission zip
test-output.txt    output of the last test run
src/
  Main.java        read/print loop
  Parser.java      turns text into S-expressions
  SExpr.java       S-expression types (Symbol, Cons, Nil); each prints itself
tests/
  NAME.lisp        test input for part NAME
  NAME.expected    expected output
```

## Limitations

- Only part 1.1 is done: expressions are read and printed, not evaluated.
- The reader does not accept dotted notation such as `(a . b)` (the spec does not require it), though the printer can print it.
- An unbalanced `)` or a missing `)` prints `error: ...` and the reader moves on.

## AI disclosure

For this assignment's preparation, the author has utilized Claude, a language model created by Anthropic. Within this assignment, Claude was used for purposes such as reviewing the assignment instructions, designing the interpreter, writing code, and writing tests.
