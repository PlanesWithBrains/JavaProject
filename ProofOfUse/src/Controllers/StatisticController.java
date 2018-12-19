package Controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.ClientData;
import sample.Program;
import sample.Statistic;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static sample.Program.GetInstanceOfChart;

public class StatisticController {

    private static boolean flagAdmin = false;
    private static String consoleLog = "";

    BarChart<String,Number> module_time;
    BarChart<String,Number> module_tu;
    BarChart<String,Number> module_addr;
    BarChart<String,Number> module_user;

    static private  int count1 = 0;
    static private  int count2 = 0;
    static private  int count3 = 0;
    static private  int count4 = 0;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TabPane tabPane;

    @FXML
    private AnchorPane tabCountUsersModuls;

    @FXML
    private AnchorPane tabCountUsersCountry;

    @FXML
    private AnchorPane tabAvgTimeModuls;

    @FXML
    private AnchorPane tabTimeModuls;

    @FXML
    private TextArea txtLogArea;

    @FXML
    private MenuItem btnImportFile;

    @FXML
    private MenuItem btnImportServer;

    @FXML
    private MenuItem btnInfo;

    @FXML
    private MenuItem btnSort;

    @FXML
    private MenuItem btnDebug;

    @FXML
    private MenuItem btnAutotest;

    @FXML
    void initialize() {
        if (flagAdmin){
            btnAutotest.setVisible(false);
            btnDebug.setVisible(false);
        }
        btnSort.setDisable(true);
        //refreshStat();
        btnSort.setOnAction(event -> {
            ObservableList<Tab> tabs = tabPane.getTabs();
            int activeTab = 1;
            for(int i = 0; i < tabs.size(); i++){
                if(tabs.get(i).isSelected()) activeTab = i + 1;
            }
            Statistic.RangeSelection(1,2,Statistic.pairUser);

        });
        btnImportServer.setOnAction(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(Program.class.getResource("../FXML/load.fxml")); //загружаем fxml нового окна
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                StatisticController.addConsoleLog(e.getMessage() + "\n");
            }
            Scene scene = new Scene(root); //выставляем его размеры
            Stage stage = new Stage(); //хуйня чисто для scene builder
            stage.setTitle("Import file from server"); //название окна
            stage.setScene(scene);
            stage.setResizable(false);
            stage.getIcons().add(new Image(Program.class.getResourceAsStream("../ImagesAndFonts/LOGOJAVA.png")));
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
                StatisticController.addConsoleLog(exp.getMessage() + "\n");
            }

            File f = fc.showOpenDialog(null);
            if (f!= null){
                StatisticController.addConsoleLog("#load file: " + f.getAbsolutePath() + "\n");
                String contents = "";
                try {
                    contents = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ArrayList<ClientData> temp = Program.LoadFile(contents);
                for(ClientData clients : temp){
                    StatisticController.addConsoleLog(clients.toString() + "\n");
                }
                Statistic stat = new Statistic(temp);
                refreshStat();
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
            alert.setContentText("Alex Umanskiy\treadysloth@protonmail.com\nSolovev Dmitry\tchrome266@gmail.com\nAnton Ablamskiy\tablamskiy98@gmail.com");
           Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Program.class.getResourceAsStream("../ImagesAndFonts/LOGOJAVA.png")));
            alert.showAndWait();
        });
    }
    static void refreshLog(){
    }
    void refreshStat(){
        module_time = GetInstanceOfChart("",
                "Hours",
                "Amount of time spent in the modules",
                Statistic.pairTime, count1);
        module_tu =  GetInstanceOfChart("",
                "Hours",
                "The average time of usage per user",
                Statistic.pairTU, count2);
        module_addr =  GetInstanceOfChart("",
                "Number of users",
                "Number of users in cities",
                Statistic.pairAdress, count3);
        module_user =  GetInstanceOfChart("Module name",
                "Number of users",
                "Number of users in moduls",
                Statistic.pairUser, count4);
        ///module_time.set
       // Scene scene = new Scene(module_time, 800, 600);
       //module_time.
        int prefWidth = 1920, prefHeight = 868;
        module_time.setPrefSize(prefWidth + (count1 > 15 ? 20*(count1-15) : 0),868);
        module_user.setPrefSize(1920 + (count2 > 15 ? 20*(count1-15) : 0),868);
        module_tu.setPrefSize(1920 + (count3 > 15 ? 20*(count1-15) : 0),868);
        module_addr.setPrefSize(1920 + (count4 > 15 ? 20*(count1-15) : 0),868);

        tabCountUsersModuls.getChildren().add(module_user);
        tabCountUsersCountry.getChildren().add(module_addr);
        tabAvgTimeModuls.getChildren().add(module_tu);
        tabTimeModuls.getChildren().add(module_time);

        txtLogArea.setText(consoleLog);
        btnSort.setDisable(false);
    }

    public static void setFlagUser(boolean pr) {flagAdmin = pr;}
    public static void addConsoleLog(String str) {
        consoleLog = str + consoleLog;
    }
    public static void setCount(int number, int count) {
            switch (number){
                case 1: {
                    count1 = count;
                    break;
                }
                case 2:{
                    count2 = count;
                    break;
                }
                case 3:{
                    count3 = count;
                    break;
                }
                case 4: {
                    count4 = count;
                }
            }
        }
}