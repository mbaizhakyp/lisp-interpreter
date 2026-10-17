# CS503 Project S-Expression Language: Overview

Source: the instruction PDFs `project 1.1.pdf` to `project 1.8.pdf`, the course page screenshot, and the syllabus (`Fall-2026-CS-503-001.pdf`). Reviewed 2026-10-16.

## 1. The big picture

You are building a small **Lisp interpreter**: a REPL that reads S-expressions, evaluates them, and prints the result. Each of the 8 parts adds one layer on top of the previous one, so by the end you have one program, not eight.

By part 1.8 the language can run programs like this:

```lisp
(def fact (n) (if (less n 2) 1 (mul n (fact (sub n 1)))))
(fact 5)          ; -> 120
```

| Part | Title | What it adds | Size |
|---|---|---|---|
| 1.1 | Print Read S-Expressions | Data type, reader (parser), printer, read/print loop | Medium (the foundation) |
| 1.2 | List Ops | `eval`, `car`, `cdr`, `cons`, `quote`, the `'x` shorthand | Small |
| 1.3 | Variables | Global env `rho`, `set`, symbol lookup, predicates `nil?` `atom?` `list?` (+ optional `not?` `number?`, `cadr` etc.) | Small |
| 1.4 | Logical | `and?` `or?` (short-circuit), `eq?`, `if`, `cond` | Small |
| 1.5 | Math | `add` `sub` `mul` `div` `rem`, `less` | Small |
| 1.6 | Local Environment | Local env stack, lookup local-then-global, `function` expressions | Small |
| 1.7 | (Function calls) | Calling user-defined functions | Medium (the key step) |
| 1.8 | (def) | `(def name (args) body)` sugar | Tiny |

Weight: the syllabus gives the **LISP interpreter 20%** of the course grade (vs 5% for Lox, 30% for the final interpreter project).

## 2. Deadlines and submission (from the course page)

| Item | Due |
|---|---|
| Lisp Submissions of progress | 11/6/26 11:59 PM CST |
| Final Submission Lisp | 10/21/26 11:59 PM CDT |
| Lox Submissions / Final Lox Submission | 11/2/26 11:59 PM CST |
| Lisp Blackjack (separate item) | 10/6/26 9:40 AM CDT (already past) |

Things worth checking with the instructor:

- **The dates look swapped.** The final Lisp submission (10/21) is due *before* the progress submissions (11/6). If 10/21 is real, that is 5 days from today for the whole interpreter. It is doable (the total code is a few hundred lines), but plan for it.
- **Progress submissions are expected.** Part 1.7 says: "I am expecting multiple submissions as you work through the project... more often than not is the expectation. Every other stage should be a bare minimum. Each stage should be tested before moving on." So submit a snapshot at least after every second stage, ideally every stage.
- **Lisp Blackjack** is listed in the same module with a past due date and no instructions were uploaded. Worth confirming whether it applies to you.

## 3. Rules that affect how we work

- **Language.** Part 1.1 actually allows C, C++, Java, Rust, Swift, Kotlin, Odin or Zig ("imperative or OOP with strong static typing"), and others on request. You narrowed it to C or Java. My recommendation is **Java**: garbage collection removes the memory-management work C would need for millions of tiny cons cells, and a `sealed interface` with `Symbol` / `Cons` / `Nil` records maps exactly onto the S-expression type.
- **AI disclosure is required.** The syllabus allows AI tools "with proper referencing" and asks you to include this statement in assignments: *"For this assignment's preparation, the author(s) have utilized [Tool Name], a language model created by [Provider]. Within this assignment, the [Tool Name] was used for purposes such as [...]"*. We should put this in the README / submission. It also says to "verify everything", so you should understand every piece of the code.
- Background reading the course provides: *Micro Manual for LISP*, *LISP 1.5 Programmer's Manual* (in Blackboard "Papers"). This project is closely modeled on McCarthy's original eval.

## 4. Part-by-part requirements

### 1.1 Print Read S-Expressions
Goal: a program that repeatedly reads and prints S-expressions.

- An S-expression is **an atom (symbol)**, **a pair cell** (car, cdr), or **nil**. Nil is *not* an atom/symbol.
- A symbol is any run of characters that isn't whitespace or a parenthesis. `a`, `45`, `+`, `test` are all symbols. Numbers do not need special treatment.
- Lists are chains of cells ending in nil: `(a b c)` = `(a . (b . (c . ())))`.
- You do **not** read dotted notation (but the printer must print it, see 1.2).
- Input may span multiple lines; one line may hold several expressions.
- Grammar:
  ```
  <START> => <EXPR> <START> | EOF
  <EXPR>  => <ATOM> | ( <LIST>
  <LIST>  => ) | <EXPR> <LIST>
  ```
- Must handle: `()`, `a`, `45`, `test`, `(1 2 3)`, `((1 2 3) (a b c))`, `(()()())`, and
  `(() a 45 test (1 2 3) ((1 2 3) (a b c) ) ( ( ) ( ) ( ) ))`.

### 1.2 List Ops
Adds `eval` between read and print, so the loop becomes a real REPL.

- `car` / `cdr`: return the first / rest of a cell. Undefined on atoms and nil.
- `cons`: builds a new cell. `(cons a ())` -> `(a)`, `(cons a (cons b ()))` -> `(a b)`, and `(cons a b)` -> `(a . b)`, so the **printer must handle dotted pairs**.
- `quote`: returns its argument unevaluated. `(quote x)` -> `x`.
- `eval`: if the car of the expression is a known symbol (`car`, `cdr`, `cons`, `quote`, `eval`), call it. First argument is `(car (cdr s))`, second is `(car (cdr (cdr s)))`.
- Reader change: `'x` becomes `(quote x)`.
- Examples: `(car '(a b c))` -> `a`, `(cdr '(a b c))` -> `(b c)`, `(eval (car '(a b c)))` -> `a`.

### 1.3 Variables
- `(set name value)` adds a **global** binding. Assignment is only for global constants; local variables come from function parameters.
- `rho` (the global environment) is itself an S-expression, a list of 2-element lists: `((a 2) (b 4) (c x))`. No dotted pairs.
- `set` never replaces: `rho = cons(list(name, value), rho)`. Lookup takes the first match, which is the latest set.
- The return value of `set` is up to you (`()`, `T`, the name, the value...). The example prints `ok`.
- Evaluating a symbol looks it up in `rho`. **An unbound symbol evaluates to itself**: `x` -> `x`. This is what makes numbers like `42` work without special handling.
- `;` starts a line comment. **The reader must skip comments** (the examples rely on it).
- Truth: `()` is false, anything else is true; canonical true is `T`.
- Predicates: `nil?` (T iff arg is `()`), `atom?` (T iff arg is a symbol), `list?` (T iff arg is a cell). `(atom? ())` and `(list? ())` are left undefined.
- Optional: `not?` (same as `nil?`), `number?` (symbol is a valid integer), and `cadr`/`caar`/`caddr` shorthands. Cheap to add, so worth doing.

### 1.4 Logical
- All built-ins evaluate their arguments **except** `quote`, `set`'s first argument (the name), and the special forms below.
- `and?` / `or?`: two arguments, **short-circuit** (`and?` skips b if a is nil; `or?` skips b if a is not nil).
- `eq?`: true only if both arguments are atoms and the same symbol. `(eq? () ())` -> `()` and `(eq? '(a) '(a))` -> `()`.
- `if`: `(if a0 a1 a2)` evaluates a0, then **only one** of a1/a2. `(if 'T ''T ())` -> `(quote T)` / `'T`, `(if () ''T ())` -> `()`.
- `cond`: takes **one** argument, a *flat* list of test/result pairs: `(cond (a0 b0 a1 b1 'T d))`. Note there are no inner parentheses around each pair, which differs from standard Lisp. If all tests fail, behavior is undefined (return `()`).

### 1.5 Math
- `add`, `sub`, `mul`, `div` (integer), `rem`: two numeric arguments, return the result **as a symbol**.
- `less`: returns `()` unless a < b.
- Needs symbol-to-int and int-to-symbol conversion (hinted at in 1.3).

### 1.6 Local Environment
- A **local environment stack**, itself an S-expression, starts as `()`.
- A frame is a pair of parallel lists, names and values: `((a b c) (2 3 x))`. Pushing it gives `(((a b c) (2 3 x)))`. Popping is `cdr`.
- Lookup checks the **local environment first, then `rho`**.
- `(function (arglist) body)` evaluates to itself. Combined with `set` it defines a function: `(set foo (function (a b) (add a b)))`. The body is a single expression.
- Suggested test: start the local stack with a non-nil value and check lookups.

### 1.7 Function calls
- For `(fun a1 a2 a3)`: if `fun` looks up to `(function args body)`:
  1. Evaluate each argument (in the **caller's** environment).
  2. Build the frame `(cons args (cons actuals ()))` and push it.
  3. `eval body`.
  4. Pop the frame (the PDF says "car" here, but it means `cdr`, as 1.6 says).
- You choose whether user functions can override built-ins (check user functions before or after built-ins).
- Argument-count checking is not required.
- Optional, no bonus: allow an inline function in call position, `((function (a b) (add a b)) 11 12)`.

### 1.8 def
- `(def foo (a b) body)` is sugar for `(set foo (function (a b) body))`.

## 5. Typos and ambiguities in the PDFs, and the reading I'd use

| Where | Text | Reading |
|---|---|---|
| 1.3 | "atom? returns () iff its argument is a symbol" | Inverted. The examples show `(atom? x)` -> `T`, so atom? returns T for symbols. |
| 1.5 | "add returns the atom a+l" | a+b. |
| 1.7 | "reset the local environment to the car" | cdr (pop), per 1.6. |
| 1.7 | Example uses `plus` | 1.5 names it `add`. Could alias `plus` to `add`. |
| 1.6 | "search local before global" | Search **only the top frame**, then globals. Searching all frames would give dynamic scoping, which the spec doesn't ask for. |
| 1.3 | Return value of `set` | Return the name (or print `ok`). |
| 1.4 | `cond` with no true test | Return `()`. |
| Printer | How to print `(quote T)` | Print as `(quote T)`; optionally print `'T`. |

## 6. Suggested architecture (Java)

```
Main / REPL     read -> eval -> print loop, prompt, EOF handling
Lexer/Reader    tokens: ( ) ' symbol; skips whitespace and ; comments; builds SExpr
SExpr types     Symbol(name), Cons(car, cdr), Nil (singleton); T symbol constant
Printer         list form, dotted form for improper tails, () for nil
Evaluator       eval(expr): symbols -> lookup; Nil -> Nil; Cons -> dispatch on car
                special forms: quote, set, if, cond, and?, or?, function, def
                built-ins: car cdr cons eval nil? atom? list? eq? add sub mul div rem less ...
                user functions: lookup -> (function args body) -> push frame, eval, pop
Environment     rho (global list), local stack (list of frames), lookup()
```

Keeping `rho` and the local stack as actual S-expressions (not Java maps) is what the instructions ask for, and it lets you print them for debugging.

## 7. Suggested plan

1. Pick the language, set up a git repo and a README with the AI disclosure.
2. Build 1.1 and test with the provided examples. **Submit a progress snapshot.**
3. 1.2 + 1.3 together (small), test, **submit**.
4. 1.4 + 1.5, test, **submit**.
5. 1.6 + 1.7 + 1.8, test with recursive functions (factorial, list length, append, reverse), **submit**.
6. Final submission with a test file of example sessions.
   - **Required by the instructor (relayed 2026-10-16):** the test code must include the **blackjack**, **bowling**, and **rpn2lisp** ("rev2lisp") programs, each as `tests/NAME.lisp` + `NAME.expected` that the interpreter runs.
     - `final/bowling.lisp` and `final/rpn2lisp.txt` hold the instructor's code as given (not in the zip or test loop yet). Blackjack we write ourselves.
     - bowling as given won't parse: `frame`'s `cond` is missing 3 closing parens and has no open-frame case (neither strike nor spare).
     - rpn2lisp as given won't parse: `(op? (car rpn)` is missing its `)`, which swallows everything after `r2l`. It also uses `not` (spec names `not?`), `;` comments right after `)`, and builds `(+ 2 (- x 3))`, so `+ - * /` must work as aliases for `add sub mul div` for its final `eval` to give `(add 2 (sub 4 3))` = 3.

Keep a test file per stage (input `.lisp` + expected output) so each stage's grading has evidence and earlier stages don't break.
