package sample;

import Controllers.StatisticController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;




public class Program extends Application {
	public static DataCollection<HashMap<Long, ArrayList<ClientData>>> gen_collection =
			new DataCollection<HashMap<Long,ArrayList<ClientData>>>(new HashMap<Long,ArrayList<ClientData>>());
	public static LoggingMachine log;
	public static Properties property;
	public static User loggingUser;
	private static boolean fast_enough = true;
	public static ArrayList<ClientData> getSortClients(){

		Collection<ArrayList<ClientData>> temp_map = gen_collection.collection.values();
		ArrayList<ClientData> temp = new ArrayList<ClientData>();
		for(ArrayList<ClientData> clients : temp_map) {
			for(int k = 0; k < clients.size(); k++)
				temp.add(((ClientData) clients.toArray()[k]));
		}
		return temp;
	}

	public static BarChart<String,Number> GetInstanceOfChart(String x_axis,
															 String y_axis,
															 String c_title,
															 Pairs[] arr_pair, int number){
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();
		BarChart<String,Number> chart = new BarChart<String, Number>(xAxis,yAxis);
		chart.setTitle(c_title);
		xAxis.setLabel(x_axis);
		yAxis.setLabel(y_axis);

		XYChart.Series series = new XYChart.Series();
		series.setName(c_title);

		ArrayList<Integer> x = new ArrayList<>();
		int i = 0;
		for(Pairs pair : arr_pair)
			try {
				if(pair._2 instanceof Integer)
					series.getData().add(
							new XYChart.Data((String)pair._1,
							(Integer) pair._2));


				if(pair._2 instanceof Duration)
					series.getData().add(
							new XYChart.Data((String) pair._1,
							(int)((Duration) pair._2).toHours()));
			}
			catch (Exception e){
				e.printStackTrace();
				StatisticController.addConsoleLog(e.getMessage() + "\n");
		}
		chart.getData().add(series);
		StatisticController.setCount(number,arr_pair.length);
		return chart;
	}

	@Override
	public void start(Stage stage) throws Exception{
		//File f = new File(getClass().getResourceAsStream("../FXML/start.fxml"))
		Parent root = FXMLLoader.load(this.getClass().getResource("/FXML/start.fxml"));//загружаем fxml стартового окна
		Scene scene = new Scene(root);
		stage.setTitle("JAVA DEMO"); //название окна
		stage.setScene(scene);
		stage.setResizable(false);
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/ImagesAndFonts/LOGOJAVA.png")));
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
			configFile = new FileInputStream("./config.ini");
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
			property.store(new FileOutputStream("./config.ini"), null);
		}
		catch (Exception e){
			log.log(Level.INFO,"Config file could not be saved");
		}
	}
	/* Функция проверяет на соответствие введенные пользователем данные
	 * и возвращает доступную ему привелегию*/
	public static void recieveJson(String IP, int Port){
		Runnable recieve_data = () -> {

			boolean[] result_of_conversation;
			do {
				ClientData lclient = GetData.Download(IP, Port);

				if (lclient == null)
					break;

				//проверка на медленный интернет
				if(fast_enough) {
                    long start_ms = System.currentTimeMillis();
                    ClientTrustworthy(lclient);
                    fast_enough = System.currentTimeMillis() - start_ms > 3*1000 ? false : true;
                }

				if (gen_collection.collection.containsKey(lclient.uniqKey)) {

					gen_collection.GetAndAdd(lclient.uniqKey, lclient);
				} else {
					ArrayList<ClientData> to_add = new ArrayList<ClientData>();
					to_add.add(lclient);
					gen_collection.Add(lclient.uniqKey, to_add);
				}
				result_of_conversation = GetData.Next();
				if(result_of_conversation[1])
					System.err.println("Object " + lclient.hashCode() + " recieved correctly");
			} while (result_of_conversation[0]);
		};
		new Thread(recieve_data).run();
		GetData.BreakConnection();
		for (int i = 0;i < Program.gen_collection.getCollection().size();i++){
			String log = Program.gen_collection.getCollection().values().toArray()[i].toString() + "\n";
			/*Image img = new ClientData().
					GetMapInstance("https://static-maps.yandex.ru/1.x/?ll=65.398900,55.928800&z=10&size=450,450&z=13&l=map&pt=65.398900,55.928800");*/
			System.out.println(log);
			StatisticController.addConsoleLog(log);
		}

		Collection<ArrayList<ClientData>> temp_map = gen_collection.collection.values();
		ArrayList<ClientData> DataArray = new ArrayList<ClientData>();
		for(ArrayList<ClientData> clients : temp_map) {
			for(int k = 0; k < clients.size(); k++)
				DataArray.add(((ClientData) clients.toArray()[k]));
		}
		Statistic stat = new Statistic(DataArray);

	}

	public static ArrayList<ClientData> LoadFile(String file_content){
		String[] lines = file_content.split(System.getProperty("line.separator"));
		ArrayList<ClientData> clients = new ArrayList<>();
		for(String line : lines){
			ClientData client = JsonWork.Deserialize(line);
			clients.add(client);
			ClientTrustworthy(client);
		}
		return clients;
	}

	public static void ClientTrustworthy_old(ClientData client){
		try{
			URL geoip_api_addr = new URL("https://ipapi.co/" + client.clientIp.getHostAddress() + "/json/");
			BufferedReader  output = new BufferedReader(new InputStreamReader(geoip_api_addr.openStream()));
			String data = "";

			String line = "";
			do {
				line = output.readLine();
				data += line;
			}while (line != null);

			if(data == null || data.contains("fail"))
				return;
			if(data.contains(client.addr.city))
				client.trusted = true;
			client.ActualLocation = "https://static-maps.yandex.ru/1.x/?ll=#lonlat#&z=#zoom#&size=450,450&z=13&l=map&pt=#lonlat#";
			String 	lon = data.substring(data.indexOf("\"longitude\":") + "\"longitude\":".length()) + 1;
					lon = lon.substring(0,lon.indexOf(','));
			String 	lat = data.substring(data.indexOf("\"latitude\":") + "\"latitude\":".length() + 1);
					lat = lat.substring(0,lat.indexOf(','));

            client.latitude = lat;
            client.longitude = lon;

			String lonlat = lon + "," + lat;
			client.ActualLocation = client.ActualLocation.replaceAll("#lonlat#",lonlat);
			client.ActualLocation = client.ActualLocation.replaceAll("#zoom#","10");
			client.ActualLocation.replaceAll(" ","");
		}
		catch (Exception e){
			System.out.printf(e.getMessage());
			client.ActualLocation = "";
			client.trusted = false;
		}

	}


	public static void ClientTrustworthy(ClientData client){
		try{
			URL geoip_api_addr = new URL("https://ipapi.co/" + client.clientIp.getHostAddress() + "/latlong/");
			BufferedReader  output = new BufferedReader(new InputStreamReader(geoip_api_addr.openStream()));
			String data = output.readLine();

			geoip_api_addr = new URL("https://ipapi.co/" + client.clientIp.getHostAddress() + "/city/");
			if((new BufferedReader(new InputStreamReader(geoip_api_addr.openStream()))).readLine().contains(client.addr.city))
				client.trusted = true;
			client.ActualLocation = "https://static-maps.yandex.ru/1.x/?ll=#lonlat#&z=#zoom#&size=450,450&z=13&l=map&pt=#lonlat#";
			String 	lat = data.substring(0,data.indexOf(','));
			data = data.substring(lat.length() + 1);
			String 	lon = data;

			client.latitude = lat;
			client.longitude = lon;

			String lonlat = lon + "," + lat;
			if(lonlat.toLowerCase().contains("undef"))
				return;
			client.ActualLocation = client.ActualLocation.replaceAll("#lonlat#",lonlat);
			client.ActualLocation = client.ActualLocation.replaceAll("#zoom#","10");
			client.ActualLocation.replaceAll(" ","");
		}
		catch (Exception e){
			System.out.printf(e.getMessage());
			client.ActualLocation = "";
			client.trusted = false;
		}

	}
}
