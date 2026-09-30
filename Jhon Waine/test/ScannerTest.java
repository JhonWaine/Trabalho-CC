public class ScannerTest {

    public static void main(String[] args) {
        System.out.println("=== INICIANDO SUÍTE DE TESTES DO SCANNER ===\n");

        testarCategoriasValidas();
        testarErrosLexicos();
        testarCodigoRealista();

        System.out.println("=== TODOS OS TESTES FORAM EXECUTADOS COM SUCESSO ===");
    }

    // Função auxiliar para validar os tokens
    private static void assertToken(Scanner scanner, TokenType tipoEsperado, String lexemaEsperado) {
        Token t = scanner.nextToken();
        if (t.getType() != tipoEsperado || !t.getLexeme().equals(lexemaEsperado)) {
            throw new RuntimeException(String.format(
                    "Falha no Teste! Esperado: %s ('%s') | Obtido: %s ('%s')",
                    tipoEsperado, lexemaEsperado, t.getType(), t.getLexeme()
            ));
        }
        System.out.println("  [OK] " + t);
    }

    private static void testarCategoriasValidas() {
        System.out.println("1. TESTE: Pelo menos um caso válido de cada categoria (5 categorias)");
        // Palavra reservada, ID, Operador, Literal Numérico e String
        String input = "int total = 100;\nstring msg = \"ok\";";
        Scanner scanner = new Scanner(input);

        assertToken(scanner, TokenType.KW_INT, "int");          // Palavra reservada
        assertToken(scanner, TokenType.TK_ID, "total");         // Identificador
        assertToken(scanner, TokenType.TK_OP, "=");             // Operador
        assertToken(scanner, TokenType.TK_INT_VAL, "100");      // Literal numérico (inteiro)
        assertToken(scanner, TokenType.TK_DELIM, ";");          // Delimitador
        assertToken(scanner, TokenType.KW_STRING, "string");    // Palavra reservada
        assertToken(scanner, TokenType.TK_ID, "msg");           // Identificador
        assertToken(scanner, TokenType.TK_OP, "=");             // Operador
        assertToken(scanner, TokenType.TK_STRING_VAL, "ok");    // String
        assertToken(scanner, TokenType.TK_DELIM, ";");          // Delimitador
        assertToken(scanner, TokenType.TK_EOF, "EOF");
        System.out.println("-> Categorias válidas testadas com sucesso.\n");
    }

    private static void testarErrosLexicos() {
        System.out.println("2. TESTE: Casos de erro com recuperação contínua");

        // Erro 1: String não fechada até o final da linha
        System.out.println("\n--- Testando String não fechada até fim de linha ---");
        Scanner scannerErro1 = new Scanner("string erro1 = \"sem aspas \n int x = 1;");
        assertToken(scannerErro1, TokenType.KW_STRING, "string");
        assertToken(scannerErro1, TokenType.TK_ID, "erro1");
        assertToken(scannerErro1, TokenType.TK_OP, "=");
        // O Scanner detecta a string quebrada, reporta o erro (System.err) e pula para o 'int'
        assertToken(scannerErro1, TokenType.KW_INT, "int");

        // Erro 2: String não fechada até o EOF
        System.out.println("\n--- Testando String não fechada até EOF ---");
        Scanner scannerErro2 = new Scanner("string erro2 = \"fim de arquivo");
        assertToken(scannerErro2, TokenType.KW_STRING, "string");
        assertToken(scannerErro2, TokenType.TK_ID, "erro2");
        assertToken(scannerErro2, TokenType.TK_OP, "=");
        // Bate no EOF com string aberta, reporta erro e emite EOF
        assertToken(scannerErro2, TokenType.TK_EOF, "EOF");

        // Erro 3: Caractere fora do alfabeto
        System.out.println("\n--- Testando Caractere Inválido ---");
        Scanner scannerErro3 = new Scanner("int valor $ = 10;");
        assertToken(scannerErro3, TokenType.KW_INT, "int");
        assertToken(scannerErro3, TokenType.TK_ID, "valor");
        // O '$' gera erro, o scanner reporta e recupera a partir do '='
        assertToken(scannerErro3, TokenType.TK_OP, "=");
        assertToken(scannerErro3, TokenType.TK_INT_VAL, "10");

        System.out.println("-> Erros léxicos validados. Scanner não travou e continuou lendo.\n");
    }

    private static void testarCodigoRealista() {
        System.out.println("3. TESTE: Trecho de código realista com comentários e espaços");
        String input =
                "// Função para somar limite\n" +
                        "while (x <= 100) {\n" +
                        "    x = x + 1; // Incremento\n" +
                        "}";

        Scanner scanner = new Scanner(input);
        // O scanner deve ignorar completamente os comentários de linha e espaços
        assertToken(scanner, TokenType.KW_WHILE, "while");
        assertToken(scanner, TokenType.TK_DELIM, "(");
        assertToken(scanner, TokenType.TK_ID, "x");
        assertToken(scanner, TokenType.TK_OP, "<="); // Teste de maximal munch
        assertToken(scanner, TokenType.TK_INT_VAL, "100");
        assertToken(scanner, TokenType.TK_DELIM, ")");
        assertToken(scanner, TokenType.TK_DELIM, "{");
        assertToken(scanner, TokenType.TK_ID, "x");
        assertToken(scanner, TokenType.TK_OP, "=");
        assertToken(scanner, TokenType.TK_ID, "x");
        assertToken(scanner, TokenType.TK_OP, "+");
        assertToken(scanner, TokenType.TK_INT_VAL, "1");
        assertToken(scanner, TokenType.TK_DELIM, ";");
        assertToken(scanner, TokenType.TK_DELIM, "}");
        assertToken(scanner, TokenType.TK_EOF, "EOF");
        System.out.println("-> Código realista tokenizado corretamente do início ao fim.\n");
    }
}