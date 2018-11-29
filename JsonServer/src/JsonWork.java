import com.google.gson.Gson;
import com.sun.security.ntlm.Client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.*;
import java.nio.charset.Charset;
import java.util.*;
import java.time.*;

/*����� ��� �������� ������ ��� ��������� ������*/
class Country{
	public String country;
	public String[] cities;

	public Country(String Country, String[] Cities) {
		country = Country;
		cities = Cities;
	}
}


public class JsonWork {

	/*����� ������� ���������� ������� � ���������� ������ ClientData*/
	static ClientData GenerateData() {
		Random rand = new Random();
		ClientData client = new ClientData( GenerateIp(),
											Math.abs(rand.nextLong()),
											Duration.ofHours(Math.abs(rand.nextLong())%512),
											GenerateModules(),
											GenerateAddress());

		return client;
	}

	/* ����� ������� ���������� ������� � ���������� ������ ������� Json,
	 * ����������� ������ ClientData*/
	static String GenJsonClientData() {
		Gson JsonClient = new Gson();

		return JsonClient.toJson(GenerateData());
	}

	/*����� ���������� ����*/
	private static String GenerateIp() {
		Random r = new Random();
		return 	(127 + r.nextInt(128)) + "." +
				(127 + r.nextInt(128)) + "." +
				(127 + r.nextInt(128)) + "." +
				(127 + r.nextInt(128));
	}

	/*����� ���������� ������ �������*/
	private static ArrayList<Module> GenerateModules(){
		Random rand = new Random();
		String[] names = {	"Main menu",
							"Settings",
							"FileSystem",
							"EmbedBrowser",
							"Search",
							"VoiceRecognition"};

		/*������ ����� ������ � ���������� �������*/
		int count = rand.nextInt(names.length - 1) + 1;
		ArrayList<Module> modules = new ArrayList<Module>();

		for(int i = 0; i < names.length; i++) {
			/*������� ��������� ������ - ��� ������*/
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
		/*�������� ��������� ������ �� �������*/
		int country_index = rand.nextInt(countries.length);
		/*�������� ��������� ����� ������ ������*/
		int city_index = rand.nextInt(countries[country_index].cities.length);

		/*������� �����*/
		Address addr = new Address(	countries[country_index].country,
									countries[country_index].cities[city_index]);

		return addr;
	}

	static String Serialize(ClientData client){

		return new Gson().toJson(client);
	}

	static ClientData Deserialize(String json){
		return new Gson().fromJson(json, ClientData.class);
	}
}