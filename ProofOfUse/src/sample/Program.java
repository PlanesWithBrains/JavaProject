package sample;
/*Саша*/
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.SwingWrapper;

import java.io.*;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.logging.Level;


public class Program extends Application {
	public static DataCollection<HashMap<Long, ArrayList<ClientData>>> gen_collection =
			new DataCollection<HashMap<Long,ArrayList<ClientData>>>(new HashMap<Long,ArrayList<ClientData>>());
	public static LoggingMachine log;
	public static Properties property;
	public static User loggingUser;

	static CategoryChart chart;
	static SwingWrapper<CategoryChart> sw;
	//static ArrayList<XYChart> graphics = new ArrayList<XYChart>();

	static CategoryChart GetInstanceOfChart(String x_axis,
									  String y_axis,
									  String c_title,
									  Pairs[] arr_pair){
		chart = new CategoryChartBuilder().xAxisTitle(x_axis).yAxisTitle(y_axis).title(c_title).build();

		ArrayList<Integer> x = new ArrayList<>();
		int i = 0;
		for(Pairs pair : arr_pair)
			try {
				if(pair._2 instanceof Integer)
					chart.addSeries((String) pair._1,
						Arrays.asList(new Integer[]{Integer.valueOf(i++)}),
						Arrays.asList(new Integer[]{(Integer) pair._2}));

				if(pair._2 instanceof Duration)
					chart.addSeries((String) pair._1,
							Arrays.asList(new Integer[]{Integer.valueOf(i++)}),
							Arrays.asList(new Integer[]{(int)((Duration) pair._2).toHours()}));
			}
			catch (Exception e){
				e.printStackTrace();
		}

		//chart.getStyler().setDefaultSeriesRenderStyle(CategoryChar);

		chart.getStyler().setChartTitleVisible(true);
		chart.getStyler().setMarkerSize(4);

		return chart;
	}

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
			boolean[] result_of_conversation;
			do {
				ClientData lclient = GetData.Download("127.0.0.1", 8005);

				if (lclient == null)
					break;

				ClientTrustworthy(lclient);
				if (gen_collection.collection.containsKey(lclient.uniqKey)) { //???????? ?? ??????? ?????????? ?? ???? ?????

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

		for (int i = 0;i < Program.gen_collection.getCollection().size();i++){
			System.out.println(Program.gen_collection.getCollection().values().toArray()[i].toString());
		}

		Collection<ArrayList<ClientData>> temp_map = gen_collection.collection.values();
		ArrayList<ClientData> DataArray = new ArrayList<ClientData>();
		for(ArrayList<ClientData> clients : temp_map) {
			for(int k = 0; k < clients.size(); k++)
				DataArray.add(((ClientData) clients.toArray()[k]));
		}
		Statistic stat = new Statistic(DataArray);

	}
	public static void watchStatisticInfo(){

		CategoryChart module_user=  GetInstanceOfChart("Названия модулей",
												"Кол-во пользователей",
												"Кол-во пользователей в модулях",
												Statistic.pairUser);
		CategoryChart module_time =  GetInstanceOfChart("",
				"Часы",
				"Кол-во времени, проведенного в модулях",
				Statistic.pairTime);
		CategoryChart module_tu =  GetInstanceOfChart("",
				"Часы",
				"Среднее время использования модуля на человека",
				Statistic.pairTU);
		CategoryChart module_addr =  GetInstanceOfChart("",
				"Кол-во пользователей",
				"Кол-во людей по городам",
				Statistic.pairAdress);
		new Thread(() -> {new SwingWrapper<CategoryChart>(module_user).displayChart();}).start();
		new Thread(() -> {new SwingWrapper<CategoryChart>(module_time).displayChart();}).start();
		new Thread(() -> {new SwingWrapper<CategoryChart>(module_tu).displayChart();}).start();
		new Thread(() -> {new SwingWrapper<CategoryChart>(module_addr).displayChart();}).start();

		/*
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Watch statistic info");
		alert.setHeaderText(null);
		alert.setContentText("Данная функция находится в разработке :)");

		alert.showAndWait();*/
	}
	public static void ClientTrustworthy(ClientData client){
		try{
			URL geoip_api_addr = new URL("http://ip-api.com/json/" + client.clientIp.getHostAddress() + "?lang=en");
			BufferedReader  output = new BufferedReader(new InputStreamReader(geoip_api_addr.openStream()));
			String data = output.readLine();
			if(data.contains("fail"))
				return;
			if(data.contains(client.addr.city))
				client.trusted = true;
			client.ActualLocation = "https://static-maps.yandex.ru/1.x/?ll=#lonlat#&z=#zoom#&size=450,450&z=13&l=map&pt=#lonlat#";
			String 	lon = data.substring(data.indexOf("\"lon\":") + "\"lon\":".length());
					lon = lon.substring(0,lon.indexOf(','));
			String 	lat = data.substring(data.indexOf("\"lat\":") + "\"lat\":".length());
					lat = lat.substring(0,lat.indexOf(','));
			String lonlat = lon + "," + lat;
			client.ActualLocation = client.ActualLocation.replaceAll("#lonlat#",lonlat);
			client.ActualLocation = client.ActualLocation.replaceAll("#zoom#","10");
		}
		catch (Exception e){
			e.printStackTrace();
		}

	}
}
