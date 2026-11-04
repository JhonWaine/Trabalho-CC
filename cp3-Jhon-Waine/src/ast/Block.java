import java.util.List;

public class Block extends Stmt {
    private final List<Stmt> statements;

    public Block(int line, int column, List<Stmt> statements) {
        super(line, column);
        this.statements = statements;
    }

    public List<Stmt> getStatements() { return statements; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitBlock(this);
    }
}