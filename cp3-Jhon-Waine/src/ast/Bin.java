public class Bin extends Expr {
    private final String op;
    private final Expr left;
    private final Expr right;

    public Bin(int line, int column, String op, Expr left, Expr right) {
        super(line, column);
        this.op = op;
        this.left = left;
        this.right = right;
    }

    public String getOp() { return op; }
    public Expr getLeft() { return left; }
    public Expr getRight() { return right; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitBin(this);
    }
}