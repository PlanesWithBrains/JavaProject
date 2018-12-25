import com.google.gson.Gson;
import com.sun.security.ntlm.Client;

import java.io.*;
import java.lang.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.time.*;

/*класс для урощения работы при генерации адреса*/
class Country{
	public String country;
	public String[] cities;

	public Country(String Country, String[] Cities) {
		country = Country;
		cities = Cities;
	}
}


public class JsonWork {

	/*метод создает случайного клиента и возвращает объект ClientData*/
	static ClientData GenerateData() {
		Random rand = new Random();
		String IP = GenerateIp();
		ClientData client = new ClientData( IP,
											Math.abs(rand.nextLong()),
											Duration.ofHours(Math.abs(rand.nextLong())%512),
											GenerateModules(),
											GenerateAddress(IP));

		return client;
	}

	/* метод создает случайного клиента и возвращает строку формата Json,
	 * описывающую объект ClientData*/
	static String GenJsonClientData() {
		Gson JsonClient = new Gson();

		return JsonClient.toJson(GenerateData());
	}

	/*метод генерирует айпи*/
	private static String GenerateIp() {
		Random r = new Random();
		return 	(127 + r.nextInt(128)) + "." +
				(127 + r.nextInt(128)) + "." +
				(127 + r.nextInt(128)) + "." +
				(127 + r.nextInt(128));
	}

	/*Метод генерирует массив модулей*/
	private static ArrayList<Module> GenerateModules(){
		Random rand = new Random();
		String[] names = {	"Main menu",
							"Settings",
							"FileSystem",
							"EmbedBrowser",
							"Search",
							"VoiceRecognition",
							};

		/*размер имени модуля и количества модулей*/
		int count = rand.nextInt(names.length - 1) + 1;
		ArrayList<Module> modules = new ArrayList<Module>();

		for(int i = 0; i < names.length; i++) {
			/*создаем случайную строку - имя модуля*/
			String name;

			name = names[i];

			Duration module_time =  rand.nextBoolean() ?
									Duration.ofHours(Math.abs(rand.nextLong())%64) :
									Duration.ZERO;
			modules.add(new Module(name, module_time,
					Math.abs(rand.nextLong())%100));
		}
		return modules;
	}


	private static Address GenerateAddress(String IP) {

		Random rand = new Random();
		Country[] countries = {
								new Country("USA",new String[]{	"Franklin",
																"Washington",
																"Springfield",
																"New York"}),
								new Country("Russia",new String[]{	"Abakan",
																"Borovsk",
																"Chelyabinsk",
																"Gus-Khrustalny",
																"Moscow"}),
								new Country("Germany",new String[]{	"Ahrensburg",
																"Bleckede",
																"Baumholder",
																"Delitzsch",
																"Munich"})};
		Country actual_Addr;
		if(!Server.FAST)
			actual_Addr = GetCountryData(IP);
		else
			actual_Addr = null;

		if(actual_Addr == null) {
			/*выбираем случайную страну из массива*/
			int country_index = rand.nextInt(countries.length);
			/*выбираем случайный город данной страны*/
			int city_index = rand.nextInt(countries[country_index].cities.length);

			/*создаем адрес*/
			return new Address(countries[country_index].country,
					countries[country_index].cities[city_index]);
		}
		else
			return new Address(actual_Addr.country,actual_Addr.cities[0]);
	}

	public static Country GetCountryData(String IP){
		String city;
		String country;

		try{
			URL geoip_api_addr = new URL("https://ipapi.co/" + IP + "/json/");
			BufferedReader output = new BufferedReader(new InputStreamReader(geoip_api_addr.openStream()));
			String data = "";

			String line = "";
			do {
				line = output.readLine();
				data += line;
			}while (line != null);

			if(data == null || data.contains("fail"))
				return null;
			int city_start = data.indexOf("city\":\"") + "city\":\"".length() + 1;
			data = data.substring(city_start);
			city = data.substring(0, data.indexOf('"'));

			int country_start = data.indexOf("country\":\"") + "country\":\"".length() + 1;
			data = data.substring(country_start);
			country = data.substring(0, data.indexOf('"'));

			return new Country(country, new String[]{city});
		}
		catch (Exception e){
			System.out.printf(e.getMessage() + "\n");
			if(e.getMessage().contains("timed out"))
				Server.FAST = true;
		}
		return null;
	}
	static String Serialize(ClientData client){

		return new Gson().toJson(client);
	}

	static ClientData Deserialize(String json){
		return new Gson().fromJson(json, ClientData.class);
	}
}