public class If extends Stmt {
    private final Expr condition;
    private final Block thenBranch;
    private final Block elseBranch; // null se não houver else

    public If(int line, int column, Expr condition, Block thenBranch, Block elseBranch) {
        super(line, column);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public Expr getCondition() { return condition; }
    public Block getThenBranch() { return thenBranch; }
    public Block getElseBranch() { return elseBranch; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitIf(this);
    }
}