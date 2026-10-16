/**
 * Converts S-expressions to text.
 *
 * Proper lists print as (a b c). A list whose last cdr is not () prints
 * its tail in dotted notation: (cons a b) prints as (a . b).
 */
public class Printer {
    private Printer() {
    }

    public static String print(SExpr expr) {
        StringBuilder out = new StringBuilder();
        print(expr, out);
        return out.toString();
    }

    private static void print(SExpr expr, StringBuilder out) {
        if (expr instanceof Nil) {
            out.append("()");
        } else if (expr instanceof Symbol s) {
            out.append(s.name());
        } else if (expr instanceof Cons cell) {
            out.append('(');
            print(cell.car(), out);
            SExpr rest = cell.cdr();
            while (rest instanceof Cons next) {
                out.append(' ');
                print(next.car(), out);
                rest = next.cdr();
            }
            if (!(rest instanceof Nil)) {
                out.append(" . ");
                print(rest, out);
            }
            out.append(')');
        }
    }
}
