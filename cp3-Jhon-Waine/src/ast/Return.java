public class Return extends Stmt {
    private final Expr value; // null se retorno void

    public Return(int line, int column, Expr value) {
        super(line, column);
        this.value = value;
    }

    public Expr getValue() { return value; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitReturn(this);
    }
}