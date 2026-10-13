public class ParserTest {
    public static void main(String[] args) {
        System.out.println("=== TESTES DO ANALISADOR SINTÁTICO ===\n");

        // --- 1. Casos Válidos ---
        System.out.println("--- PROGRAMAS VÁLIDOS ---");
        runTest("Declarações Básicas", "int x; double y; string texto;");
        runTest("Controle de Fluxo", "int main() { if (x == 1) { return true; } else { x = x + 1; } }");
        runTest("Laço e Função", "void calcular(int a, double b) { while(a < 10) { a = a + 1; } return; }");

        // --- 2. Casos Inválidos (Erros Singulares) ---
        System.out.println("\n--- PROGRAMAS INVÁLIDOS (UM ERRO) ---");
        runTest("Falta de Ponto e Vírgula", "int x = 10 \n double y = 20;");
        runTest("Parêntese Desbalanceado", "if (x > 5 { return; }");
        runTest("Tipo Inválido em Parâmetro", "void f(numero a) { }"); // 'numero' não é tipo

        // --- 3. Caso Inválido (Múltiplos Erros - Recuperação Pânico) ---
        System.out.println("\n--- MÚLTIPLOS ERROS (TESTE DE MODO PÂNICO) ---");
        String multErrors =
                "int main() {\n" +
                        "   int x = ;       // Erro 1: Falta expressão\n" +
                        "   if (x > ) {     // Erro 2: Falta expressão na condição\n" +
                        "       return 0;\n" +
                        "   }\n" +
                        "   imprimir(x)     // Erro 3: Falta ponto e vírgula\n" +
                        "}";
        runTest("Recuperação Pânico em Cascata", multErrors);
    }

    private static void runTest(String testName, String sourceCode) {
        System.out.println("\n>> Teste: " + testName);
        System.out.println("Código: \n" + sourceCode.trim());

        Scanner scanner = new Scanner(sourceCode);
        Parser parser = new Parser(scanner);

        boolean success = parser.parse();
        if (success) {
            System.out.println("[RESULTADO]: SUCESSO - NENHUM ERRO SINTÁTICO ENCONTRADO.");
        } else {
            System.out.println("[RESULTADO]: FALHA - ERROS SINTÁTICOS REPORTADOS (ESPERADO PARA ESTE TESTE).");
        }
        System.out.println("-------------------------------------------------");
    }
}