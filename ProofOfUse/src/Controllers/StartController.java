package Controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import sample.LoggingMachine;
import sample.Program;
import sample.User;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class StartController {
    public static boolean flagNewUser = true;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label lblUserName;

    @FXML
    private PasswordField fieldPass;

    @FXML
    private TextField fieldUser;

    @FXML
    private Button btnChangeUser;

    @FXML
    private Button btnLogin;

    @FXML
    private Text lblName;


    @FXML
    void initialize() {
        Font font = Font.loadFont(getClass().getResourceAsStream("/ImagesAndFonts/Label.ttf"), 31);
        lblName.setFont(font);
        if (flagNewUser){ //save user
            btnChangeUser.setVisible(true);
            lblUserName.setVisible(true);
            fieldUser.setVisible(false);
            Program.loggingUser = new User(Program.property.getProperty("current_user"), "Default");
            lblUserName.setText(Program.loggingUser.getName());
        }
        else{ //new user
            btnChangeUser.setVisible(false);
            lblUserName.setVisible(false);
            fieldUser.setVisible(true);
            fieldUser.setPromptText("Login");
        }


        btnLogin.setOnAction(event -> {
            if (flagNewUser) { //save user
                if (fieldPass.getText().length() != 0)
                    Program.loggingUser.setPassword(fieldPass.getText());
            }
            else //new user
                if (fieldUser.getText().length() != 0 && fieldPass.getText().length() != 0)
                    Program.loggingUser = new User(fieldUser.getText(), fieldPass.getText());
            Program.log = login(btnLogin.getScene().getWindow(), getClass());
        });

        btnChangeUser.setOnAction(event -> {
            btnChangeUser.getScene().getWindow().hide(); //ñêðûòü ñòàðîå îêíî
            StartController.flagNewUser = false; //ôëàã òîãî, ÷òî ôîðìà îòêðûâàåòñÿ íà ââîä äàííûõ íîâîãî ïîëüçîâàòåëÿ
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("/FXML/start.fxml")); //çàãðóæàåì fxml íîâîãî îêíà
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }

            Scene scene = new Scene(root); //âûñòàâëÿåì åãî ðàçìåðû
            Stage stage = new Stage(); //õóéíÿ ÷èñòî äëÿ scene builder
            stage.setTitle("Start new user"); //íàçâàíèå îêíà
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    Program.SaveConfig(Program.property, Program.loggingUser);
                }
            });
            stage.show();
        });
    }

    public static LoggingMachine login(Window window, Class clases) {
        User.Priveledge priv = Verify(Program.loggingUser, Program.property);
        //Program.recieveJson();
        if (priv != User.Priveledge.wrong_pass && priv != User.Priveledge.wrong_login) window.hide();
        switch (priv) {
            case user: {
                StatisticController.setFlagUser(true);
                loadStatistic("Proof of Use", clases);
                break;
            }
            case root: {
                StatisticController.setFlagUser(false);
                loadStatistic("Proof of Use (Admin)", clases);
                break;
            }
            case wrong_pass: {
                alertLogin("User entered wrong password! Access Denied!");
            }
                break;
            case wrong_login: {
                alertLogin("User entered wrong login! Access Denied!");
                break;
            }
        }
        return Program.log;
    }
    //public static void setLoggingUser(User us) { loggingUser = us;}
    public static User.Priveledge Verify(User user, Properties property) {
        Program.log.info("Verifying entered data");

        /*ïîëó÷àåì â ñòðîêó ñâîéñòâà äëÿ ââåäåííîãî íèêà*/
        String props = property.getProperty(user.getName());

        /*åñëè ÷òåíèå ñâîéñòâà âåðíóëî null, òî ñâîéñòâî íå ïðî÷èòàëîñü*/
        if(props == null) {
            Program.log.log(Level.INFO, "Wrong username or encoding error in config.ini");
            return User.Priveledge.wrong_login;
        }

		/*â ìàññèâ userData íà [0] ïîëîæèòñÿ ïàðîëü èç êîíôèãà
		  à íà [1] åãî ïðèâåëåãèÿ â âèäå ñòðîêè*/
        String[] userData = props.split(",");

        /*ôîðìèðóåì èç ñòðîêè ïðèâåëåãèè enum è êëàäåì åãî â user`à*/
        user.setPriveledge(User.Priveledge.valueOf(userData[1]));
        if(userData[0].equals(user.getPassword()))
            return user.getPriveledge();
        else
            return User.Priveledge.wrong_pass;


    }
    static void alertLogin(String messege){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Login to system");
        alert.setHeaderText(null);
        alert.setContentText(messege);
        alert.showAndWait();
        Program.log.info("User entered wrong login or password");
        System.out.println("======++++++Access Denied++++++=====");
    }
    static void loadStatistic(String title, Class clases){
        Parent root = null;
        try {
            root = FXMLLoader.load(clases.getResource("/FXML/statistic.fxml")); //çàãðóæàåì fxml íîâîãî îêíà
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        Scene scene = new Scene(root); //âûñòàâëÿåì åãî ðàçìåðû
        Stage stage = new Stage(); //õóéíÿ ÷èñòî äëÿ scene builder
        stage.setTitle(title); //íàçâàíèå îêíà
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(new Image(clases.getResourceAsStream("/ImagesAndFonts/LOGOJAVA.png")));
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Program.SaveConfig(Program.property, Program.loggingUser);
                StatisticController.saveHTML();
            }
        });
        stage.show();
    }
}
