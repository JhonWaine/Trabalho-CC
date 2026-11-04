import java.util.List;

public class Call extends Expr {
    private final String name;
    private final List<Expr> args;

    public Call(int line, int column, String name, List<Expr> args) {
        super(line, column);
        this.name = name;
        this.args = args;
    }

    public String getName() { return name; }
    public List<Expr> getArgs() { return args; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitCall(this);
    }
}