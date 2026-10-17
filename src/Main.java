/** Reads S-expressions from standard input, evaluates them, and prints the results. */
public class Main {
    public static void main(String[] args) {
        Parser parser = new Parser(System.in);
        while (true) {
            try {
                SExpr expr = parser.next();
                if (expr == null) return;
                System.out.println(Eval.eval(expr));
            } catch (RuntimeException e) {
                System.out.println("error: " + e.getMessage());
            }
        }
    }
}
