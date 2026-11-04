public class VarDecl extends Stmt {
    private final String type;
    private final String name;
    private final Expr init; // Pode ser null quando não inicializada

    public VarDecl(int line, int column, String type, String name, Expr init) {
        super(line, column);
        this.type = type;
        this.name = name;
        this.init = init;
    }

    public String getType() { return type; }
    public String getName() { return name; }
    public Expr getInit() { return init; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitVarDecl(this);
    }
}