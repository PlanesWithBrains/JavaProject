package sample;
/*Саша*/
import java.io.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.*;
import java.util.Properties;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;


public class Program extends Application {
	public static DataCollection<HashMap<Long, ArrayList<ClientData>>> gen_collection =
			new DataCollection<HashMap<Long,ArrayList<ClientData>>>(new HashMap<Long,ArrayList<ClientData>>());
	public static LoggingMachine log;
	public static Properties property;
	public static User loggingUser;
	@Override
	public void start(Stage stage) throws Exception{
		Parent root = FXMLLoader.load(getClass().getResource("../FXML/start.fxml"));//загружаем fxml стартового окна
		Scene scene = new Scene(root);
		stage.setTitle("JAVA DEMO"); //название окна
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show(); //запускаем окно
	}
	@Override
	public void stop() {
		Program.SaveConfig(property, loggingUser);
	}



	public static void main(String[] args) {

		property = loadLog();
		launch(args);

//


	}
	public static Properties loadLog(){
		log = new LoggingMachine(Program.class);
		/*пишем в лог о запуске программы*/
		log.info("Program launched");

		/*создаем поток для конфига и объект для чтения конфига*/
		FileInputStream configFile;
		Properties property = new Properties();

		/*читаем файл конфига*/
		try {
			configFile = new FileInputStream("config.ini");
			property.load(configFile);
		} catch (FileNotFoundException e) {
			log.log(Level.INFO,"Config file not found");
		}
		catch(IOException e) {
			log.log(Level.INFO,"IO exception while loading property");
		}

		if(property.getProperty("log").equals("false"))
			log.revertChanges();

		log.info("Loading last user");
		/*получаем из конфга имя последнего залогиненного юзера*/
		String currentUser = property.getProperty("current_user");

		log.info("Last user is " + currentUser);
		return property;
	}
	/*пишем в лог сообщение о закрытии программы и закрываем ее*/
	public static void Exit(Properties property,User user) {
		SaveConfig(property, user);
		log.info("Program closed");
		System.exit(0);
	}
	public static void SaveConfig(Properties property,User user){
		try {
			property.setProperty("current_user", user.name);
		}
		catch (Exception e){}

		try {
			property.store(new FileOutputStream("sample/config.ini"), null);
		}
		catch (Exception e){
			log.log(Level.INFO,"Config file could not be saved");
		}
	}
	/* Функция проверяет на соответствие введенные пользователем данные
	 * и возвращает доступную ему привелегию*/
	public static void recieveJson(){
		Runnable recieve_data = () -> {
		do {
			ClientData lclient = GetData.Download("127.0.0.1", 8005);

			if (lclient == null)
				break;
			if (Program.gen_collection.getCollection().containsKey(lclient.getUniqKey())) { //???????? ?? ??????? ?????????? ?? ???? ?????

				Program.gen_collection.GetAndAdd(lclient.getUniqKey(), lclient);
			} else {
				ArrayList<ClientData> to_add = new ArrayList<>();
				to_add.add(lclient);
				Program.gen_collection.Add(lclient.getUniqKey(), to_add);
			}

		} while (GetData.Next());
	};
		new Thread(recieve_data).run();

		for (int i = 0;i < Program.gen_collection.getCollection().size();i++){
			System.out.println(Program.gen_collection.getCollection().values().toArray()[i].toString());
		}
	}
	public static void watchStatisticInfo(){
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Watch statistic info");
		alert.setHeaderText(null);
		alert.setContentText("Данная функция находится в разработке :)");

		alert.showAndWait();
	}
}
