public class While extends Stmt {
    private final Expr condition;
    private final Block body;

    public While(int line, int column, Expr condition, Block body) {
        super(line, column);
        this.condition = condition;
        this.body = body;
    }

    public Expr getCondition() { return condition; }
    public Block getBody() { return body; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitWhile(this);
    }
}