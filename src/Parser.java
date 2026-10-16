import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

/**
 * Reads S-expressions one at a time from a character stream.
 *
 * Grammar:
 *   START => EXPR START | EOF
 *   EXPR  => ATOM | ( LIST
 *   LIST  => ) | EXPR LIST
 *
 * Expressions may span lines, and a line may hold several expressions.
 */
public class Parser {
    private static final int EOF = -1;

    private final PushbackReader in;

    public Parser(Reader in) {
        this.in = new PushbackReader(in);
    }

    /** Thrown when the input is not a well formed S-expression. */
    public static class ParseException extends Exception {
        public ParseException(String message) {
            super(message);
        }
    }

    /** Returns the next S-expression, or null at end of input. */
    public SExpr next() throws IOException, ParseException {
        int c = skipWhitespace();
        if (c == EOF) {
            return null;
        }
        return readExpr(c);
    }

    // EXPR => ATOM | ( LIST
    private SExpr readExpr(int c) throws IOException, ParseException {
        if (c == '(') {
            return readList();
        }
        if (c == ')') {
            throw new ParseException("unexpected )");
        }
        return readSymbol(c);
    }

    // LIST => ) | EXPR LIST
    private SExpr readList() throws IOException, ParseException {
        int c = skipWhitespace();
        if (c == EOF) {
            throw new ParseException("unexpected end of input, missing )");
        }
        if (c == ')') {
            return Nil.NIL;
        }
        SExpr first = readExpr(c);
        SExpr rest = readList();
        return new Cons(first, rest);
    }

    private SExpr readSymbol(int c) throws IOException {
        StringBuilder name = new StringBuilder();
        while (c != EOF && !Character.isWhitespace(c) && c != '(' && c != ')') {
            name.append((char) c);
            c = in.read();
        }
        if (c != EOF) {
            in.unread(c);
        }
        return new Symbol(name.toString());
    }

    /** Skips whitespace and returns the next character, or EOF. */
    private int skipWhitespace() throws IOException {
        int c = in.read();
        while (c != EOF && Character.isWhitespace(c)) {
            c = in.read();
        }
        return c;
    }
}
