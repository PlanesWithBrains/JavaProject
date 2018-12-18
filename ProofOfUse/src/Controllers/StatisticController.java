package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.Program;
import sample.Statistic;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static sample.Program.GetInstanceOfChart;

public class StatisticController {

    private static boolean flagAdmin = false;
    private static String consoleLog = "";

    BarChart<String,Number> module_time;
    BarChart<String,Number> module_tu;
    BarChart<String,Number> module_addr;
    BarChart<String,Number> module_user;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private MenuButton mbtnMenu;

    @FXML
    private MenuItem btnImportFile;

    @FXML
    private MenuItem btnImportServer;

    @FXML
    private MenuItem btnInfo;

    @FXML
    private MenuItem btnDebug;

    @FXML
    private MenuItem btnAutotest;

    @FXML
    private Tab tabCountUsersModuls;

    @FXML
    private Tab tabCountUsersCountry;

    @FXML
    private Tab tabAvgTimeModuls;

    @FXML
    private Tab tabTimeModuls;

    @FXML
    private  TextArea txtLogArea;

    @FXML
    void initialize() {
        if (flagAdmin){
            btnAutotest.setVisible(false);
            btnDebug.setVisible(false);
        }
        //refreshStat();
        btnImportServer.setOnAction(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(Program.class.getResource("../FXML/load.fxml")); //загружаем fxml нового окна
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
            Scene scene = new Scene(root); //выставляем его размеры
            Stage stage = new Stage(); //хуйня чисто для scene builder
            stage.setTitle("Import file from server"); //название окна
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
            refreshStat();
        });
        btnImportFile.setOnAction(event -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));

            //директория по умолчанию
            try {
                File initialDirectory = new File(System.getProperty("user.home") + "\\Documents\\");
                fc.setInitialDirectory(initialDirectory);
            }
            catch (Exception exp){ //если MAC os
                System.out.println((char)27 + "[32m"+exp.getMessage());
                StatisticController.addConsoleLog(exp.getMessage());
            }

            File f = fc.showOpenDialog(null);
            if (f!= null){
                //ВОТ ЗДЕСЬ НУЖНА ФУНКЦИЯ ДЕССЕРИАЛИЗАТОРА
            }
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
        btnInfo.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information of developers");
            alert.setHeaderText("DEVELOPERS:");
            alert.setContentText("Alexander Umanski\t  readysloth@protonmail.com \nSolovev Dmitry\t  chrome266@gmail.com\nAnton Ablamski\t  ablamskiy98@gmail.com");
           /* Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(this.getClass().getResource("login.png").toString()));*/
            alert.showAndWait();
        });
    }

    void refreshStat(){
        module_time = GetInstanceOfChart("",
                "Hours",
                "Amount of time spent in the modules",
                Statistic.pairTime);
        module_tu =  GetInstanceOfChart("",
                "Hours",
                "The average time of usage per user",
                Statistic.pairTU);
        module_addr =  GetInstanceOfChart("",
                "Number of users",
                "Number of users in cities",
                Statistic.pairAdress);
        module_user =  GetInstanceOfChart("Module name",
                "Number of users",
                "Number of users in moduls",
                Statistic.pairUser);
        tabCountUsersModuls.setContent(module_user);
        tabCountUsersCountry.setContent(module_addr);
        tabAvgTimeModuls.setContent(module_tu);
        tabTimeModuls.setContent(module_time);
        txtLogArea.setText(consoleLog);
    }

    public static void setFlagUser(boolean pr) {flagAdmin = pr;}
    public static void addConsoleLog(String str) {
        consoleLog = str + consoleLog;
    }
}