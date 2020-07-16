package model;

/**
 * Model class that represents an EBNF Optional statement
 */

public class EbnfOptional extends EbnfElement {
    private EbnfElement ebnfElement;
    //optional element
    public EbnfElement getEbnfElement() {
        return ebnfElement;
    }

    public String toText(){
        return "[ "+ ebnfElement.toText()+" ]";
    }

    public EbnfOptional(EbnfElement ebnfElement) {
        this.ebnfElement = ebnfElement;
    }

    public void setEbnfElement(EbnfElement ebnfElement) {
        this.ebnfElement = ebnfElement;
    }

    @Override
    public int hashCode() {
        return ebnfElement.hashCode()+toText().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        return obj instanceof EbnfOptional && this.hashCode() == obj.hashCode();
    }
}
