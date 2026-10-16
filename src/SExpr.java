/**
 * An S-expression is a symbol (atom), a pair cell, or nil.
 * Nil is its own type: it is not a symbol.
 */
public sealed interface SExpr permits Symbol, Cons, Nil {
}

/** An atom. Any run of characters that is not whitespace or a parenthesis. */
record Symbol(String name) implements SExpr {
}

/** A pair cell holding two S-expressions: car (first) and cdr (rest). */
record Cons(SExpr car, SExpr cdr) implements SExpr {
}

/** The empty list (). There is exactly one instance, Nil.NIL. */
final class Nil implements SExpr {
    static final Nil NIL = new Nil();

    private Nil() {
    }
}
