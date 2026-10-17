import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Reads S-expressions one at a time. Expressions may span lines, and a line may hold several.
 *
 *   START => EXPR START | EOF
 *   EXPR  => ATOM | ( LIST | ' EXPR
 *   LIST  => ) | EXPR LIST
 */
public class Parser {
    private static final Pattern TOKEN = Pattern.compile("[()']|[^\\s()']+");
    private static final Symbol QUOTE = new Symbol("quote");
    private final Scanner in;

    public Parser(InputStream in) {
        this.in = new Scanner(in);
    }

    /** Returns the next S-expression, or null at end of input. */
    public SExpr next() {
        String t = token();
        return t == null ? null : expr(t);
    }

    private String token() {
        return in.findWithinHorizon(TOKEN, 0); // skips whitespace; null at EOF
    }

    private SExpr expr(String t) {
        if (t == null) throw new IllegalStateException("unexpected end of input");
        if (t.equals(")")) throw new IllegalStateException("unexpected )");
        if (t.equals("'")) return new Cons(QUOTE, new Cons(expr(token()), Nil.NIL)); // 'x is (quote x)
        return t.equals("(") ? list() : new Symbol(t);
    }

    private SExpr list() {
        String t = token();
        return ")".equals(t) ? Nil.NIL : new Cons(expr(t), list());
    }
}
