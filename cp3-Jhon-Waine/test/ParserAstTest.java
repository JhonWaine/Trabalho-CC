import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ParserAstTest {

    public static void main(String[] args) throws IOException {
        System.out.println("=== INICIANDO SUÍTE DE TESTES DO CHECKPOINT 3 (AST & VISITORS) ===\n");

        testarProgramasValidosEArquivosEsperados();
        testarPrecedenciaEAssociatividade();
        testarDescarteDeParentesesRedundantes();
        testarRoundTripIdaEVolta();
        testarRegressaoCP2();

        System.out.println("\n=== TODOS OS TESTES DO CP3 PASSARAM COM SUCESSO! ===");
    }

    private static void assertEquals(String testName, String expected, String actual) {
        String normExp = expected.replace("\r\n", "\n").trim();
        String normAct = actual.replace("\r\n", "\n").trim();
        if (!normExp.equals(normAct)) {
            throw new RuntimeException(String.format(
                "FALHA em [%s]!\n--- Esperado ---\n%s\n--- Obtido ---\n%s",
                testName, normExp, normAct
            ));
        }
        System.out.println("  [OK] " + testName);
    }

    private static void assertTrue(String testName, boolean condition) {
        if (!condition) {
            throw new RuntimeException("FALHA em [" + testName + "]: condição esperada era true.");
        }
        System.out.println("  [OK] " + testName);
    }

    // 1. Três programas válidos comparados com test/esperado/*.txt
    private static void testarProgramasValidosEArquivosEsperados() throws IOException {
        System.out.println("1. TESTE: Programas válidos vs. Árvores esperadas (test/esperado/)");
        AstPrinter printer = new AstPrinter();

        String prog1 = "int x; double y = 3.14; string texto = \"ok\";";
        String exp1 = new String(Files.readAllBytes(Paths.get("test/esperado/prog1.txt")));
        ParseResult res1 = new Parser(new Scanner(prog1)).parseProgramResult();
        assertTrue("Prog 1 válido", res1.isValid());
        assertEquals("AST Prog 1 (Declarações e Tipos)", exp1, printer.print(res1.getAst()));

        String prog2 = "int main() { if (x == 1) { return true; } else { x = x + 1; } }";
        String exp2 = new String(Files.readAllBytes(Paths.get("test/esperado/prog2.txt")));
        ParseResult res2 = new Parser(new Scanner(prog2)).parseProgramResult();
        assertTrue("Prog 2 válido", res2.isValid());
        assertEquals("AST Prog 2 (Seleção If/Else)", exp2, printer.print(res2.getAst()));

        String prog3 = "void calcular(int a, double b) { while(a < 10) { a = a + 1; } return; }";
        String exp3 = new String(Files.readAllBytes(Paths.get("test/esperado/prog3.txt")));
        ParseResult res3 = new Parser(new Scanner(prog3)).parseProgramResult();
        assertTrue("Prog 3 válido", res3.isValid());
        assertEquals("AST Prog 3 (Repetição e Procedimento)", exp3, printer.print(res3.getAst()));
        System.out.println();
    }

    // 2. Precedência e Associatividade
    private static void testarPrecedenciaEAssociatividade() {
        System.out.println("2. TESTE: Precedência e Associatividade de Operadores");
        AstPrinter printer = new AstPrinter();

        // Caso A: a + b * c -> Bin(+, Id(a), Bin(*, Id(b), Id(c)))
        Expr e1 = new Parser(new Scanner("a + b * c")).parseStandaloneExpr();
        String exp1 = "Bin(+)\n  Id(a)\n  Bin(*)\n    Id(b)\n    Id(c)";
        assertEquals("Precedência: a + b * c", exp1, printer.print(e1));

        // Caso B: a - b - c -> Bin(-, Bin(-, Id(a), Id(b)), Id(c))
        Expr e2 = new Parser(new Scanner("a - b - c")).parseStandaloneExpr();
        String exp2 = "Bin(-)\n  Bin(-)\n    Id(a)\n    Id(b)\n  Id(c)";
        assertEquals("Associatividade à esquerda: a - b - c", exp2, printer.print(e2));

        // Caso C: a - (b - c) -> Bin(-, Id(a), Bin(-, Id(b), Id(c)))
        Expr e3 = new Parser(new Scanner("a - (b - c)")).parseStandaloneExpr();
        String exp3 = "Bin(-)\n  Id(a)\n  Bin(-)\n    Id(b)\n    Id(c)";
        assertEquals("Parênteses alterando precedência: a - (b - c)", exp3, printer.print(e3));
        System.out.println();
    }

    // 3. A AST não é a árvore de derivação (parênteses redundantes)
    private static void testarDescarteDeParentesesRedundantes() {
        System.out.println("3. TESTE: Descarte de Parênteses Redundantes (((a))) vs a");
        AstPrinter printer = new AstPrinter();

        Expr comParenteses = new Parser(new Scanner("(((a)))")).parseStandaloneExpr();
        Expr semParenteses = new Parser(new Scanner("a")).parseStandaloneExpr();

        String printCom = printer.print(comParenteses);
        String printSem = printer.print(semParenteses);

        assertEquals("(((a))) gera apenas Id(a)", "Id(a)", printCom);
        assertEquals("(((a))) == a na AST", printSem, printCom);
        System.out.println();
    }

    // 4. Ida e volta (Round-Trip) com SourceReprinter e AstPrinter
    private static void testarRoundTripIdaEVolta() {
        System.out.println("4. TESTE: Ida e Volta (Round-Trip) com Reimpressor e Impressor");
        String[] programas = {
            "int x; double y = 3.14; string texto = \"ok\";",
            "int main() { if (x == 1) { return true; } else { x = x + 1 - 2 * 3; } }",
            "void calcular(int a, double b) { while(a < 10) { a = a - 1 - 2; imprimir(a, -b); } return; }"
        };

        AstPrinter astPrinter = new AstPrinter();
        SourceReprinter reprinter = new SourceReprinter();

        for (int i = 0; i < programas.length; i++) {
            // 1ª leitura e reimpressão
            ParseResult r1 = new Parser(new Scanner(programas[i])).parseProgramResult();
            assertTrue("Round-trip prog " + (i + 1) + " parse inicial válido", r1.isValid());
            String ast1 = astPrinter.print(r1.getAst());
            String src1 = reprinter.reprint(r1.getAst());

            // 2ª leitura (sobre o código reimpresso) e 2ª reimpressão
            ParseResult r2 = new Parser(new Scanner(src1)).parseProgramResult();
            assertTrue("Round-trip prog " + (i + 1) + " re-parse válido", r2.isValid());
            String ast2 = astPrinter.print(r2.getAst());
            String src2 = reprinter.reprint(r2.getAst());

            assertEquals("Round-trip Prog " + (i + 1) + " (Texto Reimpresso 1 == 2)", src1, src2);
            assertEquals("Round-trip Prog " + (i + 1) + " (AST 1 == AST 2)", ast1, ast2);
        }
        System.out.println();
    }

    // 5. Regressão do CP2 (erros únicos e múltiplos erros em modo pânico sem NPE)
    private static void testarRegressaoCP2() {
        System.out.println("5. TESTE: Regressão de Erros Sintáticos do CP2 (Modo Pânico e AST nula)");

        ParseResult err1 = new Parser(new Scanner("int x = 10 \n double y = 20;")).parseProgramResult();
        assertTrue("Rejeita falta de ponto e vírgula e invalida AST", !err1.isValid() && err1.getAst() == null);

        ParseResult err2 = new Parser(new Scanner("int main() { if (x > 5 { return 0; } }")).parseProgramResult();
        assertTrue("Rejeita parêntese desbalanceado e invalida AST", !err2.isValid() && err2.getAst() == null);

        ParseResult err3 = new Parser(new Scanner("void f(numero a) { }")).parseProgramResult();
        assertTrue("Rejeita tipo inválido em parâmetro e invalida AST", !err3.isValid() && err3.getAst() == null);

        String multErrors =
            "int main() {\n" +
            "   int x = ;       // Erro 1: Falta expressão\n" +
            "   if (x > ) {     // Erro 2: Falta expressão na condição\n" +
            "       return 0;\n" +
            "   }\n" +
            "   imprimir(x)     // Erro 3: Falta ponto e vírgula\n" +
            "}";
        ParseResult errMult = new Parser(new Scanner(multErrors)).parseProgramResult();
        assertTrue("Múltiplos erros: AST anulada com segurança", !errMult.isValid() && errMult.getAst() == null);
        assertTrue("Múltiplos erros: Capturou os 3 erros na mesma rodada", errMult.getErrors().size() >= 3);
    }
}