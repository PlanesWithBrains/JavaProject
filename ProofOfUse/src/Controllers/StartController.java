package Controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    void initialize() {
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
            btnChangeUser.getScene().getWindow().hide(); //скрыть старое окно
            StartController.flagNewUser = false; //флаг того, что форма открывается на ввод данных нового пользователя
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("../FXML/start.fxml")); //загружаем fxml нового окна
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }

            Scene scene = new Scene(root); //выставляем его размеры
            Stage stage = new Stage(); //хуйня чисто для scene builder
            stage.setTitle("Start new user"); //название окна
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
        Program.recieveJson();
        if (priv != User.Priveledge.wrong_pass && priv != User.Priveledge.wrong_login) window.hide();
        switch (priv) {
            case user: {
                MenuController.setFlagUser(true);
                loadStatistic("MENU USER", clases);
                break;
            }
            case root: {
                MenuController.setFlagUser(false);
                loadStatistic("MENU ADMIN", clases);
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

        /*получаем в строку свойства для введенного ника*/
        String props = property.getProperty(user.getName());

        /*если чтение свойства вернуло null, то свойство не прочиталось*/
        if(props == null) {
            Program.log.log(Level.INFO, "Wrong username or encoding error in config.ini");
            return User.Priveledge.wrong_login;
        }

		/*в массив userData на [0] положится пароль из конфига
		  а на [1] его привелегия в виде строки*/
        String[] userData = props.split(",");

        /*формируем из строки привелегии enum и кладем его в user`а*/
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
            root = FXMLLoader.load(clases.getResource("../FXML/statistic.fxml")); //загружаем fxml нового окна
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        Scene scene1 = new Scene(root); //выставляем его размеры
        Stage stage1 = new Stage(); //хуйня чисто для scene builder
        stage1.setTitle(title); //название окна
        stage1.setScene(scene1);
        stage1.setResizable(false);
        stage1.show();
    }
}
