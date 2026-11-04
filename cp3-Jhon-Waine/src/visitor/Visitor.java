public interface Visitor<T> {
    T visitProgram(Program node);
    T visitVarDecl(VarDecl node);
    T visitFuncDecl(FuncDecl node);
    T visitParam(Param node);
    T visitBlock(Block node);
    T visitAssign(Assign node);
    T visitCallStmt(CallStmt node);
    T visitIf(If node);
    T visitWhile(While node);
    T visitReturn(Return node);
    T visitBin(Bin node);
    T visitUnary(Unary node);
    T visitCall(Call node);
    T visitId(Id node);
    T visitIntLit(IntLit node);
    T visitFloatLit(FloatLit node);
    T visitBoolLit(BoolLit node);
    T visitStringLit(StringLit node);
    T visitCharLit(CharLit node);
}