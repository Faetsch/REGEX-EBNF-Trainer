package model;

/**
 * This class represents EBNF non-terminals (left sides)
 */
public class NonTerminalSymbol extends EbnfElement{
    private String name;

    public String getName() {
        return name;
    }

    public String toText(){
        return "<"+getName()+">";
    }

    public void setName(String name) {
        this.name = name;
    }

    public NonTerminalSymbol(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        return obj instanceof NonTerminalSymbol && this.hashCode() == obj.hashCode();
    }
}
