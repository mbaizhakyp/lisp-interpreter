# CS503 Lisp Interpreter

- **Student:** Margulan Baizhakyp
- **Language:** Java 17 or newer

## Summary

An interpreter for the S-expression language from CS503 Project 1 (parts 1.1 to 1.8). It reads S-expressions from the keyboard or a file, evaluates them, and prints the results. Features by part:

- 1.1: reads and prints S-expressions; a list whose last cdr is not `()` prints in dotted form `(a . b)`.
- 1.2: `car`, `cdr`, `cons`, `quote`, `eval`, and the `'x` shorthand for `(quote x)`. A list that does not start with a built-in or a function evaluates to itself.
- 1.3: global variables with `(set name value)` (returns the value) stored in `rho`; an unset symbol evaluates to itself; predicates `nil?`, `atom?`, `list?` (true is `T`, false is `()`); `;` comments.
- 1.4: `and?`, `or?` (short-circuit), `eq?`, `if`, and `cond` with a flat `(a0 b0 a1 b1 ...)` list.
- 1.5: `add`, `sub`, `mul`, `div`, `rem`, `less` on integer symbols.
- 1.6: a local environment stack of `((names) (values))` frames, searched (top frame) before `rho`; `(function (args) body)` evaluates to itself.
- 1.7: calling user functions: arguments are evaluated, a frame is pushed, the body is evaluated, the frame is popped.
- 1.8: `(def name (args) body)` as shorthand for `(set name (function (args) body))`; `not?` (same as `nil?`).

The final tests run three full programs: `bowling` and `rpn2lisp` from the instructor, and a `blackjack` program written for this project.

| Part | Status |
|---|---|
| 1.1 Print Read S-Expressions | Done |
| 1.2 List Ops | Done |
| 1.3 Variables | Done |
| 1.4 Logical | Done |
| 1.5 Math | Done |
| 1.6 Local Environment | Done |
| 1.7 Function calls | Done |
| 1.8 def | Done |

## Build

```sh
javac -d out src/*.java        # compiles into out/
java -cp out Main              # type expressions, Ctrl-D to end
java -cp out Main < file.lisp  # run a file
```

## Test

Each `tests/NAME.lisp` is test input and `tests/NAME.expected` is its expected output. After building, run every test, compare it with the expected output, and save the log to `test-output.txt`:

```sh
for t in tests/*.lisp; do
    echo "== $t"; java -cp out Main < $t | tee out/actual.txt
    diff ${t%.lisp}.expected out/actual.txt && echo "PASS $t" || echo "FAIL $t"
done > test-output.txt
```

## Directory structure

```
README.md          this file
test-output.txt    output of the last test run (see Test)
src/
  Main.java        read/eval/print loop
  Eval.java        evaluates built-ins; holds the global environment rho
  Parser.java      turns text into S-expressions, 'x into (quote x), skips ; comments
  SExpr.java       S-expression types (Symbol, Cons, Nil); each prints itself
tests/
  1.N.lisp         test input for part 1.N
  bowling.lisp     instructor's bowling scorer
  rpn2lisp.lisp    instructor's RPN-to-lisp converter
  blackjack.lisp   blackjack hand scoring, dealer play, and winner
  NAME.expected    expected output for NAME.lisp
```

## Limitations

- `car` or `cdr` on an atom or `()`, and `cons` or `set` with one argument, are undefined in the spec; they print `error: ...` and the loop moves on. `atom?` and `list?` on `()` (also undefined) return `()`.
- Math on a non-number, or `div`/`rem` by 0, prints `error: ...`.
- Local lookup searches only the top frame of the local stack, then `rho`. Built-ins are checked before user functions, so a user function cannot replace a built-in. Argument counts are not checked, and the optional inline `((function (a b) ...) 1 2)` call is not supported.
- `bowling.lisp` and `rpn2lisp.lisp` needed fixes to run: missing parentheses, bowling's missing open-frame case, rpn2lisp's `not` (here `not?`), and rpn2lisp's first `cond`, which must wrap its pairs in one list as the 1.4 spec requires. rpn2lisp builds expressions with `+ - * /`, so the test defines those with `def` before evaluating them.
- `cond` with no true test (undefined in the spec) returns `()`.
- The reader does not accept dotted notation such as `(a . b)` (the spec does not require it), though the printer can print it.
- An unbalanced `)` or a missing `)` prints `error: ...` and the reader moves on.

## AI disclosure

For this assignment's preparation, the author has utilized Claude, a language model created by Anthropic. Within this assignment, Claude was used for purposes such as reviewing the assignment instructions, designing the interpreter, writing code, and writing tests.
