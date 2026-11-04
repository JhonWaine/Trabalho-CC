import java.util.List;

public class Program extends Node {
    private final List<Node> declarations;

    public Program(int line, int column, List<Node> declarations) {
        super(line, column);
        this.declarations = declarations;
    }

    public List<Node> getDeclarations() { return declarations; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitProgram(this);
    }
}