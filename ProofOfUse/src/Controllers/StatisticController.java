package Controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Tab;
import sample.Statistic;

import java.net.URL;
import java.util.ResourceBundle;

import static sample.Program.GetInstanceOfChart;

public class StatisticController {

    BarChart<String,Number> module_time =  GetInstanceOfChart("",
            "Hours",
            "Amount of time spent in the modules",
            Statistic.pairTime);
    BarChart<String,Number> module_tu =  GetInstanceOfChart("",
            "Hours",
            "The average time of usage per user",
            Statistic.pairTU);
    BarChart<String,Number> module_addr =  GetInstanceOfChart("",
            "Number of users",
            "Number of users in cities",
            Statistic.pairAdress);
    BarChart<String,Number> module_user=  GetInstanceOfChart("Module name",
            "Number of users",
            "Number of users in moduls",
            Statistic.pairUser);
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Tab tabCountUsersModuls;

    @FXML
    private Tab tabCountUsersCountry;

    @FXML
    private Tab tabAvgTimeModuls;

    @FXML
    private Tab tabTimeModuls;

    @FXML
    void initialize() {
        tabCountUsersModuls.setContent(module_user);
        tabCountUsersCountry.setContent(module_addr);
        tabAvgTimeModuls.setContent(module_tu);
        tabTimeModuls.setContent(module_time);
    }
}