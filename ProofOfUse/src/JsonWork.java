import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.sun.security.ntlm.Client;

import java.io.*;
import java.lang.*;
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
		ClientData client = new ClientData( GenerateIp(),
											Math.abs(rand.nextLong()),
											Duration.ofHours(Math.abs(rand.nextLong())%512),
											GenerateModules(),
											GenerateAddress());
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
		return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
	}

	/*Метод генерирует массив модулей*/
	private static ArrayList<Module> GenerateModules(){
		Random rand = new Random();

		/*размер имени модуля и количества модулей*/
		int size = rand.nextInt(10) + 3;
		ArrayList<Module> modules = new ArrayList<Module>();
	    
		for(int i = 0; i < size; i++) {
			/*создаем случайную строку - имя модуля*/
			byte[] array = new byte[size];
		    for(int j = 0; j < array.length; j++)
		    	array[j] = (byte)('0' + rand.nextInt(74));
		    
			modules.add(new Module( new String(array, Charset.forName("UTF-8")),
						Duration.ofHours(Math.abs(rand.nextLong())%64),
					Math.abs(rand.nextLong())%100));
		}
		return modules;
	}

	private static Address GenerateAddress() {
		
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
		/*выбираем случайную страну из массива*/
		int country_index = rand.nextInt(countries.length);
		/*выбираем случайный город данной страны*/
		int city_index = rand.nextInt(countries[country_index].cities.length);

		/*создаем адрес*/
		Address addr = new Address(	countries[country_index].country,
									countries[country_index].cities[city_index]);
		
		return addr;
	}

	static String Serialize(ClientData client){

		return new Gson().toJson(client);
	}

	static ClientData Deserialize(String json){
		JsonReader reader = new JsonReader(new StringReader(json));
		reader.setLenient(true);

		return new Gson().fromJson(reader, ClientData.class);
	}
}