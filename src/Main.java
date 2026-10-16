import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Lisp interpreter entry point.
 *
 * Usage:
 *   java -cp out Main           read from standard input
 *   java -cp out Main file.lisp read from a file
 *
 * Part 1.1: repeatedly read an S-expression and print it.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Reader input = args.length > 0
                ? Files.newBufferedReader(Path.of(args[0]), StandardCharsets.UTF_8)
                : new InputStreamReader(System.in, StandardCharsets.UTF_8);
        // Only show a prompt when a person is typing, so piped output stays clean.
        boolean interactive = args.length == 0 && System.console() != null;

        Parser parser = new Parser(input);
        while (true) {
            if (interactive) {
                System.out.print("=> ");
                System.out.flush();
            }
            try {
                SExpr expr = parser.next();
                if (expr == null) {
                    break;
                }
                System.out.println(Printer.print(expr));
            } catch (Parser.ParseException e) {
                System.out.println("error: " + e.getMessage());
            }
        }
    }
}
