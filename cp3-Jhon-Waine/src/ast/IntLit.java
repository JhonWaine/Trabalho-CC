public class IntLit extends Expr {
    private final int value;

    public IntLit(int line, int column, int value) {
        super(line, column);
        this.value = value;
    }

    public int getValue() { return value; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitIntLit(this);
    }
}