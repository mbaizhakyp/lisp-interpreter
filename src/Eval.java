/** Evaluates S-expressions. Symbols are looked up in rho; a list starting with a built-in name is a call; anything else evaluates to itself. */
class Eval {
    static final Symbol T = new Symbol("T");
    static SExpr rho = Nil.NIL; // global environment ((name value) ...), newest first

    static SExpr eval(SExpr s) {
        if (s instanceof Symbol) return lookup(s);
        if (!(s instanceof Cons c && c.car() instanceof Symbol f && c.cdr() instanceof Cons args)) return s;
        SExpr a = args.car(); // first argument; the second is the car of args.cdr()
        return switch (f.name()) {
            case "quote" -> a;
            case "car" -> ((Cons) eval(a)).car();
            case "cdr" -> ((Cons) eval(a)).cdr();
            case "cons" -> new Cons(eval(a), eval(((Cons) args.cdr()).car()));
            case "eval" -> eval(eval(a));
            case "set" -> {
                SExpr v = eval(((Cons) args.cdr()).car());
                rho = new Cons(new Cons(a, new Cons(v, Nil.NIL)), rho);
                yield v;
            }
            case "nil?" -> truth(eval(a) == Nil.NIL);
            case "atom?" -> truth(eval(a) instanceof Symbol);
            case "list?" -> truth(eval(a) instanceof Cons);
            default -> s;
        };
    }

    /** The value of the newest (name value) in rho, or the name itself if unset. */
    static SExpr lookup(SExpr name) {
        for (SExpr e = rho; e instanceof Cons c; e = c.cdr()) {
            Cons pair = (Cons) c.car();
            if (pair.car().equals(name)) return ((Cons) pair.cdr()).car();
        }
        return name;
    }

    static SExpr truth(boolean b) {
        return b ? T : Nil.NIL;
    }
}
