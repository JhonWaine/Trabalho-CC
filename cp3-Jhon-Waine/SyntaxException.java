// Exceção customizada usada apenas internamente para abortar regras e entrar no catch de Pânico
public class SyntaxException extends RuntimeException {
    public SyntaxException(String message) {
        super(message);
    }
}