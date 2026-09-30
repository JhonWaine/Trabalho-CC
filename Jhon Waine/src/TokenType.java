public enum TokenType {
    // Palavras Reservadas (Tipos e Controle)
    KW_INT, KW_DOUBLE, KW_BOOL, KW_CHAR, KW_STRING, KW_VOID,
    KW_IF, KW_ELSE, KW_WHILE, KW_RETURN, KW_TRUE, KW_FALSE,

    // Identificadores e Literais
    TK_ID,
    TK_INT_VAL,
    TK_FLOAT_VAL,
    TK_STRING_VAL,

    // Operadores e Delimitadores
    TK_OP,
    TK_DELIM,

    // Fim de Arquivo (End Of File)
    TK_EOF
}