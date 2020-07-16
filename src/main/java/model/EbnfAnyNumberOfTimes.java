package model;

/**
 * Model that represents EBNF loops.
 */
public class EbnfAnyNumberOfTimes extends EbnfElement {
    private EbnfElement ebnfElement;

    public EbnfElement getEbnfElement() {
        return ebnfElement;
    }

    public String toText(){
        return "{ "+ebnfElement.toText()+" }";
    }

    public EbnfAnyNumberOfTimes(EbnfElement ebnfElement) {
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
        return obj instanceof EbnfAnyNumberOfTimes && this.hashCode() == obj.hashCode();
    }
}
