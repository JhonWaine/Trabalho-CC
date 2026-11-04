public class Assign extends Stmt {
    private final String name;
    private final Expr value;

    public Assign(int line, int column, String name, Expr value) {
        super(line, column);
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }
    public Expr getValue() { return value; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitAssign(this);
    }
}