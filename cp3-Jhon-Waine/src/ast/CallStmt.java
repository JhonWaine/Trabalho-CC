import java.util.List;

public class CallStmt extends Stmt {
    private final String name;
    private final List<Expr> args;

    public CallStmt(int line, int column, String name, List<Expr> args) {
        super(line, column);
        this.name = name;
        this.args = args;
    }

    public String getName() { return name; }
    public List<Expr> getArgs() { return args; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitCallStmt(this);
    }
}