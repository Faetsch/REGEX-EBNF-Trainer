package model;

/**
 * Model class that represents an empty statement
 */
public class EbnfEmpty extends EbnfElement {
    public String toText(){
        return "<Empty>";
    }

    @Override
    public int hashCode() {
        return 42;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        return obj instanceof EbnfEmpty;
    }

}
