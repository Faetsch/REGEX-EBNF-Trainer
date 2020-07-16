package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Model class that represents EBNF concatenation
 */

public class EbnfSum extends EbnfElement {
    private EbnfElement[] ebnfElemente;
    public EbnfSum(){
        ebnfElemente=new EbnfElement[0];
    }
    public EbnfSum(EbnfElement[] ebnfElemente) {
        this.ebnfElemente = ebnfElemente;
        removeEmptys();
    }

    /**
     * removes all the EbnfEmpty elements from this object
     */
    public void removeEmptys(){
        List<EbnfElement> ebnfElementList;
        List<EbnfElement> ebnfElementList1 = Arrays.<EbnfElement>asList(ebnfElemente);
        ebnfElementList = new ArrayList<>(ebnfElementList1);
        for(int i=0;i<ebnfElementList.size();i++){
            if(ebnfElementList.get(i) instanceof EbnfEmpty) {
                ebnfElementList.remove(i);
                i--;
            }
            else if(ebnfElementList.get(i) instanceof EbnfSum) {
                ((EbnfSum) ebnfElementList.get(i)).removeEmptys();
            }
            else if(ebnfElementList.get(i) instanceof EbnfOrSum) {
                ((EbnfOrSum) ebnfElementList.get(i)).removeEmptys();
            }
        }
        ebnfElemente=ebnfElementList.toArray(new EbnfElement[] {});
    }

    @Override
    public EbnfElement simplify(){
        if(this.ebnfElemente.length>=2){
            return this;
        }
        else if(this.ebnfElemente.length==1)
        {
            return this.ebnfElemente[0];
        }
        else {
            return new EbnfEmpty();
        }
    }

    public String toText(){
        if(ebnfElemente.length==0){return "sum()";}
        else{
            String string;
            string="( " + ebnfElemente[0].toText();
            for (int i=1; i<ebnfElemente.length;i++){
                string=string.concat(" + "+ebnfElemente[i].toText());
            }
            return string+" )";
        }
    }

    public EbnfElement[] getEbnfElemente() {
        return ebnfElemente;
    }

    public void setEbnfElemente(EbnfElement[] ebnfElemente) {
        this.ebnfElemente = ebnfElemente;
    }

    @Override
    public int hashCode() {
        int hash=0;
        for(int i=0; i<ebnfElemente.length;i++){
            hash+=ebnfElemente[i].hashCode()*(i+1);
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        return obj instanceof EbnfSum && this.hashCode() == obj.hashCode();
    }
}
