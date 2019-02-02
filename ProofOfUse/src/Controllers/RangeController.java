package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import sample.Statistic;

import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;

public class RangeController {
    static  private  int number = 1;
    static  private int bot = 0;
    static private int top;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField fldTOP;

    @FXML
    private TextField fldBot;

    @FXML
    private Button btnRange;

    @FXML
    private CheckBox chkBelow;

    @FXML
    private CheckBox chkOver;

    @FXML
    void initialize() {
       chkBelow.setOnAction(event -> {
           if (chkBelow.isSelected())
            fldBot.setDisable(false);
           else
               fldBot.setDisable(true);
       });
       chkOver.setOnAction(event -> {
           if (chkOver.isSelected())
               fldTOP.setDisable(false);
           else
               fldTOP.setDisable(true);
       });

        btnRange.setOnAction(event -> {
            final Object topBorder, botBorder, topBorderDur, botBorderDur;
            Double doubleTBorder, doubleBBorder;
            if (chkOver.isSelected()) {
                doubleTBorder = Double.valueOf(fldTOP.getText());
                topBorder = doubleTBorder.intValue();
                topBorderDur = Duration.ofHours((int) topBorder);
            }
            else {
                topBorder = null;
                topBorderDur = null;
            }
            if (chkBelow.isSelected()) {
                doubleBBorder = Double.valueOf(fldBot.getText());
                botBorder = doubleBBorder.intValue();
                botBorderDur = Duration.ofHours((int) botBorder);
            }
            else {
                botBorder = 0;
                botBorderDur = Duration.ofHours(0);
            }

                switch (number) {
                    case 1: {
                        StatisticController.chartNameContainer.push("pairUser");
                        Statistic.RangeSelection(botBorder, topBorder, Statistic.pairUser);
                        break;
                    }
                    case 2: {
                        StatisticController.chartNameContainer.push("pairTU");
                        Statistic.RangeSelection(botBorder, topBorder, Statistic.pairTU);
                        break;
                    }
                    case 3: {
                        StatisticController.chartNameContainer.push("pairAddress");
                        Statistic.RangeSelection(botBorderDur, topBorderDur, Statistic.pairAdress);
                        break;
                    }
                    case 4: {
                        StatisticController.chartNameContainer.push("pairTime");
                        Statistic.RangeSelection(botBorderDur, topBorderDur, Statistic.pairTime);
                        break;
                    }
                }
                btnRange.getScene().getWindow().hide();
            });


        }

    static public void setNumber(int numb) { number = numb;}
}
