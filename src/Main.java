/** Reads S-expressions from standard input, evaluates each one, and prints the result. */
public class Main {
    public static void main(String[] args) {
        Parser parser = new Parser(System.in);
        while (true) {
            try {
                SExpr expr = parser.next();
                if (expr == null) return; // end of input
                System.out.println(Eval.eval(expr));
            } catch (RuntimeException e) {
                System.out.println("error: " + e.getMessage());
                Eval.env = Nil.NIL; // drop any frames left by a failed function call
            }
        }
    }
}
