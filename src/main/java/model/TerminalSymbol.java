package model;

/**
 * Model class that represents EBNF terminal symbols
 */
public class TerminalSymbol extends EbnfElement{
    private String inhalt;

    public TerminalSymbol(String inhalt) {
        this.inhalt = inhalt;
    }

    public String getInhalt() {
        return inhalt;
    }

    @Override
    public String toText(){
        return "\""+getInhalt()+"\"";
    }

    @Override
    public EbnfElement simplify() {
        if (inhalt.length()<1){
            return new EbnfEmpty();
        }
        if(inhalt.length()==1){
            return this;
        }
        else{
            char[] chars=inhalt.toCharArray();
            TerminalSymbol[] terminalSymbols=new TerminalSymbol[chars.length];
            for(int i=0; i<chars.length; i++){
                terminalSymbols[i]=new TerminalSymbol(Character.toString(chars[i]));
            }
            return new EbnfSum(terminalSymbols);
        }
    }

    public void setInhalt(String inhalt) {
        this.inhalt = inhalt;
    }

    @Override
    public int hashCode() {
        return inhalt.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        return obj instanceof TerminalSymbol && this.hashCode() == obj.hashCode();
    }
}
