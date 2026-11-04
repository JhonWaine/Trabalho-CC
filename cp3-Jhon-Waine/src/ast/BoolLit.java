public class BoolLit extends Expr {
    private final boolean value;

    public BoolLit(int line, int column, boolean value) {
        super(line, column);
        this.value = value;
    }

    public boolean getValue() { return value; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitBoolLit(this);
    }
}