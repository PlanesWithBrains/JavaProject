package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import sample.Program;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


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
        btnJson.setVisible(false);


        btnJson.setOnAction(event -> {
            //Program.recieveJson();
        });
        btnStatistic.setOnAction(event -> {
            //Program.watchStatisticInfo();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("../FXML/statistic.fxml")); //загружаем fxml нового окна
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }

            Scene scene = new Scene(root); //выставляем его размеры
            Stage stage = new Stage(); //хуйня чисто для scene builder
            stage.setTitle("Statistic"); //название окна
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
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

