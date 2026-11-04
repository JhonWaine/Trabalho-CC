import java.util.List;

public class FuncDecl extends Node {
    private final String returnType;
    private final String name;
    private final List<Param> params;
    private final Block body;

    public FuncDecl(int line, int column, String returnType, String name, List<Param> params, Block body) {
        super(line, column);
        this.returnType = returnType;
        this.name = name;
        this.params = params;
        this.body = body;
    }

    public String getReturnType() { return returnType; }
    public String getName() { return name; }
    public List<Param> getParams() { return params; }
    public Block getBody() { return body; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitFuncDecl(this);
    }
}