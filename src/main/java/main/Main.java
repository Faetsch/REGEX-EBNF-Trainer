package main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/MainUI.fxml"));
        primaryStage.setTitle("Regex/EBNF Trainer (RET)");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();

//        EbnfParser parser = new EbnfParser();
//        String ebnfprogram = "Meme:=(Iks,(De|Be))\n" +
//                             "Iks:=\"x\"\n" +
//                             "De:=\"d\"\n" +
//                             "Be:=\"b\"";
//        String ebnfprogram = "Meme:=(Iks,(De|\"lol\"))\n" +
//                             "Iks:=\"x\"\n" +
//                             "De:=\"d\"\n" +
//                             "Be:=\"b\"";
//        EbnfGrammar p = parser.parseTextToEbnfGrammar(ebnfprogram);



//        TaskSerializationController c = new TaskSerializationController();
//        RegexTask task = new RegexTask();
//        task.setDescription("Find a language that includes every word starting with x and following and ending with any amount of D");
//        ArrayList<String> included = new ArrayList<String>();
//        included.add("xD");
//        included.add("xDD");
//        included.add("xDDD");
//        task.setIncludedWords(included);
//
//        ArrayList<String> excluded = new ArrayList<String>();
//        excluded.add("x");
//        excluded.add("D");
//        excluded.add("DD");
//        task.setExcludedWords(excluded);
//        task.setSolution("xD+");
//
//        ArrayList<String> hints = new ArrayList<String>();
//        hints.add("hint number 1 akjdajdajsdadasdja");
//        hints.add("hint number 2 asdajdajsdasdasdasd");
//        hints.add("hint number 3 adsjsakjdaksdadasd");
//        hints.add("hint number 4 adsjsakjdaksdadasd");
//        hints.add("hint number 5 adsjsakjdaksdadasd");
//        hints.add("hint number 6 adsjsakjdaksdadasd");
//        hints.add("hint number 7 adsjsakjdaksdadasd");
//        task.setHints(hints);
//


//        RegexTask regTest = new RegexTask();
//        regTest.setDescription("aelgalk");
//        regTest.setSolution("a");
//        regTest.setIncludedWords(iw);
//        regTest.setHints(h);
//        regTest.setExcludedWords(ew);


//        TaskSerializationController ts = new TaskSerializationController();
//        ts.save("src/main/resources/saves/savedTask.xml", regTest);
//        tester.writeAufgabe("alohaheya", h, "false", iw, ew, "testingsaver", "Die Lösung ist 42");
//
//        LoadNsave testing = new LoadNsave();
//        testing.loadFileSaveInsave("src/save/testingsaver.xml", "copy");

    }

    public static void main(String[] args) {
        launch(args);
    }
}
