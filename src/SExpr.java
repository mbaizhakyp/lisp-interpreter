/** An S-expression: a symbol (atom), a pair cell, or nil. Nil is not a symbol. */
public sealed interface SExpr permits Symbol, Cons, Nil {
}

/** Any run of characters that is not whitespace or a parenthesis. */
record Symbol(String name) implements SExpr {
    public String toString() {
        return name;
    }
}

/** A pair cell. Prints as (a b c), with a dotted tail when the last cdr is not (): (a . b). */
record Cons(SExpr car, SExpr cdr) implements SExpr {
    public String toString() {
        String s = "(" + car;
        SExpr rest = cdr;
        while (rest instanceof Cons next) {
            s += " " + next.car;
            rest = next.cdr;
        }
        if (rest != Nil.NIL) s += " . " + rest;
        return s + ")";
    }
}

/** The empty list (). */
enum Nil implements SExpr {
    NIL;

    public String toString() {
        return "()";
    }
}
