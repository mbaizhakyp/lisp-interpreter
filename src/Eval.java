/**
 * Evaluates S-expressions.
 * A symbol is a variable lookup. A list that starts with the name of a built-in
 * or a user function is a call. Anything else evaluates to itself.
 */
class Eval {
    static final SExpr NIL = Nil.NIL;
    static final Symbol T = new Symbol("T");
    static final Symbol FUNCTION = new Symbol("function");

    static SExpr rho = NIL; // global variables: ((name value) ...), newest first
    static SExpr env = NIL; // local environment stack: (((names) (values)) ...), top frame first

    static SExpr eval(SExpr s) {
        if (s instanceof Symbol) return lookup(s);
        if (!(s instanceof Cons c && c.car() instanceof Symbol f)) return s;
        SExpr args = c.cdr();

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

            // these evaluate all of their arguments
            case "car":   return car(eval(arg(args, 0)));
            case "cdr":   return cdr(eval(arg(args, 0)));
            case "cons":  return new Cons(eval(arg(args, 0)), eval(arg(args, 1)));
            case "eval":  return eval(eval(arg(args, 0)));
            case "nil?":
            case "not?":  return truth(eval(arg(args, 0)) == NIL);
            case "atom?": return truth(eval(arg(args, 0)) instanceof Symbol);
            case "list?": return truth(eval(arg(args, 0)) instanceof Cons);
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

    static SExpr set(SExpr name, SExpr value) {
        rho = new Cons(list(name, value), rho);
        return value;
    }

    /** Searches the top local frame, then the globals. An unset symbol evaluates to itself. */
    static SExpr lookup(SExpr name) {
        if (env != NIL) {
            SExpr names = arg(car(env), 0);
            SExpr values = arg(car(env), 1);
            while (names != NIL) {
                if (car(names).equals(name)) return car(values);
                names = cdr(names);
                values = cdr(values);
            }
        }
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
