package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import sample.Program;

import java.net.URL;
import java.util.ResourceBundle;

public class LoadController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView imgLoad;

    @FXML
    private TextField fldIP;

    @FXML
    private TextField fldPort;

    @FXML
    private Button btnLoad;

    @FXML
    void initialize() {
       // imgLoad.setVisible(false); // не работает
        imgLoad.setManaged(false);// не работает

        btnLoad.setOnAction(event -> {
            imgLoad.setVisible(true); // не работает
            imgLoad.setManaged(true);// не работает

            Program.recieveJson(fldIP.getText(), Integer.valueOf(fldPort.getText()));
            this.imgLoad.getScene().getWindow().hide();
        });
    }
}