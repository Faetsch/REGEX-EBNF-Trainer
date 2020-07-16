package model;

/**
 * Model class that represents and EBNF Grammar
 */
public class EbnfGrammar {
    private EbnfRule[] ebnfRules;
    //rule 0 is starting rule

    public EbnfGrammar(EbnfRule[] ebnfRules){
        this.ebnfRules=ebnfRules;
    }
    public EbnfRule[] getEbnfRules() {
        return ebnfRules;
    }

    public String toText(){
        String string="";
        for(EbnfRule ebnfRule: ebnfRules){
            string=string.concat(ebnfRule.toText())+"\n";
        }
        return string;
    }

    public void setEbnfRules(EbnfRule[] ebnfRules) {
        this.ebnfRules = ebnfRules;
    }
}
