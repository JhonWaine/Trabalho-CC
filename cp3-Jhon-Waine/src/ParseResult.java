import java.util.List;

public class ParseResult {
    private final Program ast;
    private final List<String> errors;

    public ParseResult(Program ast, List<String> errors) {
        // Se houver erros sintáticos, a árvore não pode ser exposta como válida
        this.ast = errors.isEmpty() ? ast : null;
        this.errors = errors;
    }

    public boolean isValid() {
        return errors.isEmpty() && ast != null;
    }

    public Program getAst() {
        return ast;
    }

    public List<String> getErrors() {
        return errors;
    }
}