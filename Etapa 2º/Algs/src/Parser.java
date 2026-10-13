import java.util.Arrays;
import java.util.List;

public class Parser {
    private Scanner scanner;
    private Token lookahead;
    private boolean hasErrors;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        this.lookahead = scanner.nextToken();
        this.hasErrors = false;
    }

    public boolean parse() {
        parseProgram();
        return !hasErrors;
    }

    // --- Utilitários ---
    private void error(String expected) {
        hasErrors = true;
        System.err.printf("Erro Sintático [%d:%d]: Esperava '%s', encontrou '%s'\n",
                lookahead.getLine(), lookahead.getColumn(), expected, lookahead.getLexeme());
        throw new SyntaxException(expected);
    }

    private void match(TokenType expectedType) {
        if (lookahead.getType() == expectedType) {
            lookahead = scanner.nextToken();
        } else {
            error(expectedType.toString());
        }
    }

    private void match(TokenType expectedType, String expectedLexeme) {
        if (lookahead.getType() == expectedType && lookahead.getLexeme().equals(expectedLexeme)) {
            lookahead = scanner.nextToken();
        } else {
            error(expectedLexeme);
        }
    }

    private boolean isType() {
        return lookahead.getType() == TokenType.KW_INT || lookahead.getType() == TokenType.KW_DOUBLE ||
                lookahead.getType() == TokenType.KW_BOOL || lookahead.getType() == TokenType.KW_CHAR ||
                lookahead.getType() == TokenType.KW_STRING || lookahead.getType() == TokenType.KW_VOID;
    }

    private boolean isBase() {
        return lookahead.getType() == TokenType.TK_ID || lookahead.getType() == TokenType.TK_INT_VAL ||
                lookahead.getType() == TokenType.TK_FLOAT_VAL || lookahead.getType() == TokenType.TK_STRING_VAL ||
                lookahead.getType() == TokenType.KW_TRUE || lookahead.getType() == TokenType.KW_FALSE ||
                lookahead.getLexeme().equals("(");
    }

    // Sincroniza o parser após um erro para evitar cascata/loops infinitos (Modo Pânico)
    private void panicModeSync() {
        while (lookahead.getType() != TokenType.TK_EOF) {
            // Sincroniza nos FOLLOWs principais de declarações e sentenças
            if (lookahead.getType() == TokenType.TK_DELIM && (lookahead.getLexeme().equals(";") || lookahead.getLexeme().equals("}"))) {
                if (lookahead.getLexeme().equals(";")) {
                    lookahead = scanner.nextToken(); // Consome o ponto e vírgula
                }
                break;
            }
            if (isType() || lookahead.getType() == TokenType.KW_IF || lookahead.getType() == TokenType.KW_WHILE) {
                break;
            }
            lookahead = scanner.nextToken();
        }
    }

    // --- Regras da Gramática (Uma função por Não-Terminal) ---

    private void parseProgram() {
        parseDeclList();
        if (lookahead.getType() != TokenType.TK_EOF) {
            System.err.printf("Erro Sintático [%d:%d]: Tokens extras no final do arquivo.\n", lookahead.getLine(), lookahead.getColumn());
            hasErrors = true;
        }
    }

    private void parseDeclList() {
        if (isType()) {
            try {
                parseDecl();
            } catch (SyntaxException e) {
                panicModeSync();
            }
            parseDeclList();
        }
        // ε (Vazio é aceito se o lookahead estiver no FOLLOW(DeclList), que é EOF. Tratado implicitamente)
    }

    private void parseDecl() {
        parseType();
        match(TokenType.TK_ID);
        parseDeclTail();
    }

    private void parseDeclTail() {
        if (lookahead.getLexeme().equals(";")) {
            match(TokenType.TK_DELIM, ";");
        } else if (lookahead.getLexeme().equals("(")) {
            match(TokenType.TK_DELIM, "(");
            parseParamList();
            match(TokenType.TK_DELIM, ")");
            parseBlock();
        } else {
            error("';' ou '('");
        }
    }

    private void parseType() {
        if (isType()) {
            lookahead = scanner.nextToken();
        } else {
            error("Tipo válido (int, double, etc)");
        }
    }

    private void parseParamList() {
        if (isType()) {
            parseType();
            match(TokenType.TK_ID);
            parseParamListTail();
        }
    }

    private void parseParamListTail() {
        if (lookahead.getLexeme().equals(",")) {
            match(TokenType.TK_DELIM, ",");
            parseType();
            match(TokenType.TK_ID);
            parseParamListTail();
        }
    }

    private void parseBlock() {
        match(TokenType.TK_DELIM, "{");
        parseStmtList();
        match(TokenType.TK_DELIM, "}");
    }

    private void parseStmtList() {
        List<TokenType> firstStmt = Arrays.asList(TokenType.TK_ID, TokenType.KW_IF, TokenType.KW_WHILE, TokenType.KW_RETURN);
        while (isType() || firstStmt.contains(lookahead.getType())) {
            try {
                parseStmt();
            } catch (SyntaxException e) {
                panicModeSync();
            }
        }
    }

    private void parseStmt() {
        if (isType()) {
            parseType();
            match(TokenType.TK_ID);
            match(TokenType.TK_DELIM, ";");
        } else if (lookahead.getType() == TokenType.TK_ID) {
            match(TokenType.TK_ID);
            parseAssignOrCall();
        } else if (lookahead.getType() == TokenType.KW_IF) {
            match(TokenType.KW_IF);
            match(TokenType.TK_DELIM, "(");
            parseExpr();
            match(TokenType.TK_DELIM, ")");
            parseBlock();
            parseElsePart();
        } else if (lookahead.getType() == TokenType.KW_WHILE) {
            match(TokenType.KW_WHILE);
            match(TokenType.TK_DELIM, "(");
            parseExpr();
            match(TokenType.TK_DELIM, ")");
            parseBlock();
        } else if (lookahead.getType() == TokenType.KW_RETURN) {
            match(TokenType.KW_RETURN);
            parseReturnExpr();
            match(TokenType.TK_DELIM, ";");
        } else {
            error("Comando (Stmt)");
        }
    }

    private void parseAssignOrCall() {
        if (lookahead.getLexeme().equals("=")) {
            match(TokenType.TK_OP, "=");
            parseExpr();
            match(TokenType.TK_DELIM, ";");
        } else if (lookahead.getLexeme().equals("(")) {
            match(TokenType.TK_DELIM, "(");
            parseArgList();
            match(TokenType.TK_DELIM, ")");
            match(TokenType.TK_DELIM, ";");
        } else {
            error("'=' ou '('");
        }
    }

    private void parseElsePart() {
        if (lookahead.getType() == TokenType.KW_ELSE) {
            match(TokenType.KW_ELSE);
            parseBlock();
        }
    }

    private void parseReturnExpr() {
        if (isBase()) {
            parseExpr();
        }
    }

    private void parseArgList() {
        if (isBase()) {
            parseExpr();
            parseArgListTail();
        }
    }

    private void parseArgListTail() {
        if (lookahead.getLexeme().equals(",")) {
            match(TokenType.TK_DELIM, ",");
            parseExpr();
            parseArgListTail();
        }
    }

    private void parseExpr() {
        parseBase();
        parseExprTail();
    }

    private void parseExprTail() {
        if (lookahead.getType() == TokenType.TK_OP) {
            lookahead = scanner.nextToken(); // Consome qualquer operador relacional ou matemático
            parseBase();
            parseExprTail();
        }
    }

    private void parseBase() {
        if (lookahead.getType() == TokenType.TK_ID) {
            match(TokenType.TK_ID);
            parseBaseTail();
        } else if (lookahead.getType() == TokenType.TK_INT_VAL || lookahead.getType() == TokenType.TK_FLOAT_VAL ||
                lookahead.getType() == TokenType.TK_STRING_VAL || lookahead.getType() == TokenType.KW_TRUE ||
                lookahead.getType() == TokenType.KW_FALSE) {
            lookahead = scanner.nextToken(); // Consome o literal
        } else if (lookahead.getLexeme().equals("(")) {
            match(TokenType.TK_DELIM, "(");
            parseExpr();
            match(TokenType.TK_DELIM, ")");
        } else {
            error("Expressão ou Literal");
        }
    }

    private void parseBaseTail() {
        if (lookahead.getLexeme().equals("(")) {
            match(TokenType.TK_DELIM, "(");
            parseArgList();
            match(TokenType.TK_DELIM, ")");
        }
    }
}