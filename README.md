# CS503 Lisp Interpreter

An interpreter for the S-expression language from CS503 Project 1 (parts 1.1 to 1.8), written in Java 17+.

## Build and run

```sh
sh build.sh                    # compiles src/*.java into out/
java -cp out Main              # interactive REPL (prompt "=> ")
java -cp out Main file.lisp    # run a file
sh run_tests.sh                # run tests/*.lisp against tests/*.expected
```

## Progress

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

## Source layout

- `SExpr.java`: the S-expression type (`Symbol`, `Cons`, `Nil`); each prints itself via `toString`, with dotted notation for improper tails.
- `Parser.java`: reads S-expressions from a character stream.
- `Main.java`: the read/print loop.

## AI disclosure

For this assignment's preparation, the author has utilized Claude, a language model created by Anthropic. Within this assignment, Claude was used for purposes such as reviewing the assignment instructions, designing the interpreter, writing code, and writing tests.
