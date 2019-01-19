package Controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.ClientData;
import sample.Program;
import sample.Statistic;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static sample.Program.GetInstanceOfChart;

public class StatisticController  {

    private static boolean flagAdmin = false;
    private static String consoleLog = "";
    private static String str = "";
    private static int flag = 1;

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
    private ScrollPane scroll;

    @FXML
    private AnchorPane tabCountUsersModuls;

    @FXML
    private AnchorPane tabCountUsersCountry;

    @FXML
    private AnchorPane tabAvgTimeModuls;

    @FXML
    private AnchorPane tabTimeModuls;

    @FXML
    private AnchorPane tabClientsName;

    @FXML
    private AnchorPane tabClientsMap;

    @FXML
    private ImageView imageClient;

    @FXML
    private AnchorPane tabMap;

    @FXML
    private Tab slcMap;
    @FXML
    private Tab slcClients;

    @FXML
    private TextArea txtLogArea;

    @FXML
    private MenuItem btnImportFile;

    @FXML
    private MenuItem btnImportServer;

    @FXML
    private MenuItem btnSort;

    @FXML
    private MenuItem btnClose;

    @FXML
    private MenuItem btnInfo;

    @FXML
    private MenuItem btnAutotest;
    @FXML
    private Button btnOpenMap;


    @FXML
    void initialize() {
        tabPane.setDisable(true);
        if (flagAdmin){
            btnAutotest.setVisible(false);
        }
        btnSort.setDisable(true);
        btnClose.setDisable(true);
        //refreshStat();
        btnClose.setOnAction(event -> {
            tabCountUsersModuls.getChildren().remove(0);
            tabCountUsersCountry.getChildren().remove(0);
            tabAvgTimeModuls.getChildren().remove(0);
            tabTimeModuls.getChildren().remove(0);
            btnImportServer.setDisable(false);
            btnImportFile.setDisable(false);
            btnAutotest.setDisable(false);
            btnClose.setDisable(true);
            tabPane.setDisable(true);
            btnSort.setDisable(true);
            clearPair();
            flag = 1;
        });
        btnSort.setOnAction(event -> {
            ObservableList<Tab> tabs = tabPane.getTabs();
            int activeTab = 1;
            for(int i = 0; i < tabs.size(); i++){
                if(tabs.get(i).isSelected()) activeTab = i + 1;
            }
            RangeController.setNumber(activeTab);
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("/FXML/range.fxml")); //загружаем fxml нового окна
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                StatisticController.addConsoleLog(e.getMessage() + "\n");
            }
            Scene scene = new Scene(root); //выставляем его размеры
            Stage stage = new Stage(); //хуйня чисто для scene builder
            stage.setTitle("Filter for range"); //название окна
            stage.setScene(scene);
            stage.setResizable(false);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/ImagesAndFonts/LOGOJAVA.png")));
            stage.showAndWait();
            refreshStat(flag, true);
            btnImportFile.setDisable(true);
            btnImportServer.setDisable(true);
            btnAutotest.setDisable(true);
        });
        btnImportServer.setOnAction(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("/FXML/load.fxml")); //загружаем fxml нового окна
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
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/ImagesAndFonts/LOGOJAVA.png")));
            stage.showAndWait();
            if(refreshStat(flag, true))
            {
                btnSort.setDisable(false);
                btnClose.setDisable(false);
                tabPane.setDisable(false);
                flag++;
            }
            else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("Error importing data!");
                alert.setContentText("Error 322 - error communicating with the server!");
                alert.showAndWait();
            }
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

                if(temp.size() != 0)
                {
                    tabPane.setDisable(false);
                    refreshStat(flag, false);
                    btnSort.setDisable(false);
                    btnClose.setDisable(false);
                    flag++;
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText("Error importing data!");
                    alert.setContentText("Error 228 - error loading file!");
                    alert.showAndWait();
                }
            }
        });
        btnAutotest.setOnAction(event -> {
            File f = new File("./test.json");
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
                if(refreshStat(flag, false))
                {
                    btnSort.setDisable(false);
                    btnClose.setDisable(false);
                    tabPane.setDisable(false);
                    flag++;
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText("Error importing data!");
                    alert.setContentText("Error 148 error in the AutoTests!");
                    alert.showAndWait();
                }
            }
        });
        btnInfo.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information of developers");
            alert.setHeaderText("DEVELOPERS:");
            alert.setContentText("Alex Umanskiy\treadysloth@protonmail.com\nSolovev Dmitry\tchrome266@gmail.com\nAnton Ablamskiy\tablamskiy98@gmail.com");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Program.class.getResourceAsStream("/ImagesAndFonts/LOGOJAVA.png")));
            //alert.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
            alert.showAndWait();


        });



    }
    void clearPair(){
        Statistic.pairTime = null;
        Statistic.pairUser = null;
        Statistic.pairAdress = null;
        Statistic.pairTU = null;
    }

    boolean refreshStat(int flag, boolean flagWrite){
        module_time = GetInstanceOfChart("",
                "Hours",
                "Amount of time spent in the modules",
                Statistic.pairTime, 1);
        module_tu =  GetInstanceOfChart("",
                "Hours",
                "The average time of usage per user",
                Statistic.pairTU, 2);
        module_addr =  GetInstanceOfChart("",
                "Number of users",
                "Number of users in cities",
                Statistic.pairAdress, 3);
        module_user =  GetInstanceOfChart("Module name",
                "Number of users",
                "Number of users in modules",
                Statistic.pairUser, 4);

        int prefWidth = 949, prefHeight = 488;
//HERE SIZE OF CATEGORIES

        // if (count1 < 10)
        module_time.setCategoryGap(400.0 / count1 );
        // if (count4 < 10)
        module_user.setCategoryGap(400.0 / count4);
        //  if (count2 < 10)
        module_tu.setCategoryGap(400.0 / count2);
        //   if (count3 < 10)
        module_addr.setCategoryGap(400.0 / count3);

        tabClientsName.setPrefSize(448,478);

        module_time.setPrefSize(prefWidth + (count1 > 8 ? 20*(count1-8) : 0),prefHeight);
        tabTimeModuls.setPrefSize(prefWidth + (count1 > 8 ? 20*(count1-8) : 0),prefHeight);

        module_user.setPrefSize(prefWidth + (count4 > 8 ? 20*(count4-8) : 0),prefHeight);
        tabCountUsersModuls.setPrefSize(prefWidth + (count4 > 8 ? 20*(count4-8) : 0),prefHeight);

        module_tu.setPrefSize(prefWidth + (count2 > 8 ? 20*(count2-8) : 0),prefHeight);
        tabAvgTimeModuls.setPrefSize(prefWidth + (count2 > 8 ? 20*(count2-8) : 0),prefHeight);

        module_addr.setPrefSize(prefWidth + (count3 > 8 ? 20*(count3-8) : 0),prefHeight);
        tabCountUsersCountry.setPrefSize(prefWidth + (count3 > 8 ? 20*(count3-8) : 0),prefHeight);
        // tabMap.setPrefSize(prefWidth,prefWidth);

        if (flag != 1) {
            tabCountUsersModuls.getChildren().remove(0);
            tabCountUsersCountry.getChildren().remove(0);
            tabAvgTimeModuls.getChildren().remove(0);
            tabTimeModuls.getChildren().remove(0);
            tabClientsName.getChildren().remove(0);
            //  tabClientsName.getChildren().remove(1);
            //  tabMap.getChildren().remove(0);

        }
        tabCountUsersModuls.getChildren().add(module_user);
        tabCountUsersCountry.getChildren().add(module_addr);
        tabAvgTimeModuls.getChildren().add(module_tu);
        tabTimeModuls.getChildren().add(module_time);

        //Images
        ArrayList<ClientData> clients = Program.getSortClients();
        if (clients.size() == 0 && flagWrite) return false;
        FlowPane pane = new FlowPane(Orientation.VERTICAL);
        pane.setPrefSize(448, 478);
        for (int i = 0; i < clients.size(); i++) {
            ClientData temp = clients.get(i);
            Hyperlink button = new Hyperlink(String.valueOf(clients.get(i).getUniqKey()));
            button.setOnAction(e -> {
                imageClient.setImage(temp.GetMapInstance()); //not worked
            });
            pane.getChildren().add(button);
            double lat = temp.getLatitude();
            double lon = temp.getLongtitude();
            if(temp.getLatitude() != 0 && temp.getLongtitude() != 0)
                try {
                    if (i == 0)
                        str = addMarker(lat, lon, String.valueOf(temp.getUniqKey()));
                    else
                        addMarker(lat, lon, String.valueOf(temp.getUniqKey()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
        //pane.getChildren().add(new Label("\n\nP.S. не все клиенты могут иметь распознанный адрес\nиз-за ограничений работы GeoIP\nP.s.s. при загрузке из файла/автотестах актуальность по GeoIP\n не проверяется (она уже выполнена)"));
        tabClientsName.getChildren().add(pane);
        if (!flagWrite){
            slcClients.setOnSelectionChanged(event -> {
                if (slcClients.isSelected()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText("No customer addresses found!");
                    alert.setContentText("To determine customer addresses, use the import via the server! When importing data via file/AutoTest, GeoIP localization is not performed!");
                    alert.showAndWait();
                }
            });
        }
        else
            slcClients.setOnSelectionChanged(event -> {
                if(slcClients.isSelected()){

                }
            });
        //Maps
        //UNCOMMIT when will do all , to not to pay money Google
        //GoogleMap map = new GoogleMap();
        //map.setWidth(prefWidth);
        //map.setHeight(prefHeight);
        //tabMap.getChildren().add(map);
        btnOpenMap.setOnAction(event -> {
            File htmlFile = new File("map.html");
            try {
                Desktop.getDesktop().browse(htmlFile.toURI());
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        });

        txtLogArea.setText(consoleLog);
        return true;
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
    String addMarker(double lan, double lon, String str) throws IOException {
        String PATH = "./map.html";
        String contents = new String(Files.readAllBytes(Paths.get(PATH)));
        String res = contents.substring(0,519) + "[\r\n";
        res += "        ['"+str+"', "+lan+","+lon+"],\n";
        res += "\t" + contents.substring(521, contents.length());
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(PATH, "UTF-8");
            writer.print(res);
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        finally {
            writer.close();
        }
        return contents;
    }
    static public void saveHTML() {
        if (str != ""){
            String PATH = "./map.html";
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(PATH, "UTF-8");
                writer.print(str);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            } finally {
                writer.close();
            }
        }
    }
}