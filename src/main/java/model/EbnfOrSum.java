package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Model class that represents EBNF disjunction
 */

public class EbnfOrSum extends EbnfElement{
    private EbnfElement[] ebnfElemente;
    //groub of elements conbined with or
    public EbnfElement[] getEbnfElemente() {
        return ebnfElemente;
    }
    public EbnfOrSum(EbnfElement[] ebnfElemente) {
        this.ebnfElemente = ebnfElemente;
        removeEmptys();
    }

    public String toText(){
        if(ebnfElemente.length==0){return "OrSum()";}
        else{
            String string;
            string="OrSum( " + ebnfElemente[0].toText();
            for (int i=1; i<ebnfElemente.length;i++){
                string=string.concat(" | "+ebnfElemente[i].toText());
            }
            return string+" )";
        }
    }


    //should not remove Emptys in OrSums, as they are importent here.
    public void removeEmptys(){
        List<EbnfElement> ebnfElementList;
        List<EbnfElement> ebnfElementList1 = Arrays.<EbnfElement>asList(ebnfElemente);
        ebnfElementList = new ArrayList<>(ebnfElementList1);
        for(int i=0;i<ebnfElementList.size();i++){
            if(ebnfElementList.get(i) instanceof EbnfEmpty) {
                //they dont get removed because they are important
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
        return obj instanceof EbnfOrSum && this.hashCode() == obj.hashCode();
    }
}
