package model;

/**
 * abstract class for all things Ebnf. So far, all the children are:
 * EbnfSum
 * EbnfOrSum
 * EbnfOptional
 * EbnfAnyNumberOfTimes
 * EbnfEmpty
 * NonTerminalSymbol
 * TerminalSymbol
 */
public abstract class EbnfElement {


    public EbnfElement simplify(){return this;}
    /**
     * transforms the object into a text representation, for debug purposes mainly
     * @return A string representing the Object
     */
    public abstract String toText();
}
