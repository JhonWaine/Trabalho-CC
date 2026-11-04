public class Id extends Expr {
    private final String name;

    public Id(int line, int column, String name) {
        super(line, column);
        this.name = name;
    }

    public String getName() { return name; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitId(this);
    }
}