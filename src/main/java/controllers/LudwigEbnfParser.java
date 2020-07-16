package controllers;


import model.*;

import java.util.*;

/**
 * EBNF-Parser that is currently in use.
 *
 * @author Ludwig Scheuring
 */

public class LudwigEbnfParser {

    public static EbnfGrammar parseInput(String userInput) throws ParseException {
        try {
            //bisschen in form bringen
            userInput = inFromBringen(userInput);
            correctParenthesesOutsideTerminals(userInput);
            //debug:
            //System.out.println(userInput);
            //random test change for git
            String[] strings = userInput.split("\n");
            List<EbnfRule> ebnfRuleList = new ArrayList<>();
            for (String string : strings) {
                string = string.trim();
                if (string.charAt(string.length() - 1) == ';') {
                    string = string.substring(0, string.length() - 1);
                }
                if (string.length() != 0) {
                    ebnfRuleList.add(toRule(string));
                }
            }
            //System.out.println("done");
            EbnfGrammar ret = new EbnfGrammar(ebnfRuleList.toArray(new EbnfRule[]{}));
            System.out.println(ret.toText());
            return ret;
        } catch (Exception e) {
            if (e instanceof ParseException) {
                throw e;
            } else {
                throw new ParseException("Error while Parsing");
            }
        }
    }


    /**
     * make it so the user input can be properly parsed by our classes later,
     * making sure not to touch anything inside a Terminal ( "a(" stuff)
     *
     * @param userInput what you want to be getting in shape
     * @return userInput, but stronger
     */
    private static String inFromBringen(String userInput) {
        userInput = addPaddingToHypos(userInput);
        userInput = replaceOutsideTerminalsLevel(userInput, ',', " ");
        userInput = replaceOutsideTerminalsLevel(userInput, '+', " ");
        userInput = replaceOutsideTerminalsLevel(userInput, '(', " ( ");
        userInput = replaceOutsideTerminalsLevel(userInput, ')', " ) ");
        userInput = replaceOutsideTerminalsLevel(userInput, '[', " [ ");
        userInput = replaceOutsideTerminalsLevel(userInput, ']', " ] ");
        userInput = replaceOutsideTerminalsLevel(userInput, '{', " { ");
        userInput = replaceOutsideTerminalsLevel(userInput, '}', " } ");
        while (containsOutsideTerminals(userInput, "  ")) {
            userInput = replaceOutsideTerminalsLevel(userInput, "  ", " ");
        }
        //for those ::=, := and = folk to all live happily together ever after
        userInput = replaceOutsideTerminalsLevel(userInput, ":=", "=");
        userInput = replaceOutsideTerminalsLevel(userInput, ":=", "=");
        return userInput;
    }

    private static void correctParenthesesOutsideTerminals(String s) throws ParseException {
        String[] strings = s.split("\n");
        for (String ruleLine : strings) {
            try {
                lineCorrectParentheses(ruleLine);
            } catch (ParseException p) {
                throw new ParseException("Parentheses error in Line \"" + ruleLine + "\": "
                        + p.getMessage());
            }

        }
    }

    private static void lineCorrectParentheses(String s) throws ParseException {
        //System.out.println("checking line "+s);
        //inspiration for this Stack based approach found online
        LinkedList<Character> stack = new LinkedList<>();
        char[] chars = s.toCharArray();
        boolean inATerminal = false;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            //System.out.println(" is the stack rn");
            //System.out.println(c+" is being processed");
            if (c == '"') {
                inATerminal = !inATerminal;
                //System.out.println("\" picked up, entering "+inATerminal+" mode.");
            }
            if (!inATerminal) {
                switch (c) {
                    case '(':
                        stack.push(c);
                        break;
                    case '{':
                        stack.push(c);
                        break;
                    case '[':
                        stack.push(c);
                        break;
                    case ')':
                        if (stack.size() == 0) {
                            throw new ParseException("Wrong '" + c + "' at index " + i);
                        }
                        char test = stack.pop();
                        if (!(test == '(')) {
                            throw new ParseException("Last opened Parenthesis " + test + " isnt ( despite current being "
                                    + c + " at index " + i);
                        }
                        break;
                    case ']':
                        if (stack.size() == 0) {
                            throw new ParseException("Wrong '" + c + "' at index " + i);
                        }
                        char test2 = stack.pop();
                        if (!(test2 == '[')) {
                            throw new ParseException("Last opened Parenthesis " + test2 + " isnt [ despite current being "
                                    + c + " at index " + i);
                        }
                        break;
                    case '}':
                        if (stack.size() == 0) {
                            throw new ParseException("Wrong '" + c + "' at index " + i);
                        }
                        char test3 = stack.pop();
                        if (!(test3 == '{')) {
                            throw new ParseException("Last opened Parenthesis " + test3 + " isnt { despite current being "
                                    + c + " at index " + i);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        if (inATerminal) {
            throw new ParseException("One too many \" in this line");
        }
        if (stack.size() != 0) {
            String messge = "Unclosed Parentheses, stack still is ";
            for (Character character : stack) {
                messge = messge + character.toString();
            }
            throw new ParseException(messge);
        }
    }

    /**
     * check if string s contains the string what outside of a terminal
     *
     * @param s    the string you be checking
     * @param what what you are checking string s for
     * @return true if s does contain @what outside of a terminal, false otherwise
     */
    private static boolean containsOutsideTerminals(String s, String what) {
        boolean insideTerminal = false;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '"') {
                insideTerminal = !insideTerminal;
            }
            if (insideTerminal == false) {
                if (s.substring(i).startsWith(what)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String replaceOutsideTerminalsLevel(String s, char whatToReplace, String replacement) {
        char[] sAsCharArray = s.toCharArray();
        char[] replacmentAsCharArray = replacement.toCharArray();
        boolean inATerminal = false;
        List<Character> modifiedSAsList = new ArrayList<>();
        for (char c : sAsCharArray) {
            if (c == '"') {
                inATerminal = !inATerminal;
            }
            if (c != whatToReplace | inATerminal) {
                modifiedSAsList.add(c);
            } else {
                for (char c2 : replacmentAsCharArray) {
                    modifiedSAsList.add(c2);
                }
            }
        }
        Character[] ret = new Character[]{};
        ret = modifiedSAsList.toArray(ret);
        String returnValueAsString = "";
        for (Character character : ret) {
            returnValueAsString = returnValueAsString.concat(character.toString());
        }
        return returnValueAsString;
    }

    private static String addPaddingToHypos(String s) {
        char[] sAsCharArray = s.toCharArray();
        boolean inATerminal = false;
        List<Character> modifiedSAsList = new ArrayList<>();
        for (char c : sAsCharArray) {
            if (c == '"') {
                inATerminal = !inATerminal;
                if (inATerminal) {
                    modifiedSAsList.add(' ');
                    modifiedSAsList.add('"');
                } else {
                    modifiedSAsList.add('"');
                    modifiedSAsList.add(' ');
                }
            } else {
                modifiedSAsList.add(c);
            }
        }
        Character[] ret = new Character[]{};
        ret = modifiedSAsList.toArray(ret);
        String returnValueAsString = "";
        for (Character character : ret) {
            returnValueAsString = returnValueAsString.concat(character.toString());
        }
        return returnValueAsString;
    }

    private static String replaceOutsideTerminalsLevel(String s, String whatToReplace, String replacement) {
        if (whatToReplace.length() == 0) {
            throw new IllegalArgumentException("whatToReplace cant be empty");
        }
        char[] sAsCharArray = s.toCharArray();
        char[] replacmentAsCharArray = replacement.toCharArray();
        boolean inATerminal = false;
        List<Character> modifiedSAsList = new ArrayList<>();
        for (int i = 0; i < sAsCharArray.length; ) {
            if (s.charAt(i) == '"') {
                inATerminal = !inATerminal;
            }
            if (!s.substring(i).startsWith(whatToReplace) | inATerminal) {
                modifiedSAsList.add(s.charAt(i));
                i++;
            } else {
                for (char c2 : replacmentAsCharArray) {
                    modifiedSAsList.add(c2);
                }
                i += whatToReplace.length();
            }
        }
        Character[] ret = new Character[]{};
        ret = modifiedSAsList.toArray(ret);
        String returnValueAsString = "";
        for (Character character : ret) {
            returnValueAsString = returnValueAsString.concat(character.toString());
        }
        return returnValueAsString;
    }


    private static String removeSpacesAroundChar(String string, char c) {
        while (string.contains(c + " ")) {
            string = string.replace(c + " ", Character.toString(c));
        }
        while (string.contains(" " + c)) {
            string = string.replace(" " + c, Character.toString(c));
        }
        return string;
    }

    /**
     * turns one string into a Rule
     *
     * @param s the string
     * @return the Rule
     * @throws ParseException if the string is not a rule
     */
    private static EbnfRule toRule(String s) throws ParseException {

        String[] strings = splitZeroLevelBy('=', s);
        if (strings.length < 2) {
            throw new ParseException(":= kommt nicht vor");
        }
        if (strings.length > 2) {
            throw new ParseException(":= kommt zu oft in einer Zeile vor");
        }
        EbnfElement ebnfElementlinks = toEbnfElement(strings[0]);
        EbnfElement ebnfElementrechts = toEbnfElement(strings[1]);
        if (ebnfElementlinks instanceof NonTerminalSymbol == false) {
            throw new ParseException("was links von := steht ist kein NichtTerminal Symbol");
        }
        return new EbnfRule(
                ((NonTerminalSymbol) ebnfElementlinks),
                ebnfElementrechts
        );
    }


    private static String[] splitZeroLevelBy(char c, String s) {
        return splitZeroLevelByHelper(c, s).toArray(new String[]{});
    }

    private static List<String> splitZeroLevelByHelper(char c, String s) {
        int current = 0;
        int currentlvl = 0;
        boolean inATerminalSymbol = false;
        List<String> stringList = new ArrayList<>();
        while (current < s.length()) {
            char ch = s.charAt(current);
            if (ch == '"') {
                inATerminalSymbol = !inATerminalSymbol;
            }
            if ((ch == '(' || ch == '{' || ch == '[') && !inATerminalSymbol) {
                currentlvl++;
            }
            if ((ch == ')' || ch == '}' || ch == ']') && !inATerminalSymbol) {
                currentlvl--;
            }
            if (ch == c && currentlvl == 0 && !inATerminalSymbol) {
                stringList.add(s.substring(0, current));
                stringList.addAll(splitZeroLevelByHelper(
                        c,
                        s.substring(current + 1, s.length())
                ));
                return stringList;
            }
            current++;
        }
        stringList.add(s);
        return stringList;
    }

    private static EbnfElement toEbnfElement(String s) throws ParseException {
        //remove klammern around, also whitespaces
        s = s.trim();
        s = removeKlammernAusenherum(s);
        //System.out.println("starting on: "+s);
        //maybe should not do this. maybe should. only time will tell
        if (s.length() == 0) return new EbnfEmpty();
        //remove whitespace left and right
        String[] strings = splitZeroLevelBy('|', s);
        if (strings.length > 1) {
            EbnfElement[] ebnfElements = new EbnfElement[strings.length];
            for (int i = 0; i < ebnfElements.length; i++) {
                ebnfElements[i] = toEbnfElement(strings[i]);
            }
            return new EbnfOrSum(ebnfElements);
        }

        strings = splitZeroLevelBy(' ', s);
        if (strings.length > 1) {
            EbnfElement[] ebnfElements = new EbnfElement[strings.length];
            for (int i = 0; i < ebnfElements.length; i++) {
                ebnfElements[i] = toEbnfElement(strings[i]);
            }
            return new EbnfSum(ebnfElements);
        }


        if (s.charAt(0) == '[' && s.charAt(s.length() - 1) == ']') {
            return new EbnfOptional(
                    toEbnfElement(s.substring(1, s.length() - 1))
            );
        }
        if (s.charAt(0) == '{' && s.charAt(s.length() - 1) == '}') {
            return new EbnfAnyNumberOfTimes(
                    toEbnfElement(s.substring(1, s.length() - 1))
            );
        }

        if (s.contains("\"")) {
            if (s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"' && !s.substring(1, s.length() - 1).contains("\"")) {
                return new TerminalSymbol(s.substring(1, s.length() - 1));
            }
            strings = splitAroundAnfuerungszeichen(s);
            EbnfElement[] ebnfElements = new EbnfElement[strings.length];
            for (int i = 0; i < ebnfElements.length; i++) {
                ebnfElements[i] = toEbnfElement(strings[i]);
            }
            return new EbnfSum(ebnfElements);
        }


        return new NonTerminalSymbol(s);
    }

    private static String[] splitAroundAnfuerungszeichen(String s) {
        //we know that the whole thing cant be surrounded by ()
        // because we already removes those () earlier
        //find first two "" and seperates them from da rest
        List<String> stringList = new ArrayList<>();
        int i = 0;
        while (s.charAt(i) != '"') {
            i++;
        }
        if (i > 0) {
            stringList.add(s.substring(0, i));
            s = s.substring(i, s.length());
        }
        i++;
        while (s.charAt(i) != '"') {
            i++;
        }
        if (i < s.length() - 1) {
            stringList.add(s.substring(i + 1, s.length()));
            s = s.substring(0, i + 1);
        }
        stringList.add(0, s); //thingy at index 0 is always the terminalSymbol
        return stringList.toArray(new String[]{});
    }

    private static String[] splitAroundKlammern(String s) throws ParseException {
        //we know that the whole thing cant be surrounded by ()
        // because we already removes those () earlier
        //we should never go here in theory
        System.out.println("we should not be here");
        throw new ParseException("inkorrekte Klammerung");
        /*List<String> stringList=new ArrayList<>();
        int i=0;
        while(s.charAt(i)!='('){
            i++;
        }
        if(i>0){
            stringList.add(s.substring(0,i));
            s=s.substring(i, s.length());
        }
        i++;
        int klammerInceptionLevel=1;
        while(s.charAt(i)!=')' || klammerInceptionLevel!=1){
            if(s.charAt(i)=='(') klammerInceptionLevel++;
            if(s.charAt(i)==')') klammerInceptionLevel--;
            i++;
        }
        if(i<s.length()-1){
            stringList.add(s.substring(i+1,s.length()));
            s=s.substring(0,i+1);
        }
        stringList.add(s);
        return stringList.toArray(new String[]{});//*/
    }

    private static String removeKlammernAusenherum(String s) {
        while (unnoetigeKlamemrnAusen(s)) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }

    private static boolean unnoetigeKlamemrnAusen(String input) {
        //System.out.println("working on "+ input);
        //todo: make it so this finds errors in the klammersetzung
        if (input.length() == 0) {
            return false;
        }
        if (input.charAt(0) != '(') {
            return false;
        } else {
            int lvl = 0;
            int a = 0;
            do {
                if (a >= input.length()) {
                    return false;
                }
                if (input.charAt(a) == '(') {
                    ++lvl;
                }
                if (input.charAt(a) == ')') {
                    --lvl;
                }
                if (a == input.length() - 1 && input.charAt(a) == ')') {
                    return lvl == 0;
                }
                ++a;
            } while (lvl >= 1);
            return false;
        }
    }

    /*public static void main(String[] args) {
        testInFromBringen();
    }*/

    public static void testInFromBringen() {
        try {
            EbnfGrammar ebnfGrammar = parseInput(
                    "Start={A}\n" +
                            " A :=    {A}    \" ( \"{A}\" )  \"{A} \n" +
                            "A::=  \"a\"\n"
            );
            System.out.println(ebnfGrammar.toText());
            EbnfToCnfHandler ebnfToCnfHandler = new EbnfToCnfHandler();
            ebnfGrammar = ebnfToCnfHandler.makeEbnfIntoCnf(ebnfGrammar);
            System.out.println(ebnfGrammar.toText());
        } catch (ParseException e) {
            System.out.println("this didnt work out");
            System.out.println("reason: ");
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }
    }

    private static void testSplit() {
        String[] strings = splitZeroLevelBy('|', "1|23|45(6|7)890|");
        for (String s : strings) {
            System.out.println(s);
        }
    }

    private static void test() {
        try {
            EbnfGrammar ebnfGrammar = parseInput(
                    "Start={A}\n" +
                            " A = {A}\"(\"{A}\")\"{A} \n" +
                            "A=\"a\"\n"
            );
            System.out.println(ebnfGrammar.toText());
            EbnfToCnfHandler ebnfToCnfHandler = new EbnfToCnfHandler();
            ebnfGrammar = ebnfToCnfHandler.makeEbnfIntoCnf(ebnfGrammar);
            System.out.println(ebnfGrammar.toText());
        } catch (ParseException e) {
            System.out.println("this didnt work out");
            System.out.println("reason: ");
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }
    }

    private static void testChars() {
        for (int i = 0; i < 100; i++) {
            System.out.println("char nr." + i + " is >" + ((char) i) + "<");
        }
    }

}
