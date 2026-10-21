import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Reads S-expressions one at a time, following this grammar:
 *
 *   START => EXPR START | EOF
 *   EXPR  => ATOM | ( LIST | ' EXPR
 *   LIST  => ) | EXPR LIST
 *
 * An expression may span lines, and a line may hold several. ; starts a comment.
 */
public class Parser {
    // a token is a comment, a parenthesis, a quote, or a symbol
    private static final Pattern TOKEN = Pattern.compile(";.*|[()']|[^\\s()';]+");
    private static final Symbol QUOTE = new Symbol("quote");
    private final Scanner in;

    public Parser(InputStream in) {
        this.in = new Scanner(in);
    }

    /** Returns the next S-expression, or null at the end of input. */
    public SExpr next() {
        String token = token();
        return token == null ? null : expr(token);
    }

    /** Returns the next token, skipping whitespace and comments, or null at the end of input. */
    private String token() {
        String token = in.findWithinHorizon(TOKEN, 0);
        while (token != null && token.startsWith(";")) {
            token = in.findWithinHorizon(TOKEN, 0);
        }
        return token;
    }

    private SExpr expr(String token) {
        if (token == null) throw new IllegalStateException("unexpected end of input");
        if (token.equals(")")) throw new IllegalStateException("unexpected )");
        if (token.equals("(")) return list();
        if (token.equals("'")) return new Cons(QUOTE, new Cons(expr(token()), Nil.NIL)); // 'x is (quote x)
        return new Symbol(token);
    }

    private SExpr list() {
        String token = token();
        if (")".equals(token)) return Nil.NIL;
        return new Cons(expr(token), list());
    }
}
