package Controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import sample.Program;


public class MenuController {
    private static boolean flagAdmin = false;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnStatistic;

    @FXML
    private Button btnJson;

    @FXML
    private Button btnDebug;

    @FXML
    private Button btnAutotest;

    @FXML
    void initialize() {
        if (flagAdmin){
            btnAutotest.setVisible(false);
            btnDebug.setVisible(false);
        }
        btnJson.setOnAction(event -> {
            Program.recieveJson();
        });
        btnStatistic.setOnAction(event -> {
            Program.watchStatisticInfo();
        });
        btnDebug.setOnAction(event -> {
            //case 3
            Program.property.setProperty("debug", Program.property.getProperty("debug").equals("true") ? "false" : "true");
            Program.property.setProperty("log", Program.property.getProperty("log").equals("true") ? "false" : "true");
            Program.log.revertChanges();
        });
        btnAutotest.setOnAction(event -> {
            //case 4
            Program.property.setProperty("autotest", Program.property.getProperty("autotest").equals("true") ? "false" : "true");
        });
    }
    public static void setFlagUser(boolean pr) {flagAdmin = pr;}
}

