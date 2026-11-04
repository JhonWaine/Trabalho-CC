public class StringLit extends Expr {
    private final String value; // Guarda o lexema com aspas, ex: "ok"

    public StringLit(int line, int column, String value) {
        super(line, column);
        this.value = value;
    }

    public String getValue() { return value; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitStringLit(this);
    }
}