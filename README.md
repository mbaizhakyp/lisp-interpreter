# CS503 Lisp Interpreter

- **Student:** Margulan Baizhakyp
- **Language:** Java 17 or newer

## Summary

A small Lisp interpreter built in eight steps (Project 1.1 to 1.8). It reads S-expressions, evaluates them, and prints the results.

| Part | Adds |
|---|---|
| 1.1 | Reading and printing S-expressions |
| 1.2 | `car`, `cdr`, `cons`, `quote`, `eval`, and `'x` for `(quote x)` |
| 1.3 | Global variables with `set`, and `nil?`, `atom?`, `list?` |
| 1.4 | `and?`, `or?`, `eq?`, `if`, `cond` |
| 1.5 | `add`, `sub`, `mul`, `div`, `rem`, `less` |
| 1.6 | Local variables, and `(function (params) body)` |
| 1.7 | Calling user functions |
| 1.8 | `def` |

All eight parts are done. Some behavior the spec leaves open:

- `()` is false and anything else is true; built-ins return `T` for true.
- A symbol with no value evaluates to itself, so numbers need no special handling.
- A list that does not start with a built-in or a function evaluates to itself.
- `set` and `def` return the value they store.
- `not?` is the same as `nil?`.
- `;` starts a comment.

## Build

```sh
javac -d out src/*.java        # compile into out/
java -cp out Main              # type expressions; Ctrl-D to quit
java -cp out Main < file.lisp  # run a file
```

## Test

Each `tests/NAME.lisp` has an expected output in `tests/NAME.expected`. After building, this runs every test, prints PASS or FAIL, and saves the output to `test-output.txt`:

```sh
for t in tests/*.lisp; do
    echo "== $t"; java -cp out Main < $t | tee out/actual.txt
    diff ${t%.lisp}.expected out/actual.txt && echo "PASS $t" || echo "FAIL $t"
done > test-output.txt
```

There is one test per part, plus three full programs:

- `bowling.lisp` and `rpn2lisp.lisp` are the instructor's programs.
- `blackjack.lisp` is my own: it scores hands (an ace counts as 1 or 11), plays the dealer's hand, and picks the winner.

## Directory structure

```
README.md          this file
test-output.txt    output of the last test run
src/
  Main.java        read-eval-print loop
  Parser.java      turns text into S-expressions
  SExpr.java       the S-expression types: Symbol, Cons, Nil
  Eval.java        evaluates S-expressions; holds the global and local environments
tests/
  1.1.lisp ...     one test per part, 1.1 to 1.8
  bowling.lisp     instructor's bowling scorer
  rpn2lisp.lisp    instructor's RPN to Lisp converter
  blackjack.lisp   blackjack
  *.expected       expected output for each test
```

## Limitations

- Inputs the spec leaves undefined, like `(car a)` or `(add a 1)`, print an `error:` line and the interpreter keeps going.
- The reader does not accept dotted pairs like `(a . b)`, but the printer prints them.
- Built-in names cannot be redefined, argument counts are not checked, and the optional `((function (a b) ...) 1 2)` call form is not supported.
- To run, the instructor's programs needed small fixes, noted at the top of each file: missing parentheses, a missing open-frame case in bowling, `not?` for `not`, and two `cond`s in rpn2lisp written without the list around their pairs. rpn2lisp builds expressions with `+ - * /`, so the test defines those with `def` before evaluating them.

## AI disclosure

For this assignment's preparation, the author has utilized Claude, a language model created by Anthropic. Within this assignment, Claude was used for purposes such as reviewing the assignment instructions, designing the interpreter, writing code, and writing tests.
