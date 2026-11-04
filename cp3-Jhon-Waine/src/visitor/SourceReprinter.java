import java.util.List;

public class SourceReprinter implements Visitor<String> {
    private int indentLevel = 0;

    private String indent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    public String reprint(Node node) {
        this.indentLevel = 0;
        return node.accept(this).trim();
    }

    @Override
    public String visitProgram(Program node) {
        StringBuilder sb = new StringBuilder();
        for (Node decl : node.getDeclarations()) {
            sb.append(decl.accept(this)).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String visitVarDecl(VarDecl node) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append(node.getType()).append(" ").append(node.getName());
        if (node.getInit() != null) {
            sb.append(" = ").append(node.getInit().accept(this));
        }
        sb.append(";");
        return sb.toString();
    }

    @Override
    public String visitFuncDecl(FuncDecl node) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append(node.getReturnType()).append(" ").append(node.getName()).append("(");
        List<Param> params = node.getParams();
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(params.get(i).accept(this));
        }
        sb.append(") ").append(node.getBody().accept(this));
        return sb.toString();
    }

    @Override
    public String visitParam(Param node) {
        return node.getType() + " " + node.getName();
    }

    @Override
    public String visitBlock(Block node) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        indentLevel++;
        for (Stmt stmt : node.getStatements()) {
            sb.append(stmt.accept(this)).append("\n");
        }
        indentLevel--;
        sb.append(indent()).append("}");
        return sb.toString();
    }

    @Override
    public String visitAssign(Assign node) {
        return indent() + node.getName() + " = " + node.getValue().accept(this) + ";";
    }

    @Override
    public String visitCallStmt(CallStmt node) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append(node.getName()).append("(");
        List<Expr> args = node.getArgs();
        for (int i = 0; i < args.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(args.get(i).accept(this));
        }
        sb.append(");");
        return sb.toString();
    }

    @Override
    public String visitIf(If node) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent()).append("if (").append(node.getCondition().accept(this)).append(") ")
                .append(node.getThenBranch().accept(this));
        if (node.getElseBranch() != null) {
            sb.append(" else ").append(node.getElseBranch().accept(this));
        }
        return sb.toString();
    }

    @Override
    public String visitWhile(While node) {
        return indent() + "while (" + node.getCondition().accept(this) + ") " + node.getBody().accept(this);
    }

    @Override
    public String visitReturn(Return node) {
        if (node.getValue() != null) {
            return indent() + "return " + node.getValue().accept(this) + ";";
        }
        return indent() + "return;";
    }

    @Override
    public String visitBin(Bin node) {
        return "(" + node.getLeft().accept(this) + " " + node.getOp() + " " + node.getRight().accept(this) + ")";
    }

    @Override
    public String visitUnary(Unary node) {
        return "(" + node.getOp() + node.getExpr().accept(this) + ")";
    }

    @Override
    public String visitCall(Call node) {
        StringBuilder sb = new StringBuilder();
        sb.append(node.getName()).append("(");
        List<Expr> args = node.getArgs();
        for (int i = 0; i < args.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(args.get(i).accept(this));
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String visitId(Id node) {
        return node.getName();
    }

    @Override
    public String visitIntLit(IntLit node) {
        return String.valueOf(node.getValue());
    }

    @Override
    public String visitFloatLit(FloatLit node) {
        return String.valueOf(node.getValue());
    }

    @Override
    public String visitBoolLit(BoolLit node) {
        return String.valueOf(node.getValue());
    }

    @Override
    public String visitStringLit(StringLit node) {
        return node.getValue();
    }

    @Override
    public String visitCharLit(CharLit node) {
        return "'" + node.getValue() + "'";
    }
}