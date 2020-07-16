package controllers;

import model.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the controller class used to check whether the user correctly solved a regex task.
 * Since the check whether two regular expressions describe the same language involves translating each regex
 * into a NFA which then has to be translated into a DFA via the powerset constrution algorithm - which is known to be
 * NP-hard - we have to check the correctness of the user's regex by matching a list of included words of our correct
 * language and a list of excluded words.
 *
 * @author Fatih Kesikli
 */

public class RegexController {
    private Matcher matcher;
    private RegexTask task;

    /**
     * sets the current task the user works on
     * @param task current task
     */
    public void setTask(RegexTask task) {
        this.task = task;
    }

    public RegexTask getTask() {
        return this.task;
    }

    private void setMatcher(Matcher m) {
        this.matcher = m;
    }

    private Matcher getMatcher() {
        return matcher;
    }

    /**
     * tests whether the user's suggested solution includes all words that are supposed
     * to be included and excludes all words that are supposed to be excluded
     * @param suggestedSolution the user's suggested regular expression
     * @return true, if the user's suggested solution matches the words according to the current task's definition
     */
    public boolean test(String suggestedSolution) {
        ArrayList<String> includedWords = getTask().getIncludedWords();
        ArrayList<String> excludedWords = getTask().getExcludedWords();

        //checks all words to be included in the solution language
        for (String word : includedWords) {
//            System.out.println("testing included");
//            System.out.println(word);
            if (!testWord(suggestedSolution, word)) return false;
        }

        //checks all words to be excluded in the solution language
        for (String word : excludedWords) {
//            System.out.println("testing excluded");
//            System.out.println(word);
            if (testWord(suggestedSolution, word)) return false;
        }

        return true;
    }

    /**
     * tests whether the user's suggested solution includes a given word
     * @param suggestedSolution the user's suggested regular expression
     * @param word word to match
     * @return true if the word is element of the specified regular expression
     */
    public boolean testWord(String suggestedSolution, String word)
    {
        Pattern p = Pattern.compile(suggestedSolution);
        setMatcher(p.matcher(word));
        if(getMatcher().matches()) return true;
        else return false;
    }
}
