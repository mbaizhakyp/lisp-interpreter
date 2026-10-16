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
        StringBuilder s = new StringBuilder("(").append(car);
        SExpr rest = cdr;
        for (; rest instanceof Cons c; rest = c.cdr) s.append(' ').append(c.car);
        if (rest != Nil.NIL) s.append(" . ").append(rest);
        return s.append(')').toString();
    }
}

/** The empty list (). */
enum Nil implements SExpr {
    NIL;

    public String toString() {
        return "()";
    }
}
