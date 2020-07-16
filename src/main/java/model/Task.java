package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class contains getter and setter methods common to both EBNFTask and RegexTask
 */

public abstract class Task implements Serializable
{
    private static final long serialVersionUID = 420420420L;

    private boolean isSolved;
    private String description;
    private ArrayList<String> solution;
    private ArrayList<String> includedWords;
    private ArrayList<String> excludedWords;
    private ArrayList<String> hints;


    public boolean isSolved() {return isSolved;}
    public void solve() {this.isSolved = true;}

    public String getDescription() {return description;}
    public void setDescription(String desc) {this.description = desc;}

    public ArrayList<String> getSolution() {return solution;}
    public void setSolution(ArrayList<String> solution) {this.solution = solution;}

    public ArrayList<String> getIncludedWords() {return includedWords;}
    public void setIncludedWords(ArrayList<String> words) {this.includedWords = words;}

    public ArrayList<String> getExcludedWords() {return excludedWords;}
    public void setExcludedWords(ArrayList<String> words) {this.excludedWords = words;}

    public ArrayList<String> getHints() {return hints;}
    public void setHints(ArrayList<String> hints) {this.hints = hints;}
}
