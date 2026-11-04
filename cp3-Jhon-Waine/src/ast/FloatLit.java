public class FloatLit extends Expr {
    private final double value;

    public FloatLit(int line, int column, double value) {
        super(line, column);
        this.value = value;
    }

    public double getValue() { return value; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitFloatLit(this);
    }
}