package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.EBNFTask;
import model.RegexTask;
import model.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class defines the actions to fire when interacting with the Desktop-UI. More specifically,
 * this class makes use of all the controller classes in /controllers/
 *
 * @author Fatih Kesikli, Philipp Jung, Ludwig Scheuring
 */

public class GUIController {
    private RegexController regexController;
    private TaskSerializationController taskController = new TaskSerializationController();
    @FXML
    private Button btnTest;
    @FXML
    private Button btnLoadTask;
    @FXML
    private Button btnNextExercise;
    @FXML
    private TextArea txtInputSolution;
    @FXML
    private Button btnSolve;
    @FXML
    private FlowPane paneIncluded;
    @FXML
    private FlowPane paneExcluded;
    @FXML
    private Label lblTaskDescription;
    @FXML
    private Button btnCreateTask;
    @FXML
    private TextArea txtSolutions;
    @FXML
    private Label lblHints;
    private ArrayList<Label> lblIncludeds = new ArrayList<Label>();
    private ArrayList<Label> lblExcludeds = new ArrayList<Label>();
    private Task currTask;
    private int currHintIndex = -1;
    private File currTaskFile;
    private boolean showWords = false;


    /**
     * This method initializes controllers
     */
    @FXML
    public void initialize() {
        regexController = new RegexController();
    }

    /**
     * This method is fired when the a task is loaded manually
     */
    @FXML
    public void fireLoadTask(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("REGEX/EBNF-Training files", "*.xmlret"));
        File initialDirectory = new File("Default Tasks");
        File lastDirectory = null;
        if (currTaskFile != null && currTaskFile.exists()) {
            lastDirectory = currTaskFile.getParentFile();
        }
//        System.out.println(initialDirectory.getAbsolutePath());
        if (lastDirectory != null && lastDirectory.exists()) {
            fileChooser.setInitialDirectory(lastDirectory);
        } else if (initialDirectory.exists() && initialDirectory.isDirectory()) {
//            System.out.println("ye");
            fileChooser.setInitialDirectory(initialDirectory);
        }

        File selectedFile = fileChooser.showOpenDialog(null);
        Task loadedTask = null;
        try {
            if (selectedFile != null) {
                loadedTask = taskController.readTaskFromXML(selectedFile.getPath());
                loadTaskIntoUI(loadedTask, selectedFile);
            }

        } catch (FileNotFoundException e1) {
            Alert incorrectAlert = new Alert(Alert.AlertType.ERROR);
            incorrectAlert.setTitle("Failed to load task");
            incorrectAlert.setHeaderText(null);
            incorrectAlert.setContentText("Could not load selected task:\nFile not found");
            incorrectAlert.showAndWait();
        } catch (ArrayIndexOutOfBoundsException e2) {
            Alert incorrectAlert = new Alert(Alert.AlertType.ERROR);
            incorrectAlert.setTitle("Failed to load task");
            incorrectAlert.setHeaderText(null);
            incorrectAlert.setContentText("Could not load selected task:\nCorrupt file");
            incorrectAlert.showAndWait();
        } catch (IllegalStateException e3) {
            Alert incorrectAlert = new Alert(Alert.AlertType.ERROR);
            incorrectAlert.setTitle("Failed to load task");
            incorrectAlert.setHeaderText(null);
            incorrectAlert.setContentText("Could not load selected task:\nCorrupt file");
            incorrectAlert.showAndWait();
        }

    }


    /**
     * This method handles the event when the "Test Solution" button is pressed
     *
     * @param e fired event
     */
    @FXML
    public void fireBtnTest(ActionEvent e) {
        if (currTask == null) return;
        if (!showWords) {
            for (String inc : currTask.getIncludedWords()) {
                Label l = new Label(" " + inc + " ");
                l.setStyle("-fx-border-color: black");
                lblIncludeds.add(l);
                paneIncluded.getChildren().add(l);
            }

            for (String exc : currTask.getExcludedWords()) {
                Label l = new Label(" " + exc + " ");
                l.setStyle("-fx-border-color: black");
                lblExcludeds.add(l);
                paneExcluded.getChildren().add(l);
            }
            showWords = true;
        }
        showWords = true;
        if (currTask instanceof RegexTask) {
            if (currTask != null && regexController != null) {
                String suggestedSolution = txtInputSolution.getText();
                for (Label inc : lblIncludeds) {
                    if (regexController.testWord(suggestedSolution, inc.getText().substring(1, inc.getText().length() - 1))) {
                        inc.setStyle("-fx-border-color: green");
                    } else {
                        inc.setStyle("-fx-border-color: red");
                    }
                }

                for (Label exc : lblExcludeds) {
                    if (regexController.testWord(suggestedSolution, exc.getText().substring(1, exc.getText().length() - 1))) {
                        exc.setStyle("-fx-border-color: red");
                    } else {
                        exc.setStyle("-fx-border-color: green");
                    }
                }

                boolean isSolved = regexController.test(suggestedSolution);
                if (isSolved) {
                    String newConsoleText = lblHints.getText() + "\nCorrect! Press 'Load Next Task' to continue.";
                    if (newConsoleText.startsWith("\n"))
                        newConsoleText = newConsoleText.substring(1, newConsoleText.length());
                    lblHints.setText(newConsoleText);
//                    Alert correctAlert = new Alert(Alert.AlertType.INFORMATION);
//                    correctAlert.setTitle("Correct!");
//                    correctAlert.setHeaderText(null);
//                    correctAlert.setContentText("Your entered solution is correct!");
//                    correctAlert.showAndWait();
                } else {
                    String newConsoleText = lblHints.getText() + "\nIncorrect. Red words aren't handled properly.";
                    if (newConsoleText.startsWith("\n"))
                        newConsoleText = newConsoleText.substring(1, newConsoleText.length());
                    lblHints.setText(newConsoleText);
                }
            }
        } else if (currTask instanceof EBNFTask) {
            String suggestedSolution = txtInputSolution.getText();
            EbnfController ebnfController = new EbnfController();
            ebnfController.setEbnfTask((EBNFTask) currTask);
            try {
                EbnfToCnfHandler ebnfToCnfHandler = new EbnfToCnfHandler();
                ebnfController.setCykAlgoritmusHandler(
                        new CykAlgoritmusHandler(
                                ebnfToCnfHandler.makeEbnfIntoCnf(
                                        LudwigEbnfParser.parseInput(suggestedSolution)
                                )
                        )
                );
                if(ebnfController.getCykAlgoritmusHandler().cnfGrammar.getEbnfRules().length==0){
                    Alert incorrectAlert = new Alert(Alert.AlertType.ERROR);
                    incorrectAlert.setTitle("Empty Grammar");
                    incorrectAlert.setHeaderText(null);
                    incorrectAlert.setContentText("Grammar doesnt create any words (its empty). Make sure the "+
                            "Start Symbol (First Rule) can resolve into Terminals.");
                    incorrectAlert.showAndWait();
                }
            } catch (ParseException p) {
                Alert incorrectAlert = new Alert(Alert.AlertType.ERROR);
                incorrectAlert.setTitle("Syntax Error");
                incorrectAlert.setHeaderText(null);
                incorrectAlert.setContentText("Could not parse the input:\n" + p.getMessage());
                incorrectAlert.showAndWait();
                return;
            }
            boolean isSolved = true;
            for (Label inc : lblIncludeds) {
                if (ebnfController.testWord(inc.getText().substring(1, inc.getText().length() - 1))) {
                    inc.setStyle("-fx-border-color: green");
                } else {
                    inc.setStyle("-fx-border-color: red");
                    isSolved = false;
                }
            }

            for (Label exc : lblExcludeds) {
                if (ebnfController.testWord(exc.getText().substring(1, exc.getText().length() - 1))) {
                    exc.setStyle("-fx-border-color: red");
                    isSolved = false;
                } else {
                    exc.setStyle("-fx-border-color: green");
                }
            }

            //boolean isSolved = ebnfController.test(suggestedSolution);
            if (isSolved) {
                String newConsoleText = lblHints.getText() + "\nCorrect! Press 'Load Next Task' to continue.";
                if (newConsoleText.startsWith("\n"))
                    newConsoleText = newConsoleText.substring(1, newConsoleText.length());
                lblHints.setText(newConsoleText);
//                Alert correctAlert = new Alert(Alert.AlertType.INFORMATION);
//                correctAlert.setTitle("Correct!");
//                correctAlert.setHeaderText(null);
//                correctAlert.setContentText("Your entered solution is correct!");
//                correctAlert.showAndWait();
            } else {
                String newConsoleText = lblHints.getText() + "\nIncorrect. Red words aren't handled properly.";
                if (newConsoleText.startsWith("\n"))
                    newConsoleText = newConsoleText.substring(1, newConsoleText.length());
                lblHints.setText(newConsoleText);
//                Alert incorrectAlert = new Alert(Alert.AlertType.ERROR);
////                incorrectAlert.setTitle("Incorrect");
////                incorrectAlert.setHeaderText(null);
////                incorrectAlert.setContentText("Your entered solution is incorrect.\nPress \"Next Hint\" for addditional help");
////                incorrectAlert.showAndWait
            }
        }
    }

    /**
     * This method is called when the "Load Next Task" Button is pressed
     *
     * @param e fired event
     */
    @FXML
    public void fireLoadNextTask(ActionEvent e) {
        File currDirectory = null;
        if (currTaskFile == null) {
            File f = new File("Default Tasks");
            if (f.exists() && f.isDirectory()) currDirectory = f;
        } else {
            currDirectory = currTaskFile.getParentFile();
        }

        boolean getNext = false;
        for (File fileInPackage : currDirectory.listFiles()) {
            if (fileInPackage.getAbsolutePath().endsWith(".xmlret")) {
                if (getNext || currTaskFile == null) {
                    try {
                        Task t = taskController.readTaskFromXML(fileInPackage.getAbsolutePath());
                        loadTaskIntoUI(t, fileInPackage);
                        return;
                    } catch (FileNotFoundException e1) {
                        Alert incorrectAlert = new Alert(Alert.AlertType.ERROR);
                        incorrectAlert.setTitle("File not found");
                        incorrectAlert.setHeaderText(null);
                        incorrectAlert.setContentText("File not found\n" + fileInPackage.getAbsolutePath());
                        incorrectAlert.showAndWait();
                    } catch (Exception e2) {
                        Alert incorrectAlert = new Alert(Alert.AlertType.ERROR);
                        incorrectAlert.setTitle("Could not open file");
                        incorrectAlert.setHeaderText(null);
                        incorrectAlert.setContentText("Could not open\n" + fileInPackage.getAbsolutePath());
                        incorrectAlert.showAndWait();
                    }
                }

                if (fileInPackage.getAbsolutePath().equals(currTaskFile.getAbsolutePath())) {
                    getNext = true;
                }
            }
        }
        Alert correctAlert = new Alert(Alert.AlertType.INFORMATION);
        correctAlert.setTitle("No more tasks!");
        correctAlert.setHeaderText(null);
        correctAlert.setContentText("No more tasks in\n" + currDirectory.getAbsolutePath());
        correctAlert.showAndWait();

    }

    /**
     * This method is called when the Create Task Button is pressed. It loads a seperate UI that is handled
     * by the GUIControllerCreate controller class.
     */
    @FXML
    public void fireCreateTask() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/createUI.fxml"));
            Stage creator = new Stage();
            creator.setTitle("RET Task Creator");
            creator.setScene(new Scene(root, 700, 600));

            creator.show();
        } catch (IOException e) {
        }
    }


    /**
     * This method is called to display the next hint. The next hint - if available - is shown
     * in the console in the bottom left.
     *
     * @param e fired event
     */
    @FXML
    public void fireShowNextHint(ActionEvent e) {
        if (currTask != null) {
            int maxHintIndex = currTask.getHints().size() - 1;
            if (currHintIndex <= maxHintIndex - 1) {
                currHintIndex++;
                String nextHint = currTask.getHints().get(currHintIndex);
                String newHintString = lblHints.getText();
                if (currHintIndex == 0) {
                    newHintString = "Hint #" + (currHintIndex + 1) + ": " + nextHint;
                } else {
                    newHintString = newHintString + "\nHint #" + (currHintIndex + 1) + ": " + nextHint;
                }
                lblHints.setText(newHintString);
            }
        }
    }

    /**
     * This method is called whenever a task is loaded into the UI
     *
     * @param task task to load
     */
    private void loadTaskIntoUI(Task task, File taskFile) {
        if (task != null && taskFile != null) {
            clearTaskFromUI();
            this.currTask = task;
            this.currTaskFile = taskFile;
            if (currTask instanceof RegexTask) {
                regexController.setTask((RegexTask) currTask);
            }
            lblTaskDescription.setText(task.getDescription());
        }
    }

    /**
     * This method shows all the creator's example solutions in the bottom right corner.
     *
     * @param e fired event
     */

    @FXML
    public void fireSolve(ActionEvent e) {
        if (currTask != null) {
            //String solution = currTask.getSolution();
            //txtInputSolution.setText(solution);
            ArrayList<String> currsol = currTask.getSolution();
            String test = "";

            for (String s : currsol) {
                test = test + s + "\n" + "###########################" + "\n";
            }
            txtSolutions.setText(test);

        }
    }

    private void clearTaskFromUI() {
        currHintIndex = -1;
        currTask = null;
        lblTaskDescription.setText("");
        lblIncludeds.removeAll(lblIncludeds);
        lblExcludeds.removeAll(lblExcludeds);
        lblHints.setText("");
        paneIncluded.getChildren().removeAll(paneIncluded.getChildren());
        paneExcluded.getChildren().removeAll(paneExcluded.getChildren());
        regexController.setTask(null);
        txtInputSolution.setText("");
        txtSolutions.setText("");
        showWords = false;
    }
}
