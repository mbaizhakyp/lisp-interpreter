# CS503 Final Project: Extended Lisp

- **Student:** Margulan Baizhakyp
- **Language:** Java 17 or newer

## Summary

Final project, option 2 (Extend Lisp). It starts from my Lisp interpreter from Project 1.1 to 1.8, which reads S-expressions, evaluates them, and prints the results, and adds seven features that make it more like Scheme. The new features are listed under "New features and why", after the original parts.

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

All eight parts are done and still pass their tests. Some behavior the spec leaves open:

- `()` is false and anything else is true; built-ins return `T` for true.
- A symbol with no value evaluates to itself, so numbers need no special handling.
- A list that does not start with a built-in or a function evaluates to itself.
- `set` and `def` return the value they store.
- `not?` is the same as `nil?`.
- `;` starts a comment.

## New features and why

The Project 1.8 language can only write a program as one expression per function, with recursion as the only loop and the REPL echo as the only output. Every feature below fixes one of those gaps. I picked features Scheme already has, so programs read like ordinary Scheme, and each one is a few lines in `Eval.java` (strings are one line in `Parser.java`).

| Feature | Example | Why |
|---|---|---|
| `begin` | `(begin (set a 1) (add a 2))` → `3` | A function body is one expression. `begin` runs several in order and returns the last, so a body can print, set a variable, and then return a value. It is Scheme's `begin` (`progn` in Common Lisp). |
| `while` | `(while (less i 5) (set i (add i 1)))` → `()` | Repeats its body while the test is not `()`, then returns `()`. Before this, every loop had to be a recursive function, which is awkward for counting loops and uses a Java stack frame per step. Scheme has no `while`, but most Lisps (Common Lisp's `loop`, Clojure's `while`) do. |
| `let` | `(let ((x 2) (y 3)) (mul x y))` → `6` | Local variables without writing a separate function. The values are computed first, then added to the current local frame, so the body still sees the enclosing function's parameters. It saves computing the same thing twice, like `aa` and `bb` in `tests/let.lisp`. |
| `print` | `(print "Total:" 5)` prints `Total: 5` | Improved IO. Before, the only output was the REPL printing each top-level result, so a program could not show progress from inside a loop or a function. `print` prints its values on one line and returns the last one, like Common Lisp's `print`. |
| Strings | `"hello world"` | Text with spaces, so `print` can show sentences. The reader reads `"..."` as one symbol with the quotes included, so strings need no new type: they evaluate to themselves and `eq?` compares them. `print` drops the quotes. |
| `lambda` and direct calls | `((lambda (x) (mul x x)) 5)` → `25` | `lambda` is Scheme's name for `function`: `(lambda (x) body)` evaluates to `(function (x) body)`. A list whose first element is itself a list is now evaluated as a call, so a function expression can be called without a name. This was the optional call form from Project 1.7. |
| `set` on locals | `(set n (sub n 1))` inside `(def f (n) ...)` | Before, `set` always made a global. Now it changes a local variable when the top local frame has one, like Scheme's `set!`. Without this, a `while` loop inside a function could only count with globals. |

`tests/fizzbuzz.lisp` uses all seven together.

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

There is one test per part (`1.1` to `1.8`), one per new feature (`begin`, `while`, `let`, `print`, `strings`, `lambda`, `set-local`), and four full programs:

- `bowling.lisp` and `rpn2lisp.lisp` are the instructor's programs.
- `blackjack.lisp` is my own: it scores hands (an ace counts as 1 or 11), plays the dealer's hand, and picks the winner.
- `fizzbuzz.lisp` is my own: FizzBuzz to 15 and a list of squares, using every new feature.

## Directory structure

```
README.md          this file
test-output.txt    output of the last test run
src/
  Main.java        read-eval-print loop
  Parser.java      turns text into S-expressions, including "strings"
  SExpr.java       the S-expression types: Symbol, Cons, Nil
  Eval.java        evaluates S-expressions; holds the global and local environments
tests/
  1.1.lisp ...     one test per part, 1.1 to 1.8
  begin.lisp, while.lisp, let.lisp, print.lisp, strings.lisp, lambda.lisp, set-local.lisp
                   one test per new feature
  fizzbuzz.lisp    FizzBuzz and squares, using all new features
  bowling.lisp     instructor's bowling scorer
  rpn2lisp.lisp    instructor's RPN to Lisp converter
  blackjack.lisp   blackjack
  *.expected       expected output for each test
```

## Limitations

- Inputs the spec leaves undefined, like `(car a)` or `(add a 1)`, print an `error:` line and the interpreter keeps going.
- The reader does not accept dotted pairs like `(a . b)`, but the printer prints them.
- Built-in names cannot be redefined, and argument counts are not checked.
- Scope is the same as in Project 1.6: a function sees only its own parameters and the globals, not the variables of the function that called it, and functions are not closures.
- `let` copies the enclosing local frame, so a `set` inside a `let` on one of the enclosing function's parameters changes only the copy; the parameter is unchanged after the `let` ends.
- A string cannot contain a `"`, and `print` removes every `"` in what it prints, including strings inside a list. Since a string is a symbol, `(atom? "a")` is `T`.
- `print` returns its last value, so at the top level the REPL prints that value again after the printed line.
- Each `set` adds a new binding in front of the old one instead of overwriting it, so a long `while` loop that sets globals uses more memory and slows down variable lookup.
- To run, the instructor's programs needed small fixes, noted at the top of each file: missing parentheses, a missing open-frame case in bowling, `not?` for `not`, and two `cond`s in rpn2lisp written without the list around their pairs. rpn2lisp builds expressions with `+ - * /`, so the test defines those with `def` before evaluating them.

## AI disclosure

For this assignment's preparation, the author has utilized Claude, a language model created by Anthropic. Within this assignment, Claude was used for purposes such as reviewing the assignment instructions, designing the interpreter, choosing the new features, writing code, writing tests, and writing the justification.
