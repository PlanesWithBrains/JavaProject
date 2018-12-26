package Controllers;

import Map.GoogleMap;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
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

    BarChart<String, Number> module_time;
    BarChart<String, Number> module_tu;
    BarChart<String, Number> module_addr;
    BarChart<String, Number> module_user;

    static private int count1 = 0;
    static private int count2 = 0;
    static private int count3 = 0;
    static private int count4 = 0;

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
    private MenuItem btnDebug;

    @FXML
    private MenuItem btnAutotest;


    @FXML
    void initialize() {
        if (flagAdmin) {
            btnAutotest.setVisible(false);
            btnDebug.setVisible(false);
        }
        btnSort.setDisable(true);
        btnClose.setDisable(true);
        //refreshStat();
        btnClose.setOnAction(event -> {
            tabCountUsersModuls.getChildren().remove(0);
            tabCountUsersCountry.getChildren().remove(0);
            tabAvgTimeModuls.getChildren().remove(0);
            tabTimeModuls.getChildren().remove(0);
            btnClose.setDisable(true);
        });
        btnSort.setOnAction(event -> {
            ObservableList<Tab> tabs = tabPane.getTabs();
            int activeTab = 1;
            for (int i = 0; i < tabs.size(); i++) {
                if (tabs.get(i).isSelected()) activeTab = i + 1;
            }
            RangeController.setNumber(activeTab);
            Parent root = null;
            try {
                root = FXMLLoader.load(Program.class.getResource("../FXML/range.fxml")); //загружаем fxml нового окна
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
            stage.getIcons().add(new Image(Program.class.getResourceAsStream("../ImagesAndFonts/LOGOJAVA.png")));
            stage.showAndWait();
            refreshStat(2);

        });
        btnImportServer.setOnAction(event -> {
            btnClose.setDisable(false);
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
            refreshStat(1);
        });
        btnImportFile.setOnAction(event -> {
            btnClose.setDisable(false);
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files", "*.txt"));
            //директория по умолчанию
            try {
                File initialDirectory = new File(System.getProperty("user.home") + "\\Documents\\");
                fc.setInitialDirectory(initialDirectory);
            } catch (Exception exp) { //если MAC os
                System.out.println((char) 27 + "[32m" + exp.getMessage());
                StatisticController.addConsoleLog(exp.getMessage() + "\n");
            }

            File f = fc.showOpenDialog(null);
            if (f != null) {
                StatisticController.addConsoleLog("#load file: " + f.getAbsolutePath() + "\n");
                String contents = "";
                try {
                    contents = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ArrayList<ClientData> temp = Program.LoadFile(contents);
                for (ClientData clients : temp) {
                    StatisticController.addConsoleLog(clients.toString() + "\n");
                }
                Statistic stat = new Statistic(temp);
                refreshStat(1);
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

    void refreshStat(int flag) {
        module_time = GetInstanceOfChart("",
                "Hours",
                "Amount of time spent in the modules",
                Statistic.pairTime, count1);
        module_tu = GetInstanceOfChart("",
                "Hours",
                "The average time of usage per user",
                Statistic.pairTU, count2);
        module_addr = GetInstanceOfChart("",
                "Number of users",
                "Number of users in cities",
                Statistic.pairAdress, count3);
        module_user = GetInstanceOfChart("Module name",
                "Number of users",
                "Number of users in moduls",
                Statistic.pairUser, count4);

        int prefWidth = 1920, prefHeight = 868;

        CategoryAxis axis = (CategoryAxis) module_time.getXAxis();
        if (axis.getCategories().size() < 8) {
            module_time.setCategoryGap(100.0);
        }
        axis = (CategoryAxis) module_user.getXAxis();
        if (axis.getCategories().size() < 8) {
            module_user.setCategoryGap(100.0);
        }
        axis = (CategoryAxis) module_tu.getXAxis();
        if (axis.getCategories().size() < 8) {
            module_tu.setCategoryGap(100.0);
        }
        axis = (CategoryAxis) module_addr.getXAxis();
        if (axis.getCategories().size() < 8) {
            module_addr.setCategoryGap(100.0);
        }

        tabClientsName.setPrefSize(632, 838);
        module_time.setPrefSize(prefWidth + (count1 > 15 ? 20 * (count1 - 15) : 0), 868);
        module_user.setPrefSize(1920 + (count2 > 15 ? 20 * (count1 - 15) : 0), 868);
        module_tu.setPrefSize(1920 + (count3 > 15 ? 20 * (count1 - 15) : 0), 868);
        module_addr.setPrefSize(1920 + (count4 > 15 ? 20 * (count1 - 15) : 0), 868);
        tabMap.setPrefSize(1920, 868);

        if (flag != 1) {
            tabCountUsersModuls.getChildren().remove(0);
            tabCountUsersCountry.getChildren().remove(0);
            tabAvgTimeModuls.getChildren().remove(0);
            tabTimeModuls.getChildren().remove(0);
            tabClientsName.getChildren().remove(0);
            tabMap.getChildren().remove(0);
        }
        tabCountUsersModuls.getChildren().add(module_user);
        tabCountUsersCountry.getChildren().add(module_addr);
        tabAvgTimeModuls.getChildren().add(module_tu);
        tabTimeModuls.getChildren().add(module_time);




        //Images
        ArrayList<ClientData> clients = Statistic.getSortClients();
        FlowPane pane = new FlowPane(Orientation.VERTICAL);
        pane.setPrefSize(632, 838);
        for (int i = 0; i < clients.size(); i++) {
            ClientData temp = clients.get(i);
            Hyperlink button = new Hyperlink(String.valueOf(clients.get(i).getUniqKey()));
            button.setOnAction(e -> {
                imageClient.setImage(temp.GetMapInstance()); //not worked
            });
            pane.getChildren().add(button);
            double lat = temp.getLatitude(); //not work
            double lon = temp.getLongtitude(); //not word
            //UNCOMMIT THIS, after restore getLatitude/longtitude
            /*if(temp.getLatitude() != 0 && temp.getLongtitude() != 0) //UNCOMMIT THIS, after restore getLatitude/longtitude
                try {
                    if (i == 0)
                        str = addMarker(lat, lon, String.valueOf(temp.getUniqKey()));
                    else
                        addMarker(lat, lon, String.valueOf(temp.getUniqKey()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */
        }
        tabClientsName.getChildren().add(pane);

        //Maps
        //UNCOMMIT when will do all , to not to pay money Google
        GoogleMap map = new GoogleMap();
        map.setWidth(1920);
        map.setHeight(868);
        tabMap.getChildren().add(map);

        txtLogArea.setText(consoleLog);
        btnSort.setDisable(false);
    }

    public static void setFlagUser(boolean pr) {
        flagAdmin = pr;
    }

    public static void addConsoleLog(String str) {
        consoleLog = str + consoleLog;
    }

    public static void setCount(int number, int count) {
        switch (number) {
            case 1: {
                count1 = count;
                break;
            }
            case 2: {
                count2 = count;
                break;
            }
            case 3: {
                count3 = count;
                break;
            }
            case 4: {
                count4 = count;
            }
        }
    }

    String addMarker(double lan, double lon, String str) throws IOException {
        String PATH = StatisticController.class.getResource("../Map/map.html").toExternalForm();
        PATH = PATH.substring(6, PATH.length());
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
                String PATH = StatisticController.class.getResource("../Map/map.html").toExternalForm();
                PATH = PATH.substring(6, PATH.length());
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
