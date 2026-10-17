/** Reads S-expressions from standard input and prints them (part 1.1). */
public class Main {
    public static void main(String[] args) {
        Parser parser = new Parser(System.in);
        while (true) {
            try {
                SExpr expr = parser.next();
                if (expr == null) return;
                System.out.println(expr);
            } catch (IllegalStateException e) {
                System.out.println("error: " + e.getMessage());
            }
        }
    }
}
