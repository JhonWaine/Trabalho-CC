public class CharLit extends Expr {
    private final char value;

    public CharLit(int line, int column, char value) {
        super(line, column);
        this.value = value;
    }

    public char getValue() { return value; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitCharLit(this);
    }
}