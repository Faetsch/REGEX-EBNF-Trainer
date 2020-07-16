package controllers;

import com.sun.glass.ui.CommonDialogs;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.EBNFTask;
import model.EbnfGrammar;
import model.RegexTask;
import model.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller that handles the events in the Create Task UI.
 * @author Philipp Jung, Fatih Kesikli
 */
public class GUIControllerCreate {
    @FXML
    private ListView<String> tableSolutions;
    @FXML
    private Button btnSolution;
    @FXML
    private Button btnrmSolution;
    @FXML
    private TextArea txtTask;
    @FXML
    private TextArea txtSolution;
    @FXML
    private Button gen;
    @FXML
    private Button btnIncluded;
    @FXML
    private Button btnExcluded;
    @FXML
    private Button btnHints;
    @FXML
    private ListView<String> included;
    @FXML
    private ListView<String> excluded;
    @FXML
    private ListView<String> hints;
    @FXML
    private TextField txtincluded;
    @FXML
    private TextField txtexcluded;
    @FXML
    private TextField txthints;
    @FXML
    private Button btnRegex;
    @FXML
    private Button btnEBNF;
    @FXML
    private Button btnrmIncluded;
    @FXML
    private Button btnrmExcluded;
    @FXML
    private Button btnrmHint;
    @FXML
    private Button btnDirChooser;
    @FXML
    private TextArea txtnoPath;
    @FXML
    private Button btnFilename;
    @FXML
    private RadioButton radioRegex;
    @FXML
    private RadioButton radioEbnf;

    private int state = 0;
    private String path = null;
    private String filename = null;

    private List<String> inc;
    private List<String> exc;
    private List<String> hnts;
    private List<String> sol;

    private ArrayList<String> incTosave = new ArrayList<String>();
    private ArrayList<String> excTosave = new ArrayList<String>();
    private ArrayList<String> hntsTosave = new ArrayList<String>();
    private ArrayList<String> solTosave = new ArrayList<String>();
    private String task;

    private File lastDirectory;

    public void initialize()
    {
        ToggleGroup tg = new ToggleGroup();
        radioEbnf.setToggleGroup(tg);
        radioRegex.setSelected(true);
        radioRegex.setToggleGroup(tg);
        included.setPlaceholder(new Label("No words included yet"));
        excluded.setPlaceholder(new Label("No words excluded yet"));
        hints.setPlaceholder(new Label("No hints added yet"));
        tableSolutions.setPlaceholder(new Label("No example solutions added yet"));
    }

    /**
     * Old method to save files.
     * @deprecated Another method is used now. See firegen.
     */
    @FXML
    public void fireDirChooser(){
        Stage dirchooser = new Stage();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File dir = directoryChooser.showDialog(dirchooser);

        if (dir != null) {
            path = dir.getAbsolutePath();
            txtnoPath.setText("SELECTED PATH:" + path + "/! NEED TO SELECT FILENAME !");
            txtnoPath.setStyle("-fx-text-fill: red;");

            if(filename != null){

                String FullPath = path + "/" + filename + ".xmlret";
                txtnoPath.setText("SELECTED PATH:" + FullPath);
                txtnoPath.setStyle("-fx-text-fill: green;");
            }

        } else {
            path = null;
        }
    }

    /**
     * Removes a selected item in the ListView that saves words to include in the targeted language
     */
    @FXML
    public void firermIncluded(){
        final int selectedIdx = included.getSelectionModel().getSelectedIndex();
        if (selectedIdx != -1) {
            String itemToRemove = included.getSelectionModel().getSelectedItem();

            final int newSelectedIdx =
                    (selectedIdx == included.getItems().size() - 1)
                            ? selectedIdx - 1
                            : selectedIdx;

            included.getItems().remove(selectedIdx);
            included.getSelectionModel().select(newSelectedIdx);
        }
    }

    /**
     * Removes a selected item in the ListView that saves words to exclude in the targeted language
     */

    @FXML
    public void firermExcluded(){
        final int selectedIdx = excluded.getSelectionModel().getSelectedIndex();
        if (selectedIdx != -1) {
            String itemToRemove = excluded.getSelectionModel().getSelectedItem();

            final int newSelectedIdx =
                    (selectedIdx == excluded.getItems().size() - 1)
                            ? selectedIdx - 1
                            : selectedIdx;

            excluded.getItems().remove(selectedIdx);
            excluded.getSelectionModel().select(newSelectedIdx);
        }
    }

    /**
     * Removes a selected item in the ListView that saves hints to solve the task
     */

    @FXML
    public void firermHint(){
        final int selectedIdx = hints.getSelectionModel().getSelectedIndex();
        if (selectedIdx != -1) {
            String itemToRemove = hints.getSelectionModel().getSelectedItem();

            final int newSelectedIdx =
                    (selectedIdx == hints.getItems().size() - 1)
                            ? selectedIdx - 1
                            : selectedIdx;

            hints.getItems().remove(selectedIdx);
            hints.getSelectionModel().select(newSelectedIdx);
        }
    }

    /**
     * This method was used to change modes between an EBNF and Regex task. See firegen for new method.
     * @deprecated To determine whether the created task is in EBNF or Regex, radio buttons are used.
     */
    @FXML
    public void fireregex(){
        if(state == 0)
        {
            state = 1;
            btnRegex.setText("EBNF selected");
        }

        else if(state == 1)
        {
            state = 0;
            btnRegex.setText("Regex selected");
        }
    }

    /**
     * Method called by firegen to set the vars for creating a task object
     */
    private void setVars(){
        task = txtTask.getText();

        inc = included.getItems();
        exc = excluded.getItems();
        hnts = hints.getItems();
        sol = tableSolutions.getItems();

        ArrayList<String> ic = new ArrayList<String>(inc);
        ArrayList<String> ex = new ArrayList<String>(exc);
        ArrayList<String> ht = new ArrayList<String>(hnts);
        ArrayList<String> sl = new ArrayList<String>(sol);
        incTosave = ic;
        excTosave = ex;
        hntsTosave = ht;
        solTosave = sl;
    }

    /**
     * Generate Button, which saves the task in xmlret and calls the TaskSerializationController. Opens a saving dialog.
     */
    @FXML
    public void firegen(){
        setVars();
        if(isError(incTosave, excTosave, hntsTosave, task, solTosave))
        {
            return;
        }
        String FullPath = path + "/" + filename + ".xmlret";
        TaskSerializationController saver = new TaskSerializationController();
        //new save dialog
        FileChooser saveChooser = new FileChooser();
        saveChooser.setTitle("Save Task File");
        saveChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("REGEX/EBNF-Training files", ".xmlret"));

        if(lastDirectory != null && lastDirectory.isDirectory())
        {
            saveChooser.setInitialDirectory(lastDirectory);
        }
        else
        {
            saveChooser.setInitialDirectory(new File("Default Tasks"));
        }

        File file = saveChooser.showSaveDialog(null);


        if(file != null)
        {
            String fn = file.getAbsolutePath();
            if(fn.endsWith("/.xmlret"))
            {
                return;
            }
            if(!fn.endsWith(".xmlret"))
            {
                fn +=".xmlret";
            }
            Task createdTask = null;

            if(radioRegex.isSelected()) { createdTask = new RegexTask(); }
            else if(radioEbnf.isSelected()) { createdTask = new EBNFTask(); }

            createdTask.setDescription(task);
            createdTask.setSolution(solTosave);
            createdTask.setIncludedWords(incTosave);
            createdTask.setExcludedWords(excTosave);
            createdTask.setHints(hntsTosave);
            try
            {
                //saver.saveTaskInXML(file.getAbsolutePath(), createdTask);
                saver.saveTaskInXML(fn, createdTask);
                File parentDir = file.getParentFile();
                if(parentDir.exists())
                {
                    lastDirectory = parentDir;
                }
                Alert correctAlert = new Alert(Alert.AlertType.INFORMATION);
                correctAlert.setTitle("File saved");
                correctAlert.setHeaderText(null);
                correctAlert.setContentText("Task successfully created and saved!");
                correctAlert.showAndWait();
            }
            catch (FileNotFoundException e)
            {
                throwAlert("Could not save file", "File could not be saved. Check if\nyou have permissions to write in this directory.");
            }
        }
        //new save dialog

//        if(state == 0) {
//            txtnoPath.setText("xmlret is saving in:" + FullPath);
//
//            RegexTask rgxTask = new RegexTask();
//            rgxTask.setDescription(task);
//            rgxTask.setSolution(solution);
//            rgxTask.setIncludedWords(incTosave);
//            rgxTask.setExcludedWords(excTosave);
//            rgxTask.setHints(hntsTosave);
//
//            try {
//                saver.saveTaskInXML(FullPath, rgxTask);
//            }
//            catch(IOException e){
//                e.printStackTrace();
//            }
//        }
//        else {
//
//            txtnoPath.setText("xmlret is saving in:" + FullPath);
//            EBNFTask ebnfTask = new EBNFTask();
//            ebnfTask.setDescription(task);
//            ebnfTask.setSolution(solution);
//            ebnfTask.setIncludedWords(incTosave);
//            ebnfTask.setExcludedWords(excTosave);
//            ebnfTask.setHints(hntsTosave);
//
//            try {
//                saver.saveTaskInXML(FullPath, ebnfTask);
//            }
//            catch(IOException e){
//                e.printStackTrace();
//            }
//        }

    }

    /**
     * Add Buttons for adding string to the listViews
     */
    @FXML
    public void fireIncluded(){
        String includedString = txtincluded.getText();

        ObservableList<String> items = included.getItems();
        items.add(includedString);
    };
    @FXML
    public void fireExcluded(){
        String excludedString = txtexcluded.getText();

        ObservableList<String> items = excluded.getItems();
        items.add(excludedString);


    };
    @FXML
    public void fireHints(){
        String hintString = txthints.getText();

        ObservableList<String> items = hints.getItems();
        items.add(hintString);


    }


    @FXML
    public void fireSolution(){
        String solString = txtSolution.getText();

        ObservableList<String> items = tableSolutions.getItems();
        items.add(solString);


    }

    @FXML
    public void firermSolution(){
        final int selectedIdx = tableSolutions.getSelectionModel().getSelectedIndex();
        if (selectedIdx != -1) {
            String itemToRemove = tableSolutions.getSelectionModel().getSelectedItem();

            final int newSelectedIdx =
                    (selectedIdx == tableSolutions.getItems().size() - 1)
                            ? selectedIdx - 1
                            : selectedIdx;

            tableSolutions.getItems().remove(selectedIdx);
            tableSolutions.getSelectionModel().select(newSelectedIdx);
        }
    }

    /**
     * This Method checks the user input, if the user did not fill all input boxes
     * @param inclu included words
     * @param exclu excluded words
     * @param hts hints
     */

    public boolean isError(ArrayList<String> inclu, ArrayList<String> exclu, ArrayList<String> hts, String task, ArrayList<String> sol){
        String feedback = "\n";

        boolean ta = true;
        boolean so = true;
        boolean in = true;
        boolean ex = true;


        if(task.trim() == null || task.trim().isEmpty()){
            feedback = feedback + "NO TASK DESCRIPTION SET\n";
            ta = false;
        }
        if(sol.isEmpty()){
            feedback = feedback + "NO SOLUTIONS SET\n";
            so = false;
        }
        if(inclu.isEmpty()){
            feedback = feedback + "NO INCLUDED WORDS SET\n";
            in = false;
        }
        if(exclu.isEmpty()){
            feedback = feedback + "NO EXCLUDED WORDS SET\n";
            ex = false;
        }



        if(ta&& so && in && ex)
        {
            if(radioEbnf.isSelected()){
                for(String currentloesung : sol){
                    EbnfController ebnfController=new EbnfController();
                    try{
                    ebnfController.setCykAlgoritmusHandler(
                            new CykAlgoritmusHandler(
                                    EbnfToCnfHandler.EbnfToCnf(
                                     LudwigEbnfParser.parseInput(currentloesung)
                                    )
                            )
                    );}
                    catch (ParseException p){
                        throwAlert("Fehler","Musterloesung\n"+currentloesung+"\n cant be parsed.");
                        return true;
                    }
                    for(String wort : inclu){
                            if( ebnfController.testWord(wort) == false){
                                throwAlert("Fehler","Musterloesung\n"+currentloesung+
                                        "\ncant create included Word \""+wort+"\"");
                                return true;
                            }
                    }
                    for(String wort : exclu){
                        if( ebnfController.testWord(wort)){
                            throwAlert("Fehler","Musterloesung\n"+currentloesung+
                                    "\ndoes create excluded Word \""+wort+"\"");
                            return true;
                        }
                    }
                }
                return false;
            } else {
                for(String currentloesung : sol){
                    RegexController regexController=new RegexController();
                    for(String wort : inclu){
                        if( regexController.testWord(currentloesung, wort) == false){
                            throwAlert("Error","at example solution:\n"+currentloesung+
                                    "\nCan't infer \""+wort+" \"which should be included in L but isn't.");
                            return true;
                        }
                    }
                    for(String wort : exclu){
                        if( regexController.testWord(currentloesung, wort)){
                            throwAlert("Error","at example solution:\n"+currentloesung+
                                    "\""+wort+"\" should not be inferable but it is.");
                            return true;
                        }
                    }
                }
                return false;
            }

        }

        else
            {
            throwAlert("Missing Information", ("Please fill in the missing Information:\n"+feedback));
            return true;
        }
    }
    public void throwAlert(String title,String content){
        Alert incorrectAlert = new Alert(Alert.AlertType.ERROR);
        incorrectAlert.setTitle(title);
        incorrectAlert.setHeaderText(null);
        incorrectAlert.setContentText(content);
        incorrectAlert.showAndWait();

    }

    private void checkpath(){
        if(path != null) {
            String FullPath = path + "/" + filename + ".xmlret";
            txtnoPath.setText("SELECTED PATH:" + FullPath);
            txtnoPath.setStyle("-fx-text-fill: green;");

        }
        else{
            String FullPath = "! NEED TO SELECT PATH !" + "/" + filename + ".xmlret";
            txtnoPath.setText("SELECTED PATH:" + FullPath);
            txtnoPath.setStyle("-fx-text-fill: red;");
        }
    }

}
