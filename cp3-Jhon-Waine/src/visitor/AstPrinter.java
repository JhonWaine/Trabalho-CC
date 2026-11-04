public class AstPrinter implements Visitor<String> {
    private int indentLevel = 0;

    private String indent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    public String print(Node node) {
        this.indentLevel = 0;
        return node.accept(this).trim();
    }

    @Override
    public String visitProgram(Program node) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("Program\n");
        indentLevel++;
        for (Node decl : node.getDeclarations()) {
            sb.append(decl.accept(this));
        }
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visitVarDecl(VarDecl node) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("VarDecl(").append(node.getType()).append(", ").append(node.getName()).append(")\n");
        if (node.getInit() != null) {
            indentLevel++;
            sb.append(node.getInit().accept(this));
            indentLevel--;
        }
        return sb.toString();
    }

    @Override
    public String visitFuncDecl(FuncDecl node) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("FuncDecl(").append(node.getReturnType()).append(", ").append(node.getName()).append(")\n");
        indentLevel++;
        for (Param param : node.getParams()) {
            sb.append(param.accept(this));
        }
        sb.append(node.getBody().accept(this));
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visitParam(Param node) {
        return indent() + "Param(" + node.getType() + ", " + node.getName() + ")\n";
    }

    @Override
    public String visitBlock(Block node) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("Block\n");
        indentLevel++;
        for (Stmt stmt : node.getStatements()) {
            sb.append(stmt.accept(this));
        }
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visitAssign(Assign node) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("Assign(").append(node.getName()).append(")\n");
        indentLevel++;
        sb.append(node.getValue().accept(this));
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visitCallStmt(CallStmt node) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("CallStmt(").append(node.getName()).append(")\n");
        indentLevel++;
        for (Expr arg : node.getArgs()) {
            sb.append(arg.accept(this));
        }
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visitIf(If node) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("If\n");
        indentLevel++;
        sb.append(node.getCondition().accept(this));
        sb.append(node.getThenBranch().accept(this));
        if (node.getElseBranch() != null) {
            sb.append(node.getElseBranch().accept(this));
        }
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visitWhile(While node) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("While\n");
        indentLevel++;
        sb.append(node.getCondition().accept(this));
        sb.append(node.getBody().accept(this));
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visitReturn(Return node) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("Return\n");
        if (node.getValue() != null) {
            indentLevel++;
            sb.append(node.getValue().accept(this));
            indentLevel--;
        }
        return sb.toString();
    }

    @Override
    public String visitBin(Bin node) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("Bin(").append(node.getOp()).append(")\n");
        indentLevel++;
        sb.append(node.getLeft().accept(this));
        sb.append(node.getRight().accept(this));
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visitUnary(Unary node) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("Unary(").append(node.getOp()).append(")\n");
        indentLevel++;
        sb.append(node.getExpr().accept(this));
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visitCall(Call node) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("Call(").append(node.getName()).append(")\n");
        indentLevel++;
        for (Expr arg : node.getArgs()) {
            sb.append(arg.accept(this));
        }
        indentLevel--;
        return sb.toString();
    }

    @Override
    public String visitId(Id node) {
        return indent() + "Id(" + node.getName() + ")\n";
    }

    @Override
    public String visitIntLit(IntLit node) {
        return indent() + "IntLit(" + node.getValue() + ")\n";
    }

    @Override
    public String visitFloatLit(FloatLit node) {
        return indent() + "FloatLit(" + node.getValue() + ")\n";
    }

    @Override
    public String visitBoolLit(BoolLit node) {
        return indent() + "BoolLit(" + node.getValue() + ")\n";
    }

    @Override
    public String visitStringLit(StringLit node) {
        return indent() + "StringLit(" + node.getValue() + ")\n";
    }

    @Override
    public String visitCharLit(CharLit node) {
        return indent() + "CharLit(" + node.getValue() + ")\n";
    }
}