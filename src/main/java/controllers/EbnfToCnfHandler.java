package controllers;

import model.*;

import java.util.*;

/**
 * This class translates an EBNF grammar to a grammar in Chomsky normal form (CNF).
 * It's a necessary precondition to be able to apply the CYK-algorithm.
 * @author Ludwig Scheuring
 */

public class EbnfToCnfHandler {
    private List<EbnfRule> oldRules;
    private List<EbnfRule> finalEbnfRules;
    //helper for the "remove empty transions" method, is required to stop
    //A=B, B=A, B= empty   from looping
    //not the best solution, not the cleanest, but its what we got
    private Set<NonTerminalSymbol> nonTerminalSymbolsAlreadyEmptied;


    public static EbnfGrammar EbnfToCnf(EbnfGrammar ebnfGrammar){
        EbnfToCnfHandler ebnfToCnfHandler=new EbnfToCnfHandler();
        return ebnfToCnfHandler.makeEbnfIntoCnf(ebnfGrammar);
    }

    public EbnfToCnfHandler(List<EbnfRule> oldRules) {
        this.oldRules = oldRules;
    }

    public EbnfToCnfHandler() {
    }

    /*public static void main(String[] args) {
        testAll();
    }*/

    /**
     * tests the EbnfToBnf capabilities
     */
    public static void test(){
        //testing stuff. it seems to work now. isnt it beautiful *sheds tears*
        EbnfToCnfHandler ebnfToCnfHandler=new EbnfToCnfHandler();
        EbnfRule[] ebnfRules=new EbnfRule[3];
        ebnfRules[0]=new EbnfRule(new NonTerminalSymbol("Start"),
                new EbnfSum(
                        new EbnfElement[] {
                                new NonTerminalSymbol("A"),
                                new NonTerminalSymbol("B"),
                        }
                ));
        ebnfRules[1]=new EbnfRule(new NonTerminalSymbol("A"),
                new EbnfSum(
                        new EbnfElement[] {
                                new NonTerminalSymbol("B"),
                                new EbnfOptional(
                                        new NonTerminalSymbol("A")
                                ),
                                new EbnfAnyNumberOfTimes(
                                        new NonTerminalSymbol("B")
                                )
                        }
                ));
        ebnfRules[2]=new EbnfRule(new NonTerminalSymbol("B"),
                new EbnfAnyNumberOfTimes(
                        new TerminalSymbol("b")
                )
        );
        EbnfGrammar ebnfGrammar=new EbnfGrammar(ebnfRules);
        System.out.println(ebnfGrammar.toText());
        ebnfGrammar = ebnfToCnfHandler.makeEbnfIntoBnf(ebnfGrammar);
        System.out.println("A whole new World: \n"+ebnfGrammar.toText());



    }

    public static void testAll(){
        int x=1;
        System.out.println("test "+x++);
        test();
        System.out.println("test "+x++);
        test2();
        System.out.println("test "+x++);
        test3();
        System.out.println("test "+x++);
        //test4();
        //System.out.println("test "+x++);
        test5();
        System.out.println("test "+x++);
        test6();
    }

    public static void test2(){
        //testing stuff. it seems to work now. isnt it beautiful *sheds tears*
        EbnfToCnfHandler ebnfToCnfHandler=new EbnfToCnfHandler();
        EbnfRule[] ebnfRules=new EbnfRule[4];
        ebnfRules[0]=new EbnfRule(new NonTerminalSymbol("Start"),
                new EbnfSum(
                        new EbnfElement[] {
                                new NonTerminalSymbol("D"),
                                new NonTerminalSymbol("D"),
                        }
                ));
        ebnfRules[1]=new EbnfRule(new NonTerminalSymbol("D"),
                new EbnfSum(
                        new EbnfElement[] {
                                new EbnfOptional(
                                        new NonTerminalSymbol("A")
                                ),
                                new EbnfOptional(
                                        new NonTerminalSymbol("B")
                                )
                        }
                ));
        ebnfRules[2]=new EbnfRule(
                new NonTerminalSymbol("A"),
                new TerminalSymbol("a")
        );
        ebnfRules[3]=new EbnfRule(
                new NonTerminalSymbol("B"),
                new TerminalSymbol("b")
        );
        EbnfGrammar ebnfGrammar=new EbnfGrammar(ebnfRules);
        System.out.println(ebnfGrammar.toText());
        EbnfGrammar bnfGrammar = ebnfToCnfHandler.makeEbnfIntoBnf(ebnfGrammar);
        System.out.println("Bnf Version: \n"+bnfGrammar.toText());
        EbnfGrammar cnfGrammer = ebnfToCnfHandler.makeEbnfIntoCnf(ebnfGrammar);
        System.out.println("Cnf Version: \n"+cnfGrammer.toText());
    }

    /**
     * Grammar that does a fancy job at creating stuff
     */
    public static void test3(){
        EbnfToCnfHandler ebnfToCnfHandler=new EbnfToCnfHandler();
        EbnfRule[] ebnfRules=new EbnfRule[5];
        ebnfRules[0]=new EbnfRule(new NonTerminalSymbol("Start"),
                new EbnfSum(
                        new EbnfElement[] {
                                new EbnfOptional(
                                        new NonTerminalSymbol("A")
                                )
                        }
                ));
        ebnfRules[1]=new EbnfRule(new NonTerminalSymbol("A"),
                new EbnfSum(
                        new EbnfElement[] {
                                new EbnfOptional(
                                        new NonTerminalSymbol("B")
                                )
                        }
                ));
        ebnfRules[2]=new EbnfRule(new NonTerminalSymbol("B"),
                new EbnfSum(
                        new EbnfElement[] {
                                new EbnfOptional(
                                        new NonTerminalSymbol("C")
                                )
                        }
                ));
        ebnfRules[3]=new EbnfRule(new NonTerminalSymbol("C"),
                new EbnfSum(
                        new EbnfElement[] {
                                new EbnfOptional(
                                        new NonTerminalSymbol("D")
                                )
                        }
                ));
        ebnfRules[4]=new EbnfRule(new NonTerminalSymbol("D"),
                new EbnfSum(
                        new EbnfElement[] {
                                new EbnfOptional(
                                        new TerminalSymbol("Ende")
                                )
                        }
                ));
        EbnfGrammar ebnfGrammar=new EbnfGrammar(ebnfRules);
        System.out.println(ebnfGrammar.toText());
        EbnfGrammar bnfGrammar = ebnfToCnfHandler.makeEbnfIntoBnf(ebnfGrammar);
        System.out.println("Bnf Version: \n"+bnfGrammar.toText());
        EbnfGrammar cnfGrammer = ebnfToCnfHandler.makeEbnfIntoCnf(ebnfGrammar);
        System.out.println("Cnf Version: \n"+cnfGrammer.toText());



    }

    public static void test4(){
        EbnfToCnfHandler ebnfToCnfHandler=new EbnfToCnfHandler();
        EbnfGrammar ebnfGrammar;
        try {
            EbnfGrammar ebnfGrammarGen = EbnfParser.parseTextToEbnfGrammar(
                    "Start:=(X|B)\n"+
                            "X:=A\n"+
                            "A:=\"aTerminal\"\n"+
                            "B:=\"bTerminal\""
            );
            ebnfGrammar=ebnfGrammarGen;
        }
        catch(ParseException e){
            throw new IllegalArgumentException("no real grammar boy");
        }
        System.out.println(ebnfGrammar.toText());
        EbnfGrammar bnfGrammar = ebnfToCnfHandler.makeEbnfIntoBnf(ebnfGrammar);
        System.out.println("Bnf Version: \n"+bnfGrammar.toText());
        EbnfGrammar cnfGrammer = ebnfToCnfHandler.makeEbnfIntoCnf(ebnfGrammar);
        System.out.println("Cnf Version: \n"+cnfGrammer.toText());
    }

    public static void test5(){
        EbnfToCnfHandler ebnfToCnfHandler=new EbnfToCnfHandler();
        EbnfRule[] ebnfRules=new EbnfRule[1];
        ebnfRules[0]=new EbnfRule(new NonTerminalSymbol("Start"),
                new EbnfSum(
                        new EbnfElement[] {
                                new TerminalSymbol("aWort"),
                                new TerminalSymbol("b"),
                                new TerminalSymbol("c"),
                                new TerminalSymbol("d"),
                                new TerminalSymbol("e"),
                                new TerminalSymbol("f"),
                                new TerminalSymbol("g"),
                                new TerminalSymbol("h"),
                                new TerminalSymbol("i"),
                                new TerminalSymbol("j"),
                                new TerminalSymbol("k"),
                                new TerminalSymbol("l")
                        }
                ));

        EbnfGrammar ebnfGrammar=new EbnfGrammar(ebnfRules);
        System.out.println(ebnfGrammar.toText());
        EbnfGrammar bnfGrammar = ebnfToCnfHandler.makeEbnfIntoBnf(ebnfGrammar);
        System.out.println("Bnf Version: \n"+bnfGrammar.toText());
        EbnfGrammar cnfGrammer = ebnfToCnfHandler.makeEbnfIntoCnf(ebnfGrammar);
        System.out.println("Cnf Version: \n"+cnfGrammer.toText());
    }

    public static void test6(){
        EbnfToCnfHandler ebnfToCnfHandler=new EbnfToCnfHandler();
        EbnfRule[] ebnfRules=new EbnfRule[2];
        ebnfRules[0]=new EbnfRule(new NonTerminalSymbol("A"),
                new EbnfAnyNumberOfTimes(
                        new NonTerminalSymbol("A")
                ));
        ebnfRules[1]=new EbnfRule(new NonTerminalSymbol("A"),
                new TerminalSymbol("a")
        );

        EbnfGrammar ebnfGrammar=new EbnfGrammar(ebnfRules);
        System.out.println(ebnfGrammar.toText());
        EbnfGrammar bnfGrammar = ebnfToCnfHandler.makeEbnfIntoBnf(ebnfGrammar);
        System.out.println("Bnf Version: \n"+bnfGrammar.toText());
        EbnfGrammar cnfGrammer = ebnfToCnfHandler.makeEbnfIntoCnf(ebnfGrammar);
        System.out.println("Cnf Version: \n"+cnfGrammer.toText());
    }

    public static void test7(){
        EbnfToCnfHandler ebnfToCnfHandler=new EbnfToCnfHandler();
        EbnfRule[] ebnfRules=new EbnfRule[1];
        ebnfRules[0]=new EbnfRule(new NonTerminalSymbol("A"),
                new EbnfAnyNumberOfTimes(
                        new NonTerminalSymbol("A")
                ));

        EbnfGrammar ebnfGrammar=new EbnfGrammar(ebnfRules);
        System.out.println(ebnfGrammar.toText());
        EbnfGrammar bnfGrammar = ebnfToCnfHandler.makeEbnfIntoBnf(ebnfGrammar);
        System.out.println("Bnf Version: \n"+bnfGrammar.toText());
        EbnfGrammar cnfGrammer = ebnfToCnfHandler.makeEbnfIntoCnf(ebnfGrammar);
        System.out.println("Cnf Version: \n"+cnfGrammer.toText());
    }


    /**
     * turn the Grammar provided into a Cnf, using an algoritm from wikipedia
     * @param ebnfGrammar The Ebnf you want to transform. Can contain any amount of recursion and stuff
     * @return a CnfGrammar that describes/creates the same language as and EBNF entered.
     */
    public EbnfGrammar makeEbnfIntoCnf(EbnfGrammar ebnfGrammar){
        //makes A="ab" into A=sum("a","b")
        ebnfGrammar = partTooLongTerminalSymbols(ebnfGrammar);
        EbnfGrammar bnfGrammar=makeEbnfIntoBnf(ebnfGrammar);

        //here go the 5 steps following wikipedia: https://de.wikipedia.org/wiki/Chomsky-Normalform
        initialize(bnfGrammar);
        //make it so empty sums are replaced by the emtpy object

        cleanup();
        //step 1: add new starting rule (rule 0 is our starting rule)
        //this rule will be handeled a bit different in step 2
        //(its the only one that is allowed to point to empty)
        oldRules= new ArrayList<>(oldRules);
        if(oldRules.size()==0){
            return new EbnfGrammar(new EbnfRule[]{});
        }
        oldRules.add(0, new EbnfRule(
                new NonTerminalSymbol(findUniqueName("GeneratedStartingRule")),
                new NonTerminalSymbol(oldRules.get(0).getLeftSymbol().getName())
                )
        );

        //step 2: remove Empty transitions (A -> EbnfEmpty), except starting rule
        //works so far
        removeEmptyTransitions();

        //step 3: entferne einfache ersetzungen der form A->B
        //regeln der form A -> "a" sind erlaubt
        entferneEinfacheUebergaenge();

        //step 4: remove Sums that are too long, by turning A -> BCD into A -> BX + X -> CD and so on
        //we kinda need that sadly. would love to not do it, but so is life
        shortenTooLongSums();

        //step 5: ersetze übergänge der form A->"a",B mit A-> Xa,B und Xa->a
        wandleTerminaluebergaengeUm();
        removeDuplicateRules();
        //and we done. only took like 1000 lines of code
        //EbnfGrammar test=  new EbnfGrammar(oldRules.toArray(new EbnfRule[]{}));
        //System.out.println(test.toText());
        return new EbnfGrammar(oldRules.toArray(new EbnfRule[]{}));

    }

    /**
     * Teile regeln der form A-> "abc" in A-> sum("a","b","c")
     * @param ebnfGrammar
     * @return
     */
    private EbnfGrammar partTooLongTerminalSymbols(EbnfGrammar ebnfGrammar){
        EbnfRule[] ebnfRules=ebnfGrammar.getEbnfRules();
        for(EbnfRule ebnfRule: ebnfRules){
            ebnfRule.setRightSide(
                    partTooLongTerminalSymbols(
                            ebnfRule.getRightSide()
                    )
            );
        }
        return new EbnfGrammar(ebnfRules);
    }

    /**
     * helper of other partTooLongThingy, worked recursivly to make sure every A="ab" is hit,
     * even those inside Sums and other elements
     * @param ebnfElement
     * @return
     */
    private EbnfElement partTooLongTerminalSymbols(EbnfElement ebnfElement){
        if(ebnfElement instanceof TerminalSymbol){
            return ebnfElement.simplify();
        } else if(ebnfElement instanceof EbnfSum){
            EbnfElement[] ebnfElements=((EbnfSum) ebnfElement).getEbnfElemente();
            for(int i=0; i<ebnfElements.length; i++){
                ebnfElements[i]=partTooLongTerminalSymbols(ebnfElements[i]);
            }
            return new EbnfSum(ebnfElements);
        } else if(ebnfElement instanceof EbnfOrSum){
            EbnfElement[] ebnfElements=((EbnfOrSum) ebnfElement).getEbnfElemente();
            for(int i=0; i<ebnfElements.length; i++){
                ebnfElements[i]=partTooLongTerminalSymbols(ebnfElements[i]);
            }
            return new EbnfOrSum(ebnfElements);
        } else if(ebnfElement instanceof EbnfAnyNumberOfTimes) {
            return new EbnfAnyNumberOfTimes(
                    partTooLongTerminalSymbols(
                            ((EbnfAnyNumberOfTimes) ebnfElement).getEbnfElement()
                    )
            );
        } else if(ebnfElement instanceof EbnfOptional) {
            return new EbnfOptional(
                    partTooLongTerminalSymbols(
                            ((EbnfOptional) ebnfElement).getEbnfElement()
                    )
            );
        }
        return ebnfElement;
    }

    private void wandleTerminaluebergaengeUm(){
        while(wandleTerminaluebergaengeUmHelper()){}
    }

    private boolean wandleTerminaluebergaengeUmHelper(){
        //geh durch all regeln und schau ob wir A-> "a"B finden
        for(EbnfRule ebnfRule: oldRules){
            if(ebnfRule.getRightSide() instanceof EbnfSum){
                EbnfElement[] ebnfElements=((EbnfSum) ebnfRule.getRightSide()).getEbnfElemente();
                for(int i=0; i<ebnfElements.length;i++){
                    if(ebnfElements[i] instanceof TerminalSymbol){
                        //found a rule A-> "a"B, gonna replace "a" with X,
                        // and create X -> "a"
                        ebnfElements[i]=new NonTerminalSymbol(
                                addRuleUniqueNameToOldRules(
                                        new EbnfRule(
                                                new NonTerminalSymbol(
                                                        "create_"+
                                                                ((TerminalSymbol)ebnfElements[i]).getInhalt()
                                                ),
                                                ebnfElements[i]
                                        )
                                )
                        );
                        ((EbnfSum) ebnfRule.getRightSide()).setEbnfElemente(ebnfElements);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void shortenTooLongSums(){
        while(shortenTooLongSumsHelper()){}
    }

    private boolean shortenTooLongSumsHelper(){
        //check though all rules too see if they happen to be Sums longer than two
        for(EbnfRule ebnfRule: oldRules){
            if(ebnfRule.getRightSide() instanceof EbnfSum){
                if(((EbnfSum) ebnfRule.getRightSide()).getEbnfElemente().length>2){
                    //find a unique name
                    String uniquename=findUniqueName(ebnfRule.getLeftSymbol().getName());
                    List<EbnfElement> ebnfElementList=new ArrayList<>();
                    ebnfElementList.addAll(Arrays.asList(
                            ((EbnfSum) ebnfRule.getRightSide()).getEbnfElemente())
                    );
                    //turns A-> BCDEFG... into A-> BX and X-> CDEFG...
                    //where X is a unique name for a new rule
                    ebnfRule.setRightSide(
                            new EbnfSum(
                                    new EbnfElement[]{
                                            ebnfElementList.get(0),
                                            new NonTerminalSymbol(uniquename)
                                    }
                            )
                    );
                    ebnfElementList.remove(0);
                    oldRules.add(
                            new EbnfRule(
                                    new NonTerminalSymbol(uniquename),
                                    new EbnfSum(
                                            ebnfElementList.toArray(new EbnfElement[]{})
                                    )
                            )
                    );
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * entfernt einfache uebergaenge der form A->B durch magie (in dem er jedes A auf einer
     * rechten Seite durch ein (A|B) ersetzt, und dann zu BNF macht)
     */
    private void entferneEinfacheUebergaenge(){
        cleanup();
        do
        {
             //EbnfGrammar ebnfGrammar= new EbnfGrammar(oldRules.toArray(new EbnfRule[]{}));
            //System.out.println(ebnfGrammar.toText());
        } while(entferneEinfacheUebergaengeHelper());
    }

    private boolean entferneEinfacheUebergaengeHelper(){
        for(EbnfRule ebnfRule: oldRules){
            if(ebnfRule.getRightSide() instanceof NonTerminalSymbol){
                handleEinfacherUebergang(oldRules.indexOf(ebnfRule));
                return true;
            }
        }
        return false;
    }

    /**
     * removes an Einfacher uebergang A -> B by replacing B with whatever hides behind rules in
     * the Form B -> X
     * @param i index of Element to handle
     * @throws IllegalArgumentException if i is outside the array or not really an einfacher uebergang
     */
    private void handleEinfacherUebergang(int i){
        if(i<0 | i>= oldRules.size()){
            throw new IllegalArgumentException("rule not in bounds");
        }
        EbnfRule ebnfRuleToRemove = oldRules.get(i);
        String name=ebnfRuleToRemove.getLeftSymbol().getName();
        if(ebnfRuleToRemove.getRightSide() instanceof NonTerminalSymbol == false){
            throw new IllegalArgumentException("Error in removing a simple transition: not a nonTerminal Symbol");
        }
        //replace the behind of A with whatever hides behind B (added: even if it hiden deep)
        oldRules.replaceAll(ebnfRule -> {
            if(oldRules.indexOf(ebnfRule)==i){
                //todo: maybe add in the future so that rules dont duplicate
                //HashSet<NonTerminalSymbol> nonTerminalSymbols=new HashSet<>();
                //nonTerminalSymbols.add(ebnfRuleToRemove.getLeftSymbol());
                return new EbnfRule(
                        ebnfRuleToRemove.getLeftSymbol(),
                        new EbnfOrSum( findAllRightSightsOfAAdvanced(
                                (NonTerminalSymbol) ebnfRuleToRemove.getRightSide(),
                                new HashSet<>()
                                ).toArray(new EbnfElement[]{})
                        )
                );
            } else {
                return ebnfRule;
            }
        });
        cleanup();
        EbnfToCnfHandler ebnfToCnfHandler=new EbnfToCnfHandler();
        EbnfGrammar newGrammar=ebnfToCnfHandler.makeEbnfIntoBnf(new EbnfGrammar(oldRules.toArray(new EbnfRule[]{})));
        List<EbnfRule> newEbnfRules = Arrays.<EbnfRule>asList( newGrammar.getEbnfRules());
        oldRules=new ArrayList<>();
        oldRules.addAll(newEbnfRules);
        cleanup();
    }

    /**
     * goes through the oldrules and finds and returns an array of all the right side of rules
     * which left sides are A
     * @param nonTerminalSymbolA the left sind of the rules to find
     * @return An array of rightElements of all rules among oldrules which left side is a
     */
    private EbnfElement[] findAllRightSightsOfA(NonTerminalSymbol nonTerminalSymbolA){
        List<EbnfElement> ebnfElementList = new ArrayList<>();
        for(EbnfRule ebnfRule: oldRules){
            if(ebnfRule.getLeftSymbol().getName().equals(nonTerminalSymbolA.getName())){
                ebnfElementList.add(ebnfRule.getRightSide());
            }
        }
        return ebnfElementList.toArray(new EbnfElement[]{});
    }

    private List<EbnfElement> findAllRightSightsOfAList(NonTerminalSymbol nonTerminalSymbolA){
        List<EbnfElement> ebnfElementList = new ArrayList<>();
        for(EbnfRule ebnfRule: oldRules){
            if(ebnfRule.getLeftSymbol().getName().equals(nonTerminalSymbolA.getName())){
                ebnfElementList.add(ebnfRule.getRightSide());
            }
        }
        return ebnfElementList;
    }

    /**
     * kinda like findAllRightSightsOfA, except it resolves A=B, B=C, C=D automaticly,
     * so remove simple transitions doesnt loop when presented with
     * A=B, B=C, C=B which it would otherwise
     * @return everything that is hiding behind A, even if it is hiding behind other, simple transitions
     */
    private List<EbnfElement> findAllRightSightsOfAAdvanced(NonTerminalSymbol nonTerminalSymbolA,
                                                        HashSet<NonTerminalSymbol> alreadyVisited){
        if(alreadyVisited.contains(nonTerminalSymbolA)){
            return new ArrayList<>();
        } else  {
            //collect everything on the right side of A
            List<EbnfElement> ebnfElementList = findAllRightSightsOfAList(nonTerminalSymbolA);
            //we now "visited" A
            alreadyVisited.add(nonTerminalSymbolA);
            for(int i=0; i<ebnfElementList.size(); i++){
                //visit every NonTerminal, and replace it with the findings, so no NonTerminal shall remain
                if(ebnfElementList.get(i) instanceof NonTerminalSymbol){
                    NonTerminalSymbol nonTerminalSymbol= (NonTerminalSymbol) ebnfElementList.get(i);
                    ebnfElementList.remove(i);
                    i--;
                    ebnfElementList.addAll(findAllRightSightsOfAAdvanced(nonTerminalSymbol,alreadyVisited));
                }
            }
            return ebnfElementList;
        }
    }

    /**
     * replaces all occurences of non terminal Symbol on the right side of rules with EbnfElement B
     * @param nonTerminalSymbolA Symbol to replace
     * @param B the element that will be put in As place
     */
    private void ReplaceAllAwithB(NonTerminalSymbol nonTerminalSymbolA, EbnfElement B){
        String name=nonTerminalSymbolA.getName();
        for(EbnfRule ebnfRule: oldRules){
            if(ebnfRule.getRightSide() instanceof NonTerminalSymbol){
                if(((NonTerminalSymbol) ebnfRule.getRightSide()).getName().equals(name)){
                    //System.out.println("we changing boys");
                    ebnfRule.setRightSide(B);
                }
            }
            else if(ebnfRule.getRightSide() instanceof EbnfSum){
                //sums at this point should contain only terminal and nonTerminal Symbols
                EbnfElement[] ebnfElements = ((EbnfSum) ebnfRule.getRightSide()).getEbnfElemente();
                for(int i=0; i<ebnfElements.length;i++){
                    if(ebnfElements[i] instanceof NonTerminalSymbol){
                        if(((NonTerminalSymbol) ebnfElements[i]).getName().equals(name)){
                            ebnfElements[i]=B;
                        }
                    }
                }
                ebnfRule.setRightSide(new EbnfSum(ebnfElements));
            }
        }
    }

    private void removeDuplicateRules(){
        oldRules= new ArrayList<>(new LinkedHashSet<>(oldRules));
    }

    /**
     * removes all Empty transitions from the oldRules
     * oldRules are BNF at this point (better be)
     */
    private void removeEmptyTransitions() {
        cleanup();
        nonTerminalSymbolsAlreadyEmptied=new HashSet<>();
        while (removeEmptyTransitionsHelper()) { }
    }

    /**
     * searches for an empty Rule and handles the first one
     * @return true if it found and handled an empty rule, false otherwise
     */
    private boolean removeEmptyTransitionsHelper(){
       // System.out.println("we in");
        for(EbnfRule ebnfRule: oldRules){
            //generated first rule is allowed to have empty transition
            if(ebnfRule.getLeftSymbol().getName().equals(
                    oldRules.get(0).getLeftSymbol().getName()
            )){
                continue;
            }
            //System.out.println("checking rule "+oldRules.indexOf(ebnfRule));
            //System.out.println(ebnfRule.toText());
            if(ebnfRule.getRightSide() instanceof EbnfEmpty ){
                //System.out.println("we got one");
                handleEmptyRule(oldRules.indexOf(ebnfRule));
                nonTerminalSymbolsAlreadyEmptied.add(ebnfRule.getLeftSymbol());
                return true;
            } else if(ebnfRule.getRightSide() instanceof EbnfSum  ){
                if(((EbnfSum) ebnfRule.getRightSide()).getEbnfElemente().length==0){
                    //System.out.println("we got one the old fashined way");
                    handleEmptyRule(oldRules.indexOf(ebnfRule));
                    nonTerminalSymbolsAlreadyEmptied.add(ebnfRule.getLeftSymbol());
                    return true;
                }
            }
        }
        //System.out.println("we done");
        return false;
    }

    /**
     * removes an empty rule by making all the occurrences of its left Symbol optional, then
     * running a ebnf -> bnf conversion
     * @param i index of Element to handle
     * @throws IllegalArgumentException if i is outside the array or not really an empty rule
     */
    private void handleEmptyRule(int i){
        String name=oldRules.get(i).getLeftSymbol().getName();
        if(i<0 | i>= oldRules.size()){
            throw new IllegalArgumentException("rule not in bounds");
        }
        if(oldRules.get(i).getRightSide() instanceof EbnfEmpty == false){
            if(oldRules.get(i).getRightSide() instanceof EbnfSum == false){
                throw new IllegalArgumentException("not in BNF way");
            } else {
                if(((EbnfSum) oldRules.get(i).getRightSide()).getEbnfElemente().length!=0){
                    throw new IllegalArgumentException("rule to handle is not an Empty rule");
                }
            }
        }
        if(nonTerminalSymbolsAlreadyEmptied.contains(oldRules.get(i).getLeftSymbol())==false){
            makeAllOptional(oldRules.get(i).getLeftSymbol());
            oldRules.remove(i);

            //makes the rules into the BNF way again after we add all these optionals
            EbnfToCnfHandler ebnfToCnfHandler=new EbnfToCnfHandler();
            EbnfGrammar newGrammar=ebnfToCnfHandler.makeEbnfIntoBnf(new EbnfGrammar(oldRules.toArray(new EbnfRule[]{})));
            List<EbnfRule> newEbnfRules = Arrays.<EbnfRule>asList( newGrammar.getEbnfRules());
            oldRules=new ArrayList<>();
            oldRules.addAll(newEbnfRules);
            //cleaning up, just to be save
            cleanup();
            return;
        }   else {
            oldRules.remove(i);
            cleanup();
            return;
        }
    }

    /**
     * handle the case that a NonTerminalSymbol is never on the left side
     * by removing it completly (exeption being the created start symbol
     * Only works on a BNF oldrules setup
     */
    private void handleOrphans(){
        while(handleOrphansHelper()){}
    }

    private boolean handleOrphansHelper(){
        Collection<String> takennames = new HashSet<>();
        for (EbnfRule oldRule : oldRules) {
            takennames.add(oldRule.getLeftSymbol().getName());
        }
        for (EbnfRule oldRule : oldRules){
            if(oldRule.getRightSide() instanceof EbnfEmpty){
                //not My job
                //System.out.println("this should be handled");
            }
            if(oldRule.getRightSide() instanceof NonTerminalSymbol){
                if(takennames.contains(((NonTerminalSymbol) oldRule.getRightSide()).getName())==false){
                    //System.out.println("removing: \""+oldRule.toText()+'"');
                    oldRules.remove(oldRule);
                    return true;
                }
            }
            if(oldRule.getRightSide() instanceof EbnfSum){
                EbnfElement[] ebnfElements=((EbnfSum) oldRule.getRightSide()).getEbnfElemente();
                for(int i=0; i<ebnfElements.length;i++){
                    if(ebnfElements[i] instanceof NonTerminalSymbol){
                        if(takennames.contains(((NonTerminalSymbol)ebnfElements[i]).getName())==false){
                            //whole Rule must be deleted, since one part has no way of being made terminal
                            //System.out.println("removing: \""+oldRule.toText()+'"');
                            oldRules.remove(oldRule);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void advancedHandleOphans(){
        if(oldRules.size()==0) return;
        removeNonReachablesRules();
        removeNonResolvables();
    }

    /**
     * removes all Rules from oldRules that cant be reached by other rules
     */
    private void removeNonReachablesRules(){
        if(oldRules.size()==0) {
            return;
        }
        //System.out.println("removing nonReachables:");
        //System.out.println("Current grammer is: ");
        //System.out.println(new EbnfGrammar(oldRules.toArray(new EbnfRule[]{})).toText());
        //add all reachableSymbols, then delete the rest
        Set<NonTerminalSymbol> reachableSymbols=new HashSet<>();
        reachableSymbols.add(oldRules.get(0).getLeftSymbol());
        int x=0;
        do {
            x=reachableSymbols.size();
            for(EbnfRule ebnfRule: oldRules){
                //find all rules that have a reace reachable symbols.
                //repeat as long as we add symbolshable symbol on their left,
                //then add their right side to th
                if(reachableSymbols.contains(ebnfRule.getLeftSymbol())){
                    reachableSymbols.addAll(
                            findNonTerminalsInside(
                                    ebnfRule.getRightSide()
                            )
                    );
                }
            }
        } while(x<reachableSymbols.size());
        //remove all rules that are not reachable
        for(int i = 0; i<oldRules.size(); i++){
            if(reachableSymbols.contains(oldRules.get(i).getLeftSymbol())==false){
                //System.out.println("rule \""+oldRules.get(i).toText()+"\" is not reachable, so we remove it");
                oldRules.remove(i);
                i--;
            }
        }
        //System.out.println("done with all");
    }

    /**
     * finds all nonTerminalSymbols contained in an EbnfElement
     * @param ebnfElement the EbnfElement you are searching
     * @return all the nonTerminalSymbols inside
     */
    private List<NonTerminalSymbol> findNonTerminalsInside(EbnfElement ebnfElement){
        List<NonTerminalSymbol> nonTerminalSymbols=new ArrayList<>();
        if(ebnfElement instanceof NonTerminalSymbol){
            nonTerminalSymbols.add((NonTerminalSymbol) ebnfElement);
        }
        else if(ebnfElement instanceof EbnfSum){
            for(EbnfElement ebnfElementInsideSum : ((EbnfSum) ebnfElement).getEbnfElemente()){
                nonTerminalSymbols.addAll(findNonTerminalsInside(ebnfElementInsideSum));
            }
        }
        else if(ebnfElement instanceof EbnfOrSum){
            for(EbnfElement ebnfElementInsideSum : ((EbnfOrSum) ebnfElement).getEbnfElemente()){
                nonTerminalSymbols.addAll(findNonTerminalsInside(ebnfElementInsideSum));
            }
        }
        else if(ebnfElement instanceof EbnfAnyNumberOfTimes){
            nonTerminalSymbols.addAll(
                    findNonTerminalsInside(
                            ((EbnfAnyNumberOfTimes) ebnfElement).getEbnfElement()
                    )
            );
        }
        else if(ebnfElement instanceof EbnfOptional){
            nonTerminalSymbols.addAll(
                    findNonTerminalsInside(
                            ((EbnfOptional) ebnfElement).getEbnfElement()
                    )
            );
        }
        return nonTerminalSymbols;
    }

    private void removeNonResolvables(){
        //stop here if rules are empty:
        if(oldRules.size()==0){return;}
        //we might mess with the position of the start symbol here, so we better restore it if it goes missing
        NonTerminalSymbol startSymbol = oldRules.get(0).getLeftSymbol();
        Set<NonTerminalSymbol> resolvables = new HashSet<>();
        int x=0;
        //add every nonTerminalSymbol that we can resolve into terminal Symbols
        do{
            x=resolvables.size();
            for(EbnfRule ebnfRule: oldRules){
                if(resolvables.contains(ebnfRule.getLeftSymbol())==false){
                    if(isResolvable(resolvables,ebnfRule.getRightSide())){
                        resolvables.add(ebnfRule.getLeftSymbol());
                    }
                }
            }
        } while (x<resolvables.size());

        for(int i=0; i<oldRules.size(); i++){
            if(isResolvable(resolvables, oldRules.get(i).getRightSide())==false){
                oldRules.remove(i);
                i--;
            }
        }

        if(oldRules.size()==0){return;}
        if(oldRules.get(0).getLeftSymbol().equals(startSymbol)){
            return;
        } else {
            for(int i=0; i<oldRules.size(); i++){
                if(oldRules.get(i).getLeftSymbol().equals(startSymbol)){
                    Collections.swap(oldRules, 0, i);
                    return;
                }
            }
        }
        //we got a case of Non-resolvable StartSymbol here. So we return an empty grammar
        oldRules=new ArrayList<>();
        return;
    }

    /**
     * helper for removeNonResolvables, tells you wheter an element is resolvable, based on which
     * non Terminal symbols already are resolvable
     * @param resolvables the Non Terminal Symbols we can resolve
     * @param ebnfElement the element for which we want to know if we can resolve it
     * @return true is ebnfElement is resolvable, false otherwise
     */
    private boolean isResolvable(Set<NonTerminalSymbol> resolvables, EbnfElement ebnfElement){
        if(ebnfElement instanceof EbnfEmpty){
            return true;
        } else if(ebnfElement instanceof TerminalSymbol){
            return true;
        } else if(ebnfElement instanceof NonTerminalSymbol){
            return resolvables.contains((NonTerminalSymbol) ebnfElement);
        } else if(ebnfElement instanceof EbnfSum){
            //if any element in the sum is not resolvable, the sum is not resolvable
            for(EbnfElement ebnfElementInSum: ((EbnfSum) ebnfElement).getEbnfElemente()){
                if(isResolvable(resolvables,ebnfElementInSum)==false){
                    return false;
                }
            }
            return true;
        } else if(ebnfElement instanceof EbnfOrSum){
            //if any element IS resolvable, the Or Sum is resolvable (inverse to normal Sum
            for(EbnfElement ebnfElementInSum: ((EbnfOrSum) ebnfElement).getEbnfElemente()){
                if(isResolvable(resolvables,ebnfElementInSum)){
                    return true;
                }
            }
            return false;
        } else {
            throw new IllegalArgumentException("we should have kinda BNF at this point, idk");
        }
    }

    /**
     * makes all occurences of the nonTerminal Symbol in oldrules optional (only works on BNF form)
     * @param nonTerminalSymbol the symbol to make optional
     */
    private void makeAllOptional(NonTerminalSymbol nonTerminalSymbol){
        String name=nonTerminalSymbol.getName();
        for(EbnfRule ebnfRule: oldRules){
            if(ebnfRule.getRightSide() instanceof NonTerminalSymbol){
                if(((NonTerminalSymbol) ebnfRule.getRightSide()).getName().equals(name)){
                    //System.out.println("we changing boys");
                    ebnfRule.setRightSide(new EbnfOptional(new NonTerminalSymbol(name)));
                }
            }
            else if(ebnfRule.getRightSide() instanceof EbnfSum){
                //sums at this point should contain only terminal and nonTerminal Symbols
                EbnfElement[] ebnfElements = ((EbnfSum) ebnfRule.getRightSide()).getEbnfElemente();
                for(int i=0; i<ebnfElements.length;i++){
                    if(ebnfElements[i] instanceof NonTerminalSymbol){
                        if(((NonTerminalSymbol) ebnfElements[i]).getName().equals(name)){
                            ebnfElements[i]=new EbnfOptional(ebnfElements[i]);
                        }
                    }
                }
                ebnfRule.setRightSide(new EbnfSum(ebnfElements));
            }
        }
    }

    /**
     *  cleansup the rules a bit so they look and act cleaner
     *  seem to work rn. oh well
     */
    private void cleanup(){
        if(oldRules.size()==0){return;}
        handleEmptySums();
        handleSelfRules();
        handleOrphans();
        // handle rules that are never used on a right side (making an exeption for Generated starting
        // rule of course).
        advancedHandleOphans();
        removeDuplicateRules();
    }

    /**
     * removes all rules A -> A since they are useless
     */
    private void handleSelfRules(){
        for(int i=0; i<oldRules.size();i++){
            if(oldRules.get(i).getRightSide() instanceof NonTerminalSymbol){
                if(((NonTerminalSymbol) oldRules.get(i).getRightSide()).getName().equals(
                        oldRules.get(i).getLeftSymbol().getName())){
                    oldRules.remove(i);
                    i--;
                }
            }
        }
    }

    private void handleEmptySums(){
        for(EbnfRule ebnfRule: oldRules){
            if(ebnfRule.getRightSide() instanceof EbnfSum){
                ebnfRule.setRightSide(ebnfRule.getRightSide().simplify());
            }
        }
    }

    /**
     * initializes the internal rules of This class to oldRules=ebnfgrammer rules and
     * finalEbnfRules = empty
     * @param ebnfGrammar
     */
    private void initialize(EbnfGrammar ebnfGrammar){
        oldRules =new ArrayList<>( Arrays.<EbnfRule>asList(ebnfGrammar.getEbnfRules()));
        finalEbnfRules=new ArrayList<>();
    }

    /**
     * transforms a grammar in any EBNF form into BNF. new rules go into FinalebnfRule and are returned
     * as an EbnfGrammer
     * @param ebnfGrammar the normal EBNF that gets turned into BNF
     * @return a grammer following BNF
     */
    public EbnfGrammar makeEbnfIntoBnf(EbnfGrammar ebnfGrammar){
        //oldRules = ebnfGrammar.getEbnfRules();
        oldRules = new ArrayList<>();
        oldRules.addAll( Arrays.<EbnfRule>asList(ebnfGrammar.getEbnfRules()));
        finalEbnfRules=new ArrayList<>();
        transformIntoBnf();
        oldRules=finalEbnfRules;
        cleanup();
        return new EbnfGrammar(oldRules.toArray(new EbnfRule[]{}));
    }

    /**
     * turns a sum of sums into one huge sum, keeping order andF peace and prosperity, but mainly order
     * @param ebnfSum the ebnfSum element to part
     * @return one ebnfSum Element that contains only simple elements (terminal and nonterminal symbols)
     */
    private EbnfSum makeIntoOneSum(EbnfSum ebnfSum){
        EbnfElement[] ebnfElements=new EbnfElement[0];
        ebnfElements = makeIntoOneArraySum(ebnfSum).toArray(ebnfElements);
        return new EbnfSum(
                ebnfElements
        );
    }

    /**
     * helper for makeIntoOneSum
     * @param ebnfSum the EbnfSum to part
     * @return one List of simple EbnfElements
     */
    private List<EbnfElement> makeIntoOneArraySum(EbnfSum ebnfSum){
        EbnfElement[] ebnfElements=ebnfSum.getEbnfElemente();
        List<EbnfElement> ebnfElementList=new ArrayList<>();
        for(EbnfElement ebnfElement : ebnfElements){
            if(ebnfElement instanceof EbnfSum){
                ebnfElementList.addAll( makeIntoOneArraySum((EbnfSum) ebnfElement)  );
            }
            else{
                ebnfElementList.add(ebnfElement);
            }
        }

        return ebnfElementList;
    }



    /**
     * removes all {} and [] from oldRules and moves them into finalEbnfRules
     */
    private void transformIntoBnf(){
        if(oldRules.size()==0){
            return;
        }
        NonTerminalSymbol startSymbol;
        startSymbol = oldRules.get(0).getLeftSymbol();
        for(EbnfRule rule : oldRules){
            EbnfElement[] newRules=splitIntoSimpleRules(rule.getRightSide());
            for(EbnfElement newRule : newRules){
                EbnfRule ebnfRuleToAdd =  (new EbnfRule(
                        new NonTerminalSymbol(
                                rule.getLeftSymbol().getName()),
                        newRule));
                if(ebnfRuleToAdd.getRightSide() instanceof EbnfSum){
                    ebnfRuleToAdd =new EbnfRule( new NonTerminalSymbol(
                            rule.getLeftSymbol().getName()),
                            makeIntoOneSum((EbnfSum) ebnfRuleToAdd.getRightSide()));
                }
                addRuleToFinalRules(ebnfRuleToAdd);
            }
        }
        if(finalEbnfRules.get(0).getLeftSymbol().equals(startSymbol)==false){
            //führt diesen code aus falls die erste Regel aus irgendeinem grund
            //*hust* anyNumberOfTimes Wacky coding*hust* nicht mehr das startSymbol endhällt
            int i=0;
            for(i=0; finalEbnfRules.get(i).getLeftSymbol().equals(startSymbol)==false ;i++){    }
            EbnfRule startRule=finalEbnfRules.remove(i);
            finalEbnfRules.add(0,startRule);
        }
    }

    /**
     * splits the given EBNF element into smaller, simple pieces (no optionals, no Any Number of Times
     * and no Or connections
     * @param ebnfElement the element to split
     * @return an array of simple ebnf elements
     */
    private EbnfElement[] splitIntoSimpleRules(EbnfElement ebnfElement){
        // optinal: make it so this takes and receives Rules instead of elements, maybe
        // but maybe not in which case this is gonna stay as beautiful as it is right now

        EbnfElement[] ergebnis=new EbnfElement[] {ebnfElement};

        if(ebnfElement instanceof EbnfOptional){
            //do da split, then return
            EbnfElement[] insideSplitUp=splitIntoSimpleRules(((EbnfOptional) ebnfElement).getEbnfElement());
            List<EbnfElement> ergebnislist=new ArrayList<>();
            ergebnislist.add(new EbnfEmpty());
            ergebnislist.addAll(Arrays.asList(insideSplitUp));
            ergebnis = ergebnislist.toArray(ergebnis);
            return ergebnis;
        }
        if(ebnfElement instanceof EbnfAnyNumberOfTimes) {
            //add rules then split thingy and return
            //adds rule r -> E and r -> rr ro simulate recursion
            EbnfElement[] insideSplitUp = splitIntoSimpleRules(((EbnfAnyNumberOfTimes) ebnfElement).getEbnfElement());
            List<EbnfElement> ergebnislist=new ArrayList<>();
            //for every possible inside, create a resursive loop that desribes it
            String name = findUniqueName("AddedRule");
            for(EbnfElement insideElement : insideSplitUp)
            {
                addRuleToFinalRules(new EbnfRule(new NonTerminalSymbol(name),
                        insideElement));
                addRuleToFinalRules(new EbnfRule(new NonTerminalSymbol(name),
                        new EbnfSum(new EbnfElement[]{
                                new NonTerminalSymbol(name),
                                new NonTerminalSymbol(name)
                        })));
                ergebnislist.add(new NonTerminalSymbol(name));
            }
            ergebnislist.add(new EbnfEmpty());
            ergebnis=ergebnislist.toArray(ergebnis);
            return ergebnis;
        }
        if(ebnfElement instanceof EbnfOrSum){
            //split them all up, since they are or connected anyway, then
            //split them further internally
            EbnfElement[] ebnfElements= ((EbnfOrSum) ebnfElement).getEbnfElemente();
            List<EbnfElement> ergebnisliste=new ArrayList<>();
            for(int i = 0; i<ebnfElements.length;i++){
                //split the singular ebnfElements into their pieces
                 ergebnisliste.addAll( Arrays.asList( splitIntoSimpleRules(ebnfElements[i])));
            }
            ergebnis=ergebnisliste.toArray(ergebnis);
            return ergebnis;
        }
        if(ebnfElement instanceof EbnfSum){
            //create a list of all single ebnfElements simplyfied
            EbnfElement[] ebnfElements=((EbnfSum) ebnfElement).getEbnfElemente();
            if(ebnfElements.length==0){
                //just so we dont throw an error on an empty sum (which should not exist)
                return new EbnfElement[] {new EbnfEmpty()};
            }
            EbnfElement[][] ebnfElementsExtended=new EbnfElement[ebnfElements.length][];
            for(int i = 0; i<ebnfElements.length;i++){
                //split the singular ebnfElements into their pieces
                ebnfElementsExtended[i]=splitIntoSimpleRules(ebnfElements[i]);
            }
            int[] counterMax = new int[ebnfElements.length];
            int[] counterCurrent= new int[ebnfElements.length];
            for(int i = 0; i<ebnfElements.length;i++){
                counterMax[i]=ebnfElementsExtended[i].length-1;
                counterCurrent[i]=0;
            }
            List<EbnfElement> ergebnisliste=new ArrayList<>();
            boolean done=false;
            while(done==false){
                //System.out.println("  counterCurrent: "+IntArrayToStringHelper(counterCurrent));
                //System.out.println("  counterMax: "+IntArrayToStringHelper(counterMax));
                //debugstuff above
                //creates the content of a new sum element
                EbnfElement[] ebnfSumElementsToAdd=new EbnfElement[counterCurrent.length];
                for(int i = 0; i<ebnfSumElementsToAdd.length;i++){
                    //adds the requisite parts
                    ebnfSumElementsToAdd[i]=ebnfElementsExtended[i][counterCurrent[i]];
                }
                //adds it to our return
                ergebnisliste.add(new EbnfSum(ebnfSumElementsToAdd));
                done=!addOne(counterMax,counterCurrent);
            }
            ergebnis = (ergebnisliste.toArray(ergebnis));
            return ergebnis;
        }
        return ergebnis;
    }


    private String IntArrayToStringHelper(int[] ints){
        if(ints.length==0) return "[]";
        String string="["+ints[0];
        for(int i=1; i<ints.length;i++){
            string=string.concat(","+ints[i]);
        }
        return string.concat("]");
    }

    /**
     * interates through every member of Max by essentlially counting
     * @param Max maximum of each collum
     * @param current current value of each collum
     * @return true if it added, false if it was already full
     */
    private boolean addOne(int[] Max, int[] current){
        int x=0;
        while(x<Max.length){
            if(current[x]<Max[x]){
                current[x]++;
                return true;
            }
            else{
                current[x]=0;
                x++;
            }
        }
        return false;
    }


    private void addRuleToFinalRules(EbnfRule ebnfRule){
        finalEbnfRules.add(ebnfRule);
    }

    /**
     * adds a new rule, with a unique name, with the name of rule if possible
     * @param ebnfRule the rule to be put in place, with the name you want
     * @return name of the left symbol, might be different from the parameter name if already taken
     */
    private String addRuleUniqueName(EbnfRule ebnfRule){
        String name=findUniqueName(ebnfRule.getLeftSymbol().getName());
        addRuleToFinalRules(
                new EbnfRule(
                        new NonTerminalSymbol(name),
                        ebnfRule.getRightSide()
                )
        );
        return name;
    }

    private String addRuleUniqueNameToOldRules(EbnfRule ebnfRule){
        String name=findUniqueName(ebnfRule.getLeftSymbol().getName());
        oldRules.add(
                new EbnfRule(
                        new NonTerminalSymbol(name),
                        ebnfRule.getRightSide()
                )
        );
        return name;
    }

    /**
     * find a unique names near the given argument name. If name is taken, it starts adding numbers behind.
     * not very clever, but it does its job
     * @param name the name you want
     * @return the unique name you deserve
     */
    private String findUniqueName(String name){
        Collection<String> takennames = new HashSet<>();
        for (EbnfRule oldRule : oldRules) {
            takennames.add(oldRule.getLeftSymbol().getName());
        }
        for (EbnfRule finalEbnfRule : finalEbnfRules) {
            takennames.add(finalEbnfRule.getLeftSymbol().getName());
        }
        String finalname=name;
        int x=2;
        while(true){
            if(takennames.contains(finalname)==false){
                //add name to rule and add it to our rules
                return finalname;
            }
            finalname=name+x;
            x++;
        }
    }
}
