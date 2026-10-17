/** Evaluates S-expressions. A list starting with car, cdr, cons, quote or eval is a call; anything else evaluates to itself. */
class Eval {
    static SExpr eval(SExpr s) {
        if (!(s instanceof Cons c && c.car() instanceof Symbol f && c.cdr() instanceof Cons args)) return s;
        SExpr a = args.car(); // first argument; the second is the car of args.cdr()
        return switch (f.name()) {
            case "quote" -> a;
            case "car" -> ((Cons) eval(a)).car();
            case "cdr" -> ((Cons) eval(a)).cdr();
            case "cons" -> new Cons(eval(a), eval(((Cons) args.cdr()).car()));
            case "eval" -> eval(eval(a));
            default -> s;
        };
    }
}
