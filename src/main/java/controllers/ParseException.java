package controllers;

/**
 * Thrown, when a given string could not be parsed into EBNF structure
 */

public class ParseException extends Exception
{
    public ParseException() {}
    public ParseException(String message)
    {
        super(message);
    }
}
