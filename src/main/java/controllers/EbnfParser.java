package controllers;


import model.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class was used to parse plain text to a structued EBNF-program.
 * @deprecated see LudwigEbnfParser for the currently used version
 * @author Fatih Kesikli, Ludwig Scheuring
 */
public class EbnfParser
{
    /**
     * Parses a given String to a structured EBNF-tree
     * @param text plaintext to parse
     * @return parsed EBNF-program
     * @throws ParseException thrown, when a rule could not be parsed due to illegal rule structure
     */
    public static EbnfGrammar parseTextToEbnfGrammar(String text) throws ParseException
    {
        String[] splitProgramIntoLines = text.split("\n");
        EbnfRule[] rules = new EbnfRule[splitProgramIntoLines.length];
        for(int i = 0; i < splitProgramIntoLines.length; i++)
        {
            rules[i] = parseRule(splitProgramIntoLines[i]);
        }
        return new EbnfGrammar(rules);
    }

    /**
     * Parses an EBNF Rule
     * @param text one single EBNF rule
     * @return parsed EBNF structure
     * @throws ParseException if string to parse is not a valid rule
     */
    public static EbnfRule parseRule(String text) throws ParseException
    {
        String[] splitText = text.split(":=");
        if(splitText.length != 2) throw new ParseException(text + ": not a valid rule.");
        String identifier = splitText[0];
        return new EbnfRule(new NonTerminalSymbol(identifier), parseRightSide(splitText[1]));
    }

    /**
     * Parses the right side of an EBNF rule
     * @param text right side of a rule to parse
     * @return a structured EBNF right side
     * @throws ParseException if the string is not a valid right side
     */
    public static EbnfElement parseRightSide(String text) throws ParseException
    {
//        System.out.println(text);
//        Pattern disjunctionPattern = Pattern.compile("\\((.+)\\|(.+)\\)");
//        Pattern concatenationPattern = Pattern.compile("\\((.+),(.+)\\)");
//        Pattern loopPattern = Pattern.compile("\\{(.+)\\}");
//        Pattern groupPattern = Pattern.compile("\\((.+)\\)");
//        Pattern terminalPattern = Pattern.compile("\"(.*)\"");
//        Pattern optionPattern = Pattern.compile("\\[(.+)\\]");
//        Pattern identifierPattern = Pattern.compile("([a-zA-Z0-9_<>]+)");

        //TODO new patterns
        Pattern disjunctionPattern = Pattern.compile("\\s*([\\w_<>]+)\\s*\\|\\s*(.+)\\s*");
        Pattern concatenationPattern = Pattern.compile("\\s*([\\w_<>]+)\\s*,\\s*(.+)\\s*");
        Pattern loopPattern = Pattern.compile("\\{(.+)\\}");
        Pattern groupPattern = Pattern.compile("\\((.+)\\)");
        Pattern terminalPattern = Pattern.compile("\"(.*)\"");
        Pattern optionPattern = Pattern.compile("\\[(.+)\\]");
        Pattern identifierPattern = Pattern.compile("([a-zA-Z0-9_<>]+)");

        if(!hasCorrectAmountOfBrackets(text)) throw new ParseException(text + ": wrong amount of brackets.");

        String stringWithoutBrackets = trimBrackets(text);
        //TODO adapt patterns to trimmed string
        Matcher m = terminalPattern.matcher(text);
        if(m.matches()) { return new TerminalSymbol(m.group(1)); }

        m = loopPattern.matcher(text);
        if(m.matches()) { return new EbnfAnyNumberOfTimes(parseRightSide(m.group(1))); }

        m = optionPattern.matcher(text);
        if(m.matches()) { return new EbnfOptional(parseRightSide(m.group(1))); }

        char lowestOp = getLowestOperator(text);
//        System.out.println("Lowest op is " + lowestOp);
        if(lowestOp == '|')
        {
            m = disjunctionPattern.matcher(text);
            if(m.matches())
            {
                EbnfElement[] leftAndRightElement = {parseRightSide(m.group(1)), parseRightSide(m.group(2))};
                return new EbnfOrSum(leftAndRightElement);
            }
        }

        else if(lowestOp == ',')
        {
            m = concatenationPattern.matcher(text);
            if(m.matches())
            {
                EbnfElement[] leftAndRightElement = {parseRightSide(m.group(1)), parseRightSide(m.group(2))};
                return new EbnfSum(leftAndRightElement);
            }

        }
        m = groupPattern.matcher(text);
        if(m.matches())
        {
            //SPAGHETTI HACK SPAGHETTI AJSHDKAJHDLKJAHDLAJHDJADGDSA
            EbnfElement[] singleGroupElementInSpaghettiArray = {parseRightSide(m.group(1))};
            return new EbnfSum(singleGroupElementInSpaghettiArray);
        }

        m = identifierPattern.matcher(text);
        if(m.matches())
        {
            return new NonTerminalSymbol(m.group(1));
        }

        throw new ParseException(text + ": could not be parsed to a right hand side");
    }

    private static char getLowestOperator(String text)
    {
        int currLevel = 0;
        for(int i = 0; i < text.length(); i++)
        {
            if(text.charAt(i) == '(') currLevel++;
            if(text.charAt(i) == ')') currLevel--;
            if((text.charAt(i) == ',' || text.charAt(i) == '|') && currLevel == 1) return text.charAt(i);
        }
        return '?';
    }

    private static boolean hasCorrectAmountOfBrackets(String text)
    {
        int stack = 0;
        for(int i = 0; i < text.length(); i++)
        {
            if(text.charAt(i) == '(') stack++;
            if(text.charAt(i) == ')') stack--;
        }
        return stack == 0;
    }

    //TODO: test this method
    private static String trimBrackets(String text)
    {
        String temp = text.trim();
        if(temp.startsWith("(") && temp.endsWith(")"))
        {
            temp = trimBrackets(temp.substring(1, temp.length()-1));
        }
        return temp;
    }

}
