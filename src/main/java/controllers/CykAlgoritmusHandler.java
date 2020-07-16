package controllers;

import model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class CykAlgoritmusHandler {
    //Grammar in CNF form. will throw errors when not in CNF
    EbnfGrammar cnfGrammar;

    public CykAlgoritmusHandler(EbnfGrammar cnfGrammar) {
        this.cnfGrammar = cnfGrammar;
    }

    /*public static void main(String[] args) {
        functionalTest2();
    }*/

    private static void functionalTest(){
        System.out.println("start here: ");
        EbnfGrammar ebnfGrammar=new EbnfGrammar(
                new EbnfRule[]{
                        new EbnfRule(
                                new NonTerminalSymbol("Start"),
                                new EbnfSum(
                                        new EbnfElement[]{
                                                new EbnfAnyNumberOfTimes(
                                                        new NonTerminalSymbol("A")
                                                ),
                                                new TerminalSymbol("("),
                                                new EbnfAnyNumberOfTimes(
                                                        new NonTerminalSymbol("A")
                                                ),
                                                new TerminalSymbol(")"),
                                                new EbnfAnyNumberOfTimes(
                                                        new NonTerminalSymbol("A")
                                                )

                                        }
                                )
                        ),
                        new EbnfRule(
                                new NonTerminalSymbol("A"),
                                new EbnfOrSum(
                                        new EbnfElement[]{
                                                new TerminalSymbol("a"),
                                                new NonTerminalSymbol("Start")
                                        }
                                )
                        )
                }
        );

        CykAlgoritmusHandler cykAlgoritmusHandler;
        EbnfToCnfHandler ebnfToCnfHandler=new EbnfToCnfHandler();
        EbnfGrammar cnfGrammer = ebnfToCnfHandler.makeEbnfIntoCnf(ebnfGrammar);
        cykAlgoritmusHandler=new CykAlgoritmusHandler(cnfGrammer);
        System.out.println("Grammar transformed");

        System.out.println( cykAlgoritmusHandler.istEnthalten("a((a(aa)aa)aa)a") );
    }

    private static void functionalTest2(){
        try{
        EbnfGrammar ebnfGrammar=LudwigEbnfParser.parseInput("D = (NNZ {Z} | \"0\" ) [ \".\" {Z} ] \n"+
                "NNZ = \"1\"|\"2\"|\"3\"|\"4\"|\"5\"|\"6\"|\"7\"|\"8\"|\"9\"\n"+
                " Z = \"0\" | NNZ");
        ebnfGrammar = EbnfToCnfHandler.EbnfToCnf(ebnfGrammar);
            System.out.println(ebnfGrammar.toText());
        }

                catch (ParseException e){
                    System.out.println(e.getMessage()+"\n"+e.getStackTrace());
                }

    }

    /**
     * tests if its possible to create a String with our Grammar by using the CYK algorithm
     * @param string the String to test
     * @return true if its possible to create the string, false otherwise
     */
    public boolean istEnthalten(String string){
        char[] chars = string.toCharArray();
        if(chars.length==0){
            return grammarAllowsEmptyWord();
        }
        else {
            if(cnfGrammar.getEbnfRules().length==0){return false;}
            //cyk algorithm here. isnt it beautiful. I dont think I have ever
            //seen a more beautiful algorithm in my life

            //create the Pyramid thingy we will fill in
            List<List<Collection<NonTerminalSymbol>>> listList=createPyramid(chars.length);
            //add a start at the right, with each rule that creates a char filling in
            //the square
            for(int i=0; i<listList.size(); i++){
                //System.out.println("adding hashTable at ["+(chars.length-1)+"]["+ i +"]");
                listList.get(chars.length-1)
                        .add( i, whatcreates(chars[i] ));
                listList.get(chars.length-1).remove(chars.length);
            }
            //filling in the pyramid from right to left
            for(int i=chars.length-2; i>=0;i--){
                for(int j=0; j<listList.get(i).size(); j++){
                    fillIn(i,j,listList);
                }
            }

            //if field 0/0 of the pyramid contains the starting symbol, we return true
            return listList.get(0).get(0).contains(cnfGrammar.getEbnfRules()[0].getLeftSymbol());
        }
    }

    /**
     * takes the square with coordinates i and j and fills it in according to the Cyk-Algorithm
     * pretty nice stuff.
     * doesnt need a return, because if we manipulate the list passed to us,
     * it changes the object
     * @param i i coordinate of the square to fill in
     * @param j j coordinate of the square to fill in
     * @param listList the pyramid that contains the square we want to fill in
     */
    private void fillIn(int i, int j, List<List<Collection<NonTerminalSymbol>>> listList){
        Collection<NonTerminalSymbol> nonTerminalSymbols = new HashSet<>();
        //imagine this as having a constant be x and going (1,x), (2,x-1)......(x,1) because of it
        for(int x=1;x+i<listList.size();x++){
            //System.out.println("i: "+i+", j: "+j+", checking ["+ (i+x) + "]["+ j +"] and "
            //        +"["+ (listList.size()-x) + "]["+ (listList.size()-i+j-x) +"]");
            if (rulesForBoth(
                            listList.get(i+x).get(j),
                            listList.get(listList.size()-x).get(listList.size()-i+j-x)
                    ) != null) {
                nonTerminalSymbols.addAll(
                        rulesForBoth(
                                listList.get(i+x).get(j),
                                listList.get(listList.size()-x).get(listList.size()-i+j-x)
                        )
                );
            } else {
                if( listList.get(i+x).get(j) ==null){
                    System.out.println("[\"+ (i+x) + \"][\"+ j +\"] doesnt exist");
                }
                if( listList.get(listList.size()-x).get(listList.size()-i+j-x) ==null){
                    System.out.println("[\"+ (listList.size()-x) + \"][\"+ (listList.size()-i+j-x) +\"] doesnt exist");
                }
            }
        }
        //dont forget this line.
        //if you first added a "return null" here to test something else, add a to-do so you dont forget
        //dont be like me and search for an error for like an hour because you left in a
        //temporary "return null"
        listList.get(i).set(j, nonTerminalSymbols);
    }

    /**
     * Looks at the nonTerminalSymbols in two squares A and B of our Cyk-pyramid and looks through the Kreuzproduct
     * of both their contents to find any rules that might create them.
     * @param nonTerminalSymbolsA
     * @param nonTerminalSymbolsB
     * @return every NonTerminalSymbol that creates some combination of the nonTerminalSymbolsA and B
     */
    private Collection<NonTerminalSymbol> rulesForBoth(
            Collection<NonTerminalSymbol> nonTerminalSymbolsA,
            Collection<NonTerminalSymbol> nonTerminalSymbolsB
    ){
        Collection<NonTerminalSymbol> nonTerminalSymbols = new HashSet<>();
        for(NonTerminalSymbol nonTerminalSymbolA : nonTerminalSymbolsA){
            for(NonTerminalSymbol nonTerminalSymbolB : nonTerminalSymbolsB){
                //System.out.print(".");
                for(EbnfRule ebnfRule : cnfGrammar.getEbnfRules()){
                    if(ebnfRule.getRightSide() instanceof EbnfSum){
                        //nonTerminalSymbol A is "down" so left in our word
                        if((((NonTerminalSymbol)(((EbnfSum) ebnfRule.getRightSide()).getEbnfElemente()[0])).getName()
                                .equals(nonTerminalSymbolA.getName()))  &&
                                (((NonTerminalSymbol)(((EbnfSum) ebnfRule.getRightSide()).getEbnfElemente()[1])).getName()
                                        .equals(nonTerminalSymbolB.getName()))    ){
                            nonTerminalSymbols.add(ebnfRule.getLeftSymbol());
                        }
                    }
                }
            }
        }
        return nonTerminalSymbols;
    }

    /**
     * find the rules that create Terminal c and nothing else
     * @param c the Terminal we are looking for
     * @return all NonTerminalSymbols that can create c
     */
    private Collection<NonTerminalSymbol> whatcreates(char c){
        //helper to fill in the rightmost thingy in our pyramid
        Collection<NonTerminalSymbol> nonTerminalSymbolHashSet=new HashSet<>();
        for(EbnfRule ebnfRule: cnfGrammar.getEbnfRules()){
            if(ebnfRule.getRightSide() instanceof TerminalSymbol){
                if(((TerminalSymbol) ebnfRule.getRightSide()).getInhalt().toCharArray()[0]==c){
                    nonTerminalSymbolHashSet.add(ebnfRule.getLeftSymbol());
                }
            }
        }
        return nonTerminalSymbolHashSet;
    }

    public List<List<Collection<NonTerminalSymbol>>> createPyramid(int size){
        List<List<Collection<NonTerminalSymbol>>> listList=new ArrayList<>();
        for(int i=0;i<size;i++){
            listList.add(i,new ArrayList<>());
            for(int j=0;j<=i;j++){
                listList.get(i).add(j,new HashSet<>());
            }
        }
        return listList;
    }

    private boolean grammarAllowsEmptyWord(){
        for(EbnfRule ebnfRule: cnfGrammar.getEbnfRules()){
            if(ebnfRule.getLeftSymbol().getName().equals(
                    cnfGrammar.getEbnfRules()[0].getLeftSymbol().getName())
                    ){
                if(ebnfRule.getRightSide() instanceof EbnfEmpty){
                    return true;
                }
            }
        }
        return false;
    }

}
