/**
 * Evaluates S-expressions.
 * A symbol is a variable lookup. A list that starts with the name of a built-in,
 * a user function, or a function expression is a call. Anything else evaluates to itself.
 */
class Eval {
    static final SExpr NIL = Nil.NIL;
    static final Symbol T = new Symbol("T");
    static final Symbol FUNCTION = new Symbol("function");

    static SExpr rho = NIL; // global variables: ((name value) ...), newest first
    static SExpr env = NIL; // local environment stack: (((names) (values)) ...), top frame first

    static SExpr eval(SExpr s) {
        if (s instanceof Symbol) return lookup(s);
        if (!(s instanceof Cons c)) return s;
        SExpr args = c.cdr();
        if (!(c.car() instanceof Symbol f)) return call(eval(c.car()), args, s); // ((lambda (x) ...) 5)

        switch (f.name()) {
            // these do not evaluate (all of) their arguments
            case "quote":    return arg(args, 0);
            case "set":      return set(arg(args, 0), eval(arg(args, 1)));
            case "def":      return set(arg(args, 0), list(FUNCTION, arg(args, 1), arg(args, 2)));
            case "function": return s;
            case "and?":     return eval(arg(args, 0)) == NIL ? NIL : eval(arg(args, 1));
            case "or?":
                SExpr first = eval(arg(args, 0));
                return first != NIL ? first : eval(arg(args, 1));
            case "if":       return eval(arg(args, 0)) != NIL ? eval(arg(args, 1)) : eval(arg(args, 2));
            case "cond":     return cond(arg(args, 0));
            case "begin":    return begin(args);
            case "while":
                while (eval(arg(args, 0)) != NIL) begin(cdr(args));
                return NIL;
            case "let":      return let(arg(args, 0), cdr(args));
            case "lambda":   return new Cons(FUNCTION, args); // (lambda (x) body) is (function (x) body)

            // these evaluate all of their arguments
            case "car":   return car(eval(arg(args, 0)));
            case "cdr":   return cdr(eval(arg(args, 0)));
            case "cons":  return new Cons(eval(arg(args, 0)), eval(arg(args, 1)));
            case "eval":  return eval(eval(arg(args, 0)));
            case "nil?":
            case "not?":  return truth(eval(arg(args, 0)) == NIL);
            case "atom?": return truth(eval(arg(args, 0)) instanceof Symbol);
            case "list?": return truth(eval(arg(args, 0)) instanceof Cons);
            case "print": return print(evalAll(args));
            case "eq?":   return truth(eval(arg(args, 0)) instanceof Symbol a && a.equals(eval(arg(args, 1))));
            case "add": case "sub": case "mul": case "div": case "rem": case "less":
                return math(f.name(), number(eval(arg(args, 0))), number(eval(arg(args, 1))));
            default:      return call(lookup(f), args, s);
        }
    }

    /** Calls a user function (function (params) body), or returns s if fn is not a function. */
    static SExpr call(SExpr fn, SExpr args, SExpr s) {
        if (!(fn instanceof Cons && car(fn).equals(FUNCTION))) return s;
        env = new Cons(list(arg(fn, 1), evalAll(args)), env); // push the frame ((params) (values))
        SExpr result = eval(arg(fn, 2));
        env = cdr(env); // pop it
        return result;
    }

    /** (cond (a0 b0 a1 b1 ...)): evaluates the first b whose a is not (). */
    static SExpr cond(SExpr pairs) {
        while (pairs != NIL) {
            if (eval(arg(pairs, 0)) != NIL) return eval(arg(pairs, 1));
            pairs = cdr(cdr(pairs));
        }
        return NIL;
    }

    /** (begin e1 e2 ...): evaluates each e in order and returns the last value. */
    static SExpr begin(SExpr body) {
        SExpr result = NIL;
        for (; body != NIL; body = cdr(body)) result = eval(car(body));
        return result;
    }

    /** (let ((x 1) (y 2)) body ...): runs body with x and y added to the current local frame. */
    static SExpr let(SExpr bindings, SExpr body) {
        SExpr names = env == NIL ? NIL : arg(car(env), 0);
        SExpr values = env == NIL ? NIL : arg(car(env), 1);
        for (; bindings != NIL; bindings = cdr(bindings)) {
            names = new Cons(arg(car(bindings), 0), names);
            values = new Cons(eval(arg(car(bindings), 1)), values);
        }
        env = new Cons(list(names, values), env);
        SExpr result = begin(body);
        env = cdr(env);
        return result;
    }

    /** Prints the values on one line, strings without their quotes, and returns the last value. */
    static SExpr print(SExpr values) {
        String line = "";
        SExpr last = NIL;
        for (; values != NIL; values = cdr(values)) {
            last = car(values);
            line += (line.isEmpty() ? "" : " ") + last.toString().replace("\"", "");
        }
        System.out.println(line);
        return last;
    }

    static SExpr math(String op, long a, long b) {
        switch (op) {
            case "add": return symbol(a + b);
            case "sub": return symbol(a - b);
            case "mul": return symbol(a * b);
            case "div": return symbol(a / b);
            case "rem": return symbol(a % b);
            default:    return truth(a < b); // less
        }
    }

    /** Changes a local variable if the top local frame has one with this name, else sets a global. */
    static SExpr set(SExpr name, SExpr value) {
        if (env != NIL && local(name) != null) {
            SExpr frame = car(env); // add (name value) in front, hiding the old value
            env = new Cons(list(new Cons(name, arg(frame, 0)), new Cons(value, arg(frame, 1))), cdr(env));
        } else {
            rho = new Cons(list(name, value), rho);
        }
        return value;
    }

    /** The value of name in the top local frame, or null if it is not there. */
    static SExpr local(SExpr name) {
        SExpr values = arg(car(env), 1);
        for (SExpr names = arg(car(env), 0); names != NIL; names = cdr(names), values = cdr(values)) {
            if (car(names).equals(name)) return car(values);
        }
        return null;
    }

    /** Searches the top local frame, then the globals. An unset symbol evaluates to itself. */
    static SExpr lookup(SExpr name) {
        SExpr value = env == NIL ? null : local(name);
        if (value != null) return value;
        for (SExpr e = rho; e != NIL; e = cdr(e)) {
            if (arg(car(e), 0).equals(name)) return arg(car(e), 1);
        }
        return name;
    }

    static SExpr evalAll(SExpr list) {
        return list == NIL ? NIL : new Cons(eval(car(list)), evalAll(cdr(list)));
    }

    // small helpers

    static SExpr car(SExpr list) {
        if (!(list instanceof Cons c)) throw new IllegalStateException("not a list: " + list);
        return c.car();
    }

    static SExpr cdr(SExpr list) {
        if (!(list instanceof Cons c)) throw new IllegalStateException("not a list: " + list);
        return c.cdr();
    }

    /** The i-th element of a list, counting from 0. */
    static SExpr arg(SExpr list, int i) {
        for (; i > 0; i--) list = cdr(list);
        return car(list);
    }

    static SExpr list(SExpr... items) {
        SExpr result = NIL;
        for (int i = items.length - 1; i >= 0; i--) result = new Cons(items[i], result);
        return result;
    }

    static SExpr truth(boolean b) {
        return b ? T : NIL;
    }

    static long number(SExpr s) {
        try {
            return Long.parseLong(s.toString());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("not a number: " + s);
        }
    }

    static SExpr symbol(long n) {
        return new Symbol(Long.toString(n));
    }
}
