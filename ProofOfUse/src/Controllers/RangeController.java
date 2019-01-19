package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import sample.Statistic;

import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;

public class RangeController {
    static  private  int number = 1;

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
    void initialize() {


        btnRange.setOnAction(event -> {
            final Object topBorder, botBorder, topBorderDur, botBorderDur;
            Double doubleTBorder, doubleBBorder;
            doubleTBorder = Double.valueOf(fldTOP.getText());
            doubleBBorder = Double.valueOf(fldBot.getText());

            topBorder = doubleTBorder.intValue();
            botBorder = doubleBBorder.intValue();
            topBorderDur = Duration.ofHours((int) topBorder);
            botBorderDur = Duration.ofHours((int) botBorder);
                switch (number) {
                    case 1: {
                        Statistic.RangeSelection(botBorder, topBorder, Statistic.pairUser);
                        break;
                    }
                    case 2: {
                        Statistic.RangeSelection(botBorder, topBorder, Statistic.pairTU);
                        break;
                    }
                    case 3: {
                        Statistic.RangeSelection(botBorderDur, topBorderDur, Statistic.pairAdress);
                        break;
                    }
                    case 4: {
                        Statistic.RangeSelection(botBorderDur, topBorderDur, Statistic.pairTime);
                        break;
                    }
                }
                btnRange.getScene().getWindow().hide();
            });


        }

    static public void setNumber(int numb) { number = numb;}
}
