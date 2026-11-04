import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    private final Scanner scanner;
    private Token lookahead;
    private final List<String> errors;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        this.lookahead = scanner.nextToken();
        this.errors = new ArrayList<>();
    }

    // Método principal do CP3: devolve resultado com a AST (se válida) e a lista de erros
    public ParseResult parseProgramResult() {
        Program root = parseProgram();
        return new ParseResult(root, errors);
    }

    // Mantido por compatibilidade direta com os testes do CP2
    public boolean parse() {
        return parseProgramResult().isValid();
    }

    // Permite avaliar expressões isoladas nos testes unitários de precedência/parênteses
    public Expr parseStandaloneExpr() {
        Expr e = parseExpr();
        if (lookahead.getType() != TokenType.TK_EOF) {
            error("EOF");
        }
        return errors.isEmpty() ? e : null;
    }

    // --- Utilitários ---
    private void error(String expected) {
        String msg = String.format("Erro Sintático [%d:%d]: Esperava '%s', encontrou '%s'",
                lookahead.getLine(), lookahead.getColumn(), expected, lookahead.getLexeme());
        errors.add(msg);
        System.err.println(msg);
        throw new SyntaxException(expected);
    }

    private Token match(TokenType expectedType) {
        if (lookahead.getType() == expectedType) {
            Token consumed = lookahead;
            lookahead = scanner.nextToken();
            return consumed;
        } else {
            error(expectedType.toString());
            return null;
        }
    }

    private Token match(TokenType expectedType, String expectedLexeme) {
        if (lookahead.getType() == expectedType && lookahead.getLexeme().equals(expectedLexeme)) {
            Token consumed = lookahead;
            lookahead = scanner.nextToken();
            return consumed;
        } else {
            error(expectedLexeme);
            return null;
        }
    }

    private boolean isType() {
        return lookahead.getType() == TokenType.KW_INT || lookahead.getType() == TokenType.KW_DOUBLE ||
                lookahead.getType() == TokenType.KW_BOOL || lookahead.getType() == TokenType.KW_CHAR ||
                lookahead.getType() == TokenType.KW_STRING || lookahead.getType() == TokenType.KW_VOID;
    }

    private boolean isFirstExpr() {
        return lookahead.getType() == TokenType.TK_ID || lookahead.getType() == TokenType.TK_INT_VAL ||
                lookahead.getType() == TokenType.TK_FLOAT_VAL || lookahead.getType() == TokenType.TK_STRING_VAL ||
                lookahead.getType() == TokenType.KW_TRUE || lookahead.getType() == TokenType.KW_FALSE ||
                lookahead.getLexeme().equals("(") || lookahead.getLexeme().equals("-");
    }

    private boolean isRelOp() {
        if (lookahead.getType() != TokenType.TK_OP) return false;
        String lex = lookahead.getLexeme();
        return lex.equals("==") || lex.equals("!=") || lex.equals("<") ||
                lex.equals("<=") || lex.equals(">") || lex.equals(">=");
    }

    private boolean isAddOp() {
        if (lookahead.getType() != TokenType.TK_OP) return false;
        String lex = lookahead.getLexeme();
        return lex.equals("+") || lex.equals("-");
    }

    private boolean isMulOp() {
        if (lookahead.getType() != TokenType.TK_OP) return false;
        String lex = lookahead.getLexeme();
        return lex.equals("*") || lex.equals("/");
    }

    private boolean isFollowExpr() {
        return lookahead.getLexeme().equals(";") || lookahead.getLexeme().equals(")") ||
                lookahead.getLexeme().equals(",") || lookahead.getType() == TokenType.TK_EOF;
    }

    // Recuperação em Modo Pânico
    private void panicModeSync() {
        while (lookahead.getType() != TokenType.TK_EOF) {
            if (lookahead.getType() == TokenType.TK_DELIM &&
                    (lookahead.getLexeme().equals(";") || lookahead.getLexeme().equals("}"))) {
                if (lookahead.getLexeme().equals(";")) {
                    lookahead = scanner.nextToken();
                }
                break;
            }
            if (isType() || lookahead.getType() == TokenType.KW_IF ||
                    lookahead.getType() == TokenType.KW_WHILE || lookahead.getType() == TokenType.KW_RETURN) {
                break;
            }
            lookahead = scanner.nextToken();
        }
    }

    // --- Regras da Gramática com Construção da AST ---

    private Program parseProgram() {
        int line = lookahead.getLine();
        int col = lookahead.getColumn();
        List<Node> decls = new ArrayList<>();
        parseDeclList(decls);
        if (lookahead.getType() != TokenType.TK_EOF) {
            String msg = String.format("Erro Sintático [%d:%d]: Tokens extras no final do arquivo.",
                    lookahead.getLine(), lookahead.getColumn());
            errors.add(msg);
            System.err.println(msg);
        }
        return new Program(line, col, decls);
    }

    private void parseDeclList(List<Node> decls) {
        if (isType()) {
            try {
                Node d = parseDecl();
                if (d != null) decls.add(d);
            } catch (SyntaxException e) {
                panicModeSync();
            }
            parseDeclList(decls);
        } else if (lookahead.getType() == TokenType.TK_EOF) {
            // ε válido: lookahead pertence a FOLLOW(DeclList) = {EOF}
        } else {
            try {
                error("Declaração ou EOF");
            } catch (SyntaxException e) {
                panicModeSync();
                if (lookahead.getType() != TokenType.TK_EOF) {
                    parseDeclList(decls);
                }
            }
        }
    }

    private Node parseDecl() {
        Token typeTok = parseType();
        Token idTok = match(TokenType.TK_ID);
        return parseDeclTail(typeTok, idTok);
    }

    private Node parseDeclTail(Token typeTok, Token idTok) {
        if (lookahead.getLexeme().equals("=") || lookahead.getLexeme().equals(";")) {
            Expr init = parseVarInit();
            match(TokenType.TK_DELIM, ";");
            return new VarDecl(typeTok.getLine(), typeTok.getColumn(), typeTok.getLexeme(), idTok.getLexeme(), init);
        } else if (lookahead.getLexeme().equals("(")) {
            match(TokenType.TK_DELIM, "(");
            List<Param> params = parseParamList();
            match(TokenType.TK_DELIM, ")");
            Block body = parseBlock();
            return new FuncDecl(typeTok.getLine(), typeTok.getColumn(), typeTok.getLexeme(), idTok.getLexeme(), params, body);
        } else {
            error("';', '=' ou '('");
            return null;
        }
    }

    private Expr parseVarInit() {
        if (lookahead.getLexeme().equals("=")) {
            match(TokenType.TK_OP, "=");
            return parseExpr();
        } else if (lookahead.getLexeme().equals(";")) {
            // ε válido: FOLLOW(VarInit) = {;}
            return null;
        } else {
            error("'=' ou ';'");
            return null;
        }
    }

    private Token parseType() {
        if (isType()) {
            Token t = lookahead;
            lookahead = scanner.nextToken();
            return t;
        } else {
            error("Tipo válido (int, double, etc)");
            return null;
        }
    }

    private List<Param> parseParamList() {
        List<Param> params = new ArrayList<>();
        if (isType()) {
            Token typeTok = parseType();
            Token idTok = match(TokenType.TK_ID);
            params.add(new Param(typeTok.getLine(), typeTok.getColumn(), typeTok.getLexeme(), idTok.getLexeme()));
            parseParamListTail(params);
        } else if (lookahead.getLexeme().equals(")")) {
            // ε válido: FOLLOW(ParamList) = {)}
        } else {
            error("Tipo de parâmetro ou ')'");
        }
        return params;
    }

    private void parseParamListTail(List<Param> params) {
        if (lookahead.getLexeme().equals(",")) {
            match(TokenType.TK_DELIM, ",");
            Token typeTok = parseType();
            Token idTok = match(TokenType.TK_ID);
            params.add(new Param(typeTok.getLine(), typeTok.getColumn(), typeTok.getLexeme(), idTok.getLexeme()));
            parseParamListTail(params);
        } else if (lookahead.getLexeme().equals(")")) {
            // ε válido: FOLLOW(ParamListTail) = {)}
        } else {
            error("',' ou ')'");
        }
    }

    private Block parseBlock() {
        Token open = match(TokenType.TK_DELIM, "{");
        List<Stmt> stmts = new ArrayList<>();
        parseStmtList(stmts);
        match(TokenType.TK_DELIM, "}");
        return new Block(open.getLine(), open.getColumn(), stmts);
    }

    private void parseStmtList(List<Stmt> stmts) {
        List<TokenType> firstStmt = Arrays.asList(TokenType.TK_ID, TokenType.KW_IF, TokenType.KW_WHILE, TokenType.KW_RETURN);
        while (isType() || firstStmt.contains(lookahead.getType())) {
            try {
                Stmt s = parseStmt();
                if (s != null) stmts.add(s);
            } catch (SyntaxException e) {
                panicModeSync();
            }
        }
        if (!lookahead.getLexeme().equals("}")) {
            error("'}' ou Comando");
        }
    }

    private Stmt parseStmt() {
        if (isType()) {
            Token typeTok = parseType();
            Token idTok = match(TokenType.TK_ID);
            Expr init = parseVarInit();
            match(TokenType.TK_DELIM, ";");
            return new VarDecl(typeTok.getLine(), typeTok.getColumn(), typeTok.getLexeme(), idTok.getLexeme(), init);
        } else if (lookahead.getType() == TokenType.TK_ID) {
            Token idTok = match(TokenType.TK_ID);
            return parseAssignOrCall(idTok);
        } else if (lookahead.getType() == TokenType.KW_IF) {
            Token ifTok = match(TokenType.KW_IF);
            match(TokenType.TK_DELIM, "(");
            Expr cond = parseExpr();
            match(TokenType.TK_DELIM, ")");
            Block thenBlock = parseBlock();
            Block elseBlock = parseElsePart();
            return new If(ifTok.getLine(), ifTok.getColumn(), cond, thenBlock, elseBlock);
        } else if (lookahead.getType() == TokenType.KW_WHILE) {
            Token whileTok = match(TokenType.KW_WHILE);
            match(TokenType.TK_DELIM, "(");
            Expr cond = parseExpr();
            match(TokenType.TK_DELIM, ")");
            Block body = parseBlock();
            return new While(whileTok.getLine(), whileTok.getColumn(), cond, body);
        } else if (lookahead.getType() == TokenType.KW_RETURN) {
            Token retTok = match(TokenType.KW_RETURN);
            Expr val = parseReturnExpr();
            match(TokenType.TK_DELIM, ";");
            return new Return(retTok.getLine(), retTok.getColumn(), val);
        } else {
            error("Comando (Stmt)");
            return null;
        }
    }

    private Stmt parseAssignOrCall(Token idTok) {
        if (lookahead.getLexeme().equals("=")) {
            match(TokenType.TK_OP, "=");
            Expr expr = parseExpr();
            match(TokenType.TK_DELIM, ";");
            return new Assign(idTok.getLine(), idTok.getColumn(), idTok.getLexeme(), expr);
        } else if (lookahead.getLexeme().equals("(")) {
            match(TokenType.TK_DELIM, "(");
            List<Expr> args = parseArgList();
            match(TokenType.TK_DELIM, ")");
            match(TokenType.TK_DELIM, ";");
            return new CallStmt(idTok.getLine(), idTok.getColumn(), idTok.getLexeme(), args);
        } else {
            error("'=' ou '('");
            return null;
        }
    }

    private Block parseElsePart() {
        if (lookahead.getType() == TokenType.KW_ELSE) {
            match(TokenType.KW_ELSE);
            return parseBlock();
        } else if (isType() || lookahead.getType() == TokenType.TK_ID ||
                lookahead.getType() == TokenType.KW_IF || lookahead.getType() == TokenType.KW_WHILE ||
                lookahead.getType() == TokenType.KW_RETURN || lookahead.getLexeme().equals("}")) {
            // ε válido: lookahead em FOLLOW(ElsePart)
            return null;
        } else {
            error("'else' ou próximo comando/'}'");
            return null;
        }
    }

    private Expr parseReturnExpr() {
        if (isFirstExpr()) {
            return parseExpr();
        } else if (lookahead.getLexeme().equals(";")) {
            // ε válido: FOLLOW(ReturnExpr) = {;}
            return null;
        } else {
            error("Expressão de retorno ou ';'");
            return null;
        }
    }

    private List<Expr> parseArgList() {
        List<Expr> args = new ArrayList<>();
        if (isFirstExpr()) {
            args.add(parseExpr());
            parseArgListTail(args);
        } else if (lookahead.getLexeme().equals(")")) {
            // ε válido: FOLLOW(ArgList) = {)}
        } else {
            error("Argumento ou ')'");
        }
        return args;
    }

    private void parseArgListTail(List<Expr> args) {
        if (lookahead.getLexeme().equals(",")) {
            match(TokenType.TK_DELIM, ",");
            args.add(parseExpr());
            parseArgListTail(args);
        } else if (lookahead.getLexeme().equals(")")) {
            // ε válido: FOLLOW(ArgListTail) = {)}
        } else {
            error("',' ou ')'");
        }
    }

    // --- Expressões com Precedência e Associatividade à Esquerda ---

    private Expr parseExpr() {
        return parseRel();
    }

    private Expr parseRel() {
        Expr left = parseAdd();
        return parseRelTail(left);
    }

    private Expr parseRelTail(Expr left) {
        if (isRelOp()) {
            Token op = lookahead;
            lookahead = scanner.nextToken();
            Expr right = parseAdd();
            Expr bin = new Bin(op.getLine(), op.getColumn(), op.getLexeme(), left, right);
            return parseRelTail(bin);
        } else if (isFollowExpr()) {
            return left;
        } else {
            error("Operador relacional ou fim de expressão");
            return left;
        }
    }

    private Expr parseAdd() {
        Expr left = parseTerm();
        return parseAddTail(left);
    }

    private Expr parseAddTail(Expr left) {
        if (isAddOp()) {
            Token op = lookahead;
            lookahead = scanner.nextToken();
            Expr right = parseTerm();
            Expr bin = new Bin(op.getLine(), op.getColumn(), op.getLexeme(), left, right);
            return parseAddTail(bin);
        } else if (isRelOp() || isFollowExpr()) {
            return left;
        } else {
            error("Operador aditivo ou fim de expressão");
            return left;
        }
    }

    private Expr parseTerm() {
        Expr left = parseFactor();
        return parseTermTail(left);
    }

    private Expr parseTermTail(Expr left) {
        if (isMulOp()) {
            Token op = lookahead;
            lookahead = scanner.nextToken();
            Expr right = parseFactor();
            Expr bin = new Bin(op.getLine(), op.getColumn(), op.getLexeme(), left, right);
            return parseTermTail(bin);
        } else if (isAddOp() || isRelOp() || isFollowExpr()) {
            return left;
        } else {
            error("Operador multiplicativo ou fim de expressão");
            return left;
        }
    }

    private Expr parseFactor() {
        if (lookahead.getType() == TokenType.TK_OP && lookahead.getLexeme().equals("-")) {
            Token op = lookahead;
            lookahead = scanner.nextToken();
            Expr operand = parseFactor();
            return new Unary(op.getLine(), op.getColumn(), "-", operand);
        }
        return parseBase();
    }

    private Expr parseBase() {
        Token tok = lookahead;
        if (tok.getType() == TokenType.TK_ID) {
            match(TokenType.TK_ID);
            return parseBaseTail(tok);
        } else if (tok.getType() == TokenType.TK_INT_VAL) {
            lookahead = scanner.nextToken();
            return new IntLit(tok.getLine(), tok.getColumn(), Integer.parseInt(tok.getLexeme()));
        } else if (tok.getType() == TokenType.TK_FLOAT_VAL) {
            lookahead = scanner.nextToken();
            return new FloatLit(tok.getLine(), tok.getColumn(), Double.parseDouble(tok.getLexeme()));
        } else if (tok.getType() == TokenType.TK_STRING_VAL) {
            lookahead = scanner.nextToken();
            return new StringLit(tok.getLine(), tok.getColumn(), tok.getLexeme());
        } else if (tok.getType() == TokenType.KW_TRUE || tok.getType() == TokenType.KW_FALSE) {
            lookahead = scanner.nextToken();
            return new BoolLit(tok.getLine(), tok.getColumn(), Boolean.parseBoolean(tok.getLexeme()));
        } else if (tok.getLexeme().equals("(")) {
            match(TokenType.TK_DELIM, "(");
            Expr inner = parseExpr();
            match(TokenType.TK_DELIM, ")");
            return inner; // Parênteses não geram nó na AST!
        } else {
            error("Expressão ou Literal");
            return null;
        }
    }

    private Expr parseBaseTail(Token idTok) {
        if (lookahead.getLexeme().equals("(")) {
            match(TokenType.TK_DELIM, "(");
            List<Expr> args = parseArgList();
            match(TokenType.TK_DELIM, ")");
            return new Call(idTok.getLine(), idTok.getColumn(), idTok.getLexeme(), args);
        } else if (isMulOp() || isAddOp() || isRelOp() || isFollowExpr()) {
            return new Id(idTok.getLine(), idTok.getColumn(), idTok.getLexeme());
        } else {
            error("'(' ou operador/delimitador após identificador");
            return new Id(idTok.getLine(), idTok.getColumn(), idTok.getLexeme());
        }
    }
}