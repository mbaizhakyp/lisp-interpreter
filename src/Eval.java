/** Evaluates S-expressions. Symbols are looked up in rho; a list starting with a built-in name is a call; anything else evaluates to itself. */
class Eval {
    static final SExpr NIL = Nil.NIL, T = new Symbol("T");
    static SExpr rho = NIL; // global environment ((name value) ...), newest first

    static SExpr eval(SExpr s) {
        if (s instanceof Symbol) return lookup(s);
        if (!(s instanceof Cons c && c.car() instanceof Symbol f)) return s;
        SExpr args = c.cdr();
        return switch (f.name()) {
            case "quote" -> arg(args, 0);
            case "car" -> ((Cons) eval(arg(args, 0))).car();
            case "cdr" -> ((Cons) eval(arg(args, 0))).cdr();
            case "cons" -> new Cons(eval(arg(args, 0)), eval(arg(args, 1)));
            case "eval" -> eval(eval(arg(args, 0)));
            case "set" -> {
                SExpr v = eval(arg(args, 1));
                rho = new Cons(new Cons(arg(args, 0), new Cons(v, NIL)), rho);
                yield v;
            }
            case "nil?" -> truth(eval(arg(args, 0)) == NIL);
            case "atom?" -> truth(eval(arg(args, 0)) instanceof Symbol);
            case "list?" -> truth(eval(arg(args, 0)) instanceof Cons);
            case "and?" -> eval(arg(args, 0)) == NIL ? NIL : eval(arg(args, 1));
            case "or?" -> {
                SExpr v = eval(arg(args, 0));
                yield v != NIL ? v : eval(arg(args, 1));
            }
            case "eq?" -> {
                SExpr x = eval(arg(args, 0));
                yield truth(x instanceof Symbol && x.equals(eval(arg(args, 1))));
            }
            case "if" -> eval(arg(args, eval(arg(args, 0)) != NIL ? 1 : 2));
            case "cond" -> cond(arg(args, 0));
            case "add", "sub", "mul", "div", "rem", "less" -> {
                long x = num(eval(arg(args, 0))), y = num(eval(arg(args, 1)));
                yield switch (f.name()) {
                    case "add" -> new Symbol("" + (x + y));
                    case "sub" -> new Symbol("" + (x - y));
                    case "mul" -> new Symbol("" + (x * y));
                    case "div" -> new Symbol("" + (x / y));
                    case "rem" -> new Symbol("" + (x % y));
                    default -> truth(x < y);
                };
            }
            default -> s;
        };
    }

    /** (cond (a0 b0 a1 b1 ...)): the value of the b after the first a that is not (), or () if there is none. */
    static SExpr cond(SExpr pairs) {
        for (; pairs instanceof Cons p; pairs = ((Cons) p.cdr()).cdr())
            if (eval(p.car()) != NIL) return eval(arg(p, 1));
        return NIL;
    }

    /** The value of the newest (name value) in rho, or the name itself if unset. */
    static SExpr lookup(SExpr name) {
        for (SExpr e = rho; e instanceof Cons c; e = c.cdr())
            if (arg(c.car(), 0).equals(name)) return arg(c.car(), 1);
        return name;
    }

    /** The i-th element (from 0) of a list. */
    static SExpr arg(SExpr list, int i) {
        for (; i > 0; i--) list = ((Cons) list).cdr();
        return ((Cons) list).car();
    }

    static long num(SExpr s) {
        return Long.parseLong(((Symbol) s).name());
    }

    static SExpr truth(boolean b) {
        return b ? T : NIL;
    }
}
