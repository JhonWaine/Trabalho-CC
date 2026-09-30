import java.util.HashMap;
import java.util.Map;

public class Scanner {
    private String input;
    private int pos;
    private int line;
    private int column;
    private Map<String, TokenType> keywords;

    public Scanner(String input) {
        this.input = input;
        this.pos = 0;
        this.line = 1;
        this.column = 1;

        // Tabela fechada de palavras reservadas
        this.keywords = new HashMap<>();
        keywords.put("int", TokenType.KW_INT);
        keywords.put("double", TokenType.KW_DOUBLE);
        keywords.put("bool", TokenType.KW_BOOL);
        keywords.put("char", TokenType.KW_CHAR);
        keywords.put("string", TokenType.KW_STRING);
        keywords.put("void", TokenType.KW_VOID);
        keywords.put("if", TokenType.KW_IF);
        keywords.put("else", TokenType.KW_ELSE);
        keywords.put("while", TokenType.KW_WHILE);
        keywords.put("return", TokenType.KW_RETURN);
        keywords.put("true", TokenType.KW_TRUE);
        keywords.put("false", TokenType.KW_FALSE);
    }

    // Olha o caractere atual sem consumir
    private char peek() {
        if (pos >= input.length()) return '\0';
        return input.charAt(pos);
    }

    // Consome o caractere e move o cursor de linha/coluna
    private char advance() {
        char c = peek();
        pos++;
        if (c == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        return c;
    }

    private boolean hasNext() {
        return pos < input.length();
    }

    // Devolve o próximo token válido e lida com erros
    public Token nextToken() {
        while (hasNext()) {
            char c = peek();
            int startLine = line;
            int startCol = column;

            // 1. Ignorar Espaços em branco e quebras de linha
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                advance();
                continue;
            }

            // 2. Comentários (//) e Divisão (/)
            if (c == '/') {
                advance();
                if (peek() == '/') {
                    // Comentário de linha: consome até o fim da linha ou EOF
                    while (peek() != '\n' && peek() != '\0') {
                        advance();
                    }
                    continue; // Volta para buscar o próximo token após o comentário
                } else {
                    return new Token(TokenType.TK_OP, "/", startLine, startCol);
                }
            }

            // 3. Identificadores e Palavras Reservadas
            if (Character.isLetter(c)) {
                StringBuilder sb = new StringBuilder();
                while (Character.isLetterOrDigit(peek())) {
                    sb.append(advance());
                }
                String lexeme = sb.toString();
                // Verifica na tabela: se existir, é palavra-chave; senão, identificador normal
                TokenType type = keywords.getOrDefault(lexeme, TokenType.TK_ID);
                return new Token(type, lexeme, startLine, startCol);
            }

            // 4. Literais Numéricos (Inteiros e Decimais)
            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                while (Character.isDigit(peek())) {
                    sb.append(advance());
                }
                if (peek() == '.') {
                    sb.append(advance()); // consome o '.'
                    if (Character.isDigit(peek())) { // Float válido
                        while (Character.isDigit(peek())) {
                            sb.append(advance());
                        }
                        return new Token(TokenType.TK_FLOAT_VAL, sb.toString(), startLine, startCol);
                    } else { // Erro: Número terminado em ponto
                        System.err.printf("Erro Léxico [%d:%d]: Número decimal mal formatado '%s'\n", startLine, startCol, sb.toString());
                        continue; // Recupera-se e continua
                    }
                }
                return new Token(TokenType.TK_INT_VAL, sb.toString(), startLine, startCol);
            }

            // 5. Strings
            if (c == '"') {
                StringBuilder sb = new StringBuilder();
                sb.append(advance()); // consome aspas de abertura
                boolean closed = false;

                // Consome até fechar as aspas ou encontrar erro (fim de linha / EOF)
                while (peek() != '\0' && peek() != '\n' && peek() != '\r') {
                    char nextChar = advance();
                    sb.append(nextChar);
                    if (nextChar == '"') {
                        closed = true;
                        break;
                    }
                }

                if (closed) {
                    return new Token(TokenType.TK_STRING_VAL, sb.toString(), startLine, startCol);
                } else {
                    // Erro: String não fechada (EOF ou quebra de linha)
                    System.err.printf("Erro Léxico [%d:%d]: String não fechada '%s'\n", startLine, startCol, sb.toString());
                    continue; // Recupera-se pulando este token com erro
                }
            }

            // 6. Operadores - Regra do Maximal Munch (tenta o maior prefixo)
            if (c == '=') {
                advance();
                if (peek() == '=') { advance(); return new Token(TokenType.TK_OP, "==", startLine, startCol); }
                return new Token(TokenType.TK_OP, "=", startLine, startCol);
            }
            if (c == '!') {
                advance();
                if (peek() == '=') { advance(); return new Token(TokenType.TK_OP, "!=", startLine, startCol); }
                System.err.printf("Erro Léxico [%d:%d]: Caractere inválido '!' isolado\n", startLine, startCol);
                continue;
            }
            if (c == '<') {
                advance();
                if (peek() == '=') { advance(); return new Token(TokenType.TK_OP, "<=", startLine, startCol); }
                return new Token(TokenType.TK_OP, "<", startLine, startCol);
            }
            if (c == '>') {
                advance();
                if (peek() == '=') { advance(); return new Token(TokenType.TK_OP, ">=", startLine, startCol); }
                return new Token(TokenType.TK_OP, ">", startLine, startCol);
            }
            if (c == '+' || c == '-' || c == '*') {
                advance();
                return new Token(TokenType.TK_OP, String.valueOf(c), startLine, startCol);
            }

            // 7. Delimitadores
            if ("(){},;".indexOf(c) != -1) {
                advance();
                return new Token(TokenType.TK_DELIM, String.valueOf(c), startLine, startCol);
            }

            // 8. Tratamento de Erro - Caractere fora do alfabeto
            System.err.printf("Erro Léxico [%d:%d]: Caractere inválido '%c'\n", startLine, startCol, c);
            advance(); // pula o caractere inválido
        }

        return new Token(TokenType.TK_EOF, "EOF", line, column);
    }
}