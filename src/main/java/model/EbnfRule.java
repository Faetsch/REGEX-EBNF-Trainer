package model;

import java.util.Objects;

/**
 * Model class that represents EBNF rules
 */

public class EbnfRule {
    private NonTerminalSymbol leftSymbol;
    //not sure if using that
    private EbnfElement rightSide;

    public EbnfRule(NonTerminalSymbol leftSymbol, EbnfElement rightSide) {
        this.leftSymbol = leftSymbol;
        this.rightSide = rightSide;
    }

    public String toText(){
        return leftSymbol.toText()+" = "+rightSide.toText();
    }

    /**
     * makes the rule simpler by doing the following things: (maybe in class?)
     * if EbnfSum or EbnfOrSum and only one element, return rule with only that one element
     * If EbnfSum contains EbnfEmptys, remove those
     * if EbnfSum contains no elements, make into EbnfEmpty
     * if EbnfOrSum contains no elements, return null
     * if EbnfOptional contains EbnfEmpty, make into EbnfEmpty
     * @return this Rule in simplified. maybe needs to return an array
     */
    public EbnfRule simplify(){
        //todo: this if needed
        return null;
    }

    public NonTerminalSymbol getLeftSymbol() {
        return leftSymbol;
    }

    public void setLeftSymbol(NonTerminalSymbol leftSymbol) {
        this.leftSymbol = leftSymbol;
    }

    public EbnfElement getRightSide() {
        return rightSide;
    }

    public void setRightSide(EbnfElement rightSide) {
        this.rightSide = rightSide;
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftSymbol.hashCode(),rightSide.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null){
            return false;
        }
        return obj instanceof EbnfRule && obj.hashCode()==this.hashCode();
    }
}
