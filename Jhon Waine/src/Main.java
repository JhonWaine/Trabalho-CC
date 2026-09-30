public class Main {
    public static void main(String[] args) {
        // Código-fonte de teste com casos válidos e erros embutidos
        String codigoFonte =
                "int main() {\n" +
                        "    // Declarando variaveis\n" +
                        "    double taxa = 3.14;\n" +
                        "    string texto = \"olá mundo\";\n" +
                        "    \n" +
                        "    if (taxa >= 2.0) {\n" +
                        "        return true;\n" +
                        "    }\n" +
                        "    @ \n" + // Erro Léxico 1: Caractere inválido
                        "    string erro = \"nao fechada\n" + // Erro Léxico 2: String sem fechamento antes da quebra
                        "    return false;\n" +
                        "}";

        System.out.println("Iniciando a Análise Léxica...\n");

        Scanner scanner = new Scanner(codigoFonte);
        Token token;

        do {
            token = scanner.nextToken();
            // Apenas imprime os tokens válidos capturados.
            // Os erros serão impressos no System.err automaticamente pelo Scanner.
            if (token.getType() != TokenType.TK_EOF) {
                System.out.println(token);
            }
        } while (token.getType() != TokenType.TK_EOF);

        System.out.println("\nFim da Análise Léxica. (EOF encontrado)");
    }
}