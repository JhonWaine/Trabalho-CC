public class Unary extends Expr {
    private final String op;
    private final Expr expr;

    public Unary(int line, int column, String op, Expr expr) {
        super(line, column);
        this.op = op;
        this.expr = expr;
    }

    public String getOp() { return op; }
    public Expr getExpr() { return expr; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitUnary(this);
    }
}