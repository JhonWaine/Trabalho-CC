public class Main {
    public static void main(String[] args) {
        String codigoFonte =
            "int calcular(int a) {\n" +
            "    while (a < 10) {\n" +
            "        if (a == 5) {\n" +
            "            return a;\n" +
            "        }\n" +
            "        a = a + 1;\n" +
            "    }\n" +
            "    return a;\n" +
            "}";

        System.out.println("=== 1. CÓDIGO-FONTE ORIGINAL ===");
        System.out.println(codigoFonte);

        Scanner scanner = new Scanner(codigoFonte);
        Parser parser = new Parser(scanner);
        ParseResult resultado = parser.parseProgramResult();

        if (resultado.isValid()) {
            Program ast = resultado.getAst();

            System.out.println("\n=== 2. ÁRVORE DE SINTAXE ABSTRATA (AstPrinter) ===");
            AstPrinter printer = new AstPrinter();
            System.out.println(printer.print(ast));

            System.out.println("\n=== 3. CÓDIGO REIMPRESSO (SourceReprinter) ===");
            SourceReprinter reprinter = new SourceReprinter();
            System.out.println(reprinter.reprint(ast));
        } else {
            System.out.println("\n O programa contém erros sintáticos e a AST foi invalidada.");
        }
    }
}