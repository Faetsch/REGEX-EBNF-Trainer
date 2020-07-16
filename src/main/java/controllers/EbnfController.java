package controllers;

import model.EBNFTask;
import model.EbnfGrammar;

/**
 * Controller that handles EBNF-Tasks. Analogue to RegexController.
 */

public class EbnfController {
    private CykAlgoritmusHandler cykAlgoritmusHandler;
    private EBNFTask ebnfTask;

    public EBNFTask getEbnfTask() {
        return ebnfTask;
    }



    public void setEbnfTask(EBNFTask ebnfTask) {
        this.ebnfTask = ebnfTask;
    }

    /**
     * Tests whether a given suggested solution is correct
     * @param suggestedSolution solution to test
     * @return true, if the solution is correct
     */
    public boolean test(String suggestedSolution){
        if(ebnfTask==null){return false;}
        try{
            EbnfGrammar ebnfGrammar = LudwigEbnfParser.parseInput(suggestedSolution);
            EbnfToCnfHandler ebnfToCnfHandler=new EbnfToCnfHandler();
            EbnfGrammar cnfGrammer = ebnfToCnfHandler.makeEbnfIntoCnf(ebnfGrammar);
            //System.out.println(cnfGrammer.toText());
            cykAlgoritmusHandler=new CykAlgoritmusHandler(cnfGrammer);
        } catch (ParseException e) {
            return false;
        }

        for(String word : getEbnfTask().getIncludedWords()){
            if(cykAlgoritmusHandler.istEnthalten(word)==false){return false;}
        }
        for(String word : getEbnfTask().getExcludedWords()){
            if(cykAlgoritmusHandler.istEnthalten(word)==true){return false;}
        }

        return true;
    }

    /**
     * Tests whether a given word is included in the targeted language
     * @param s word to test
     * @return true, if word is included
     */
    public boolean testWord(String s){
        return cykAlgoritmusHandler.istEnthalten(s);
    }

    public CykAlgoritmusHandler getCykAlgoritmusHandler() {
        return cykAlgoritmusHandler;
    }

    public void setCykAlgoritmusHandler(CykAlgoritmusHandler cykAlgoritmusHandler) {
        this.cykAlgoritmusHandler = cykAlgoritmusHandler;
    }
}
