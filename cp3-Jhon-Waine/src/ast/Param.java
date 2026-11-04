public class Param extends Node {
    private final String type;
    private final String name;

    public Param(int line, int column, String type, String name) {
        super(line, column);
        this.type = type;
        this.name = name;
    }

    public String getType() { return type; }
    public String getName() { return name; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitParam(this);
    }
}