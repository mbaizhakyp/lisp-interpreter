import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

/** Usage: java -cp out Main [file.lisp]. Reads S-expressions and prints them (part 1.1). */
public class Main {
    public static void main(String[] args) throws IOException {
        Parser parser = new Parser(args.length > 0
                ? Files.newBufferedReader(Path.of(args[0]))
                : new InputStreamReader(System.in));
        boolean prompt = args.length == 0 && System.console() != null; // keep piped output clean
        while (true) {
            if (prompt) {
                System.out.print("=> ");
                System.out.flush();
            }
            try {
                SExpr expr = parser.next();
                if (expr == null) break;
                System.out.println(expr);
            } catch (IllegalStateException e) {
                System.out.println("error: " + e.getMessage());
            }
        }
    }
}
