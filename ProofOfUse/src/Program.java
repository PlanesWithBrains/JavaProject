/*Саша*/
import java.awt.*;
import java.io.*;
import java.lang.*;
import java.lang.reflect.Array;
import java.util.logging.*;
import java.io.*;
import java.util.Properties;
import java.util.Scanner;
import java.util.*;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import com.google.gson.Gson;

import static org.knowm.xchart.XYSeries.XYSeriesRenderStyle.Scatter;


enum Priveledge{user,root,wrong_pass}

class User{
	Priveledge priveledge;
	String name;
	String password;
}

public class Program {

	static LoggingMachine log;
	public static void main(String[] args) {
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

		System.out.println("\tWelcome, " + currentUser);

		/* этот бесконечный цикл нужен чтобы вернуться к началу, в случае неправильного ввода пароля
		 * а был бы тут goto, то цикла бы не было...*/
		while(true) {
			System.out.println("================MENU================");
			System.out.println("\tpress 1 - To sign in profile");
			System.out.println("\tpress 2 - To continue work");
			System.out.println("\tpress 0 - To exit");
			System.out.println("====================================");

			/*здесь считываем выбор пользователя*/
			Scanner scanner = new Scanner(System.in);
			String input = scanner.nextLine();

			/*создаем пользователя*/
			User loggingUser = new User();

			/* switch на выбор того, что ввел пользователь
			 * можно было сделать if, но вдруг параметры разрастутся -
			 * будет легче их добавить в switch*/
			switch(input) {
				case "0": Exit(property,loggingUser);
					break;
				case "1":

					/*формируем пользователя из ввода пользователя (каламбур)*/
					System.out.println("=========Enter profile name=========");
					loggingUser.name = scanner.nextLine();
					System.out.println("=========Enter your password========");
					loggingUser.password = scanner.nextLine();

					break;

				case "2":
					loggingUser.name = property.getProperty("current_user");
					loggingUser.password = property.getProperty(loggingUser.name).split(",")[0];
			}

			/*объект Priveledge получит от Verify тип пользователя и выберет по нему методы*/
			Priveledge priv = Verify(loggingUser,property);
			switch(priv) {
				case user:
					System.out.println("================MENU================");
					System.out.println("To watch statistic info - press 1");
					System.out.println("To recieve json statistic file - press 2");
					System.out.println("For exit  -  press 0");
					System.out.println("====================================");
					input = scanner.nextLine();
					break;
				case root:
					System.out.println("======++++++Access Granted++++++====");

					System.out.println("================MENU================");
					System.out.println("To watch statistic info - press 1");
					System.out.println("To recieve json statistic file - press 2");
					System.out.println("To switch debug mode - press 3");
					System.out.println("To switch autotest mode - press 4");
					System.out.println("For exit  -  press 0");
					System.out.println("====================================");
					input = scanner.nextLine();
					break;
				case wrong_pass:

					log.info("User entered wrong password");
					System.out.println("======++++++Access Denied++++++=====");
					continue;
			}
	/* Работа с коллекцией типа HashMap для класса-контейнера ClientData
	эта коллекция является массивом по всем данным клиента по заданному ключу
	commit by Anton
*/

			/*С дженерик коллекцией теперь только так*/
			DataCollection<HashMap<Long,ArrayList<ClientData>>> gen_collection =
					new DataCollection<HashMap<Long,ArrayList<ClientData>>>(new HashMap<Long,ArrayList<ClientData>>());
			switch (input){
				case "0": log.log(Level.INFO, "Program shutdown"); Exit(property,loggingUser);
				case "1": break;
				case "2":
					int count = 0;

					double avg = 0;
					//чтение данных с сервера с этого момента происходит в отдельном потоке
					Runnable recieve_data = () -> {
						do {
							ClientData lclient = GetData.Download("127.0.0.1", 8005);

							if (lclient == null)
								break;
							if (gen_collection.collection.containsKey(lclient.uniqKey)) { //проверка на условие встречался ли ключ ранее

								gen_collection.GetAndAdd(lclient.uniqKey, lclient);
							} else {
								ArrayList<ClientData> to_add = new ArrayList<ClientData>();
								to_add.add(lclient);
								gen_collection.Add(lclient.uniqKey, to_add);
							}

						} while (GetData.Next());
					};
					new Thread(recieve_data).run();

					for (int i = 0;i < gen_collection.collection.size();i++){
						System.out.println(gen_collection.collection.values().toArray()[i].toString());
					}
					break;
				/*если свойство debug содержит true, то тогда меняем его на false, иначе меняем false на true*/
				case "3": 	property.setProperty("debug", property.getProperty("debug").equals("true") ? "false" : "true");
							property.setProperty("log", property.getProperty("log").equals("true") ? "false" : "true");
							log.revertChanges();
					break;
				/*если свойство autotest содержит true, то тогда меняем его на false, иначе меняем false на true*/
				case "4": property.setProperty("autotest", property.getProperty("autotest").equals("true") ? "false" : "true");
					break;
			}

			SaveConfig(property, loggingUser);
		}

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
			property.store(new FileOutputStream("config.ini"), null);
		}
		catch (Exception e){
			log.log(Level.INFO,"Config file could not be saved");
		}
	}
	/* Функция проверяет на соответствие введенные пользователем данные
	 * и возвращает доступную ему привелегию*/
	public static Priveledge Verify(User user, Properties property) {
		log.info("Verifying entered data");

		/*получаем в строку свойства для введенного ника*/
		String props = property.getProperty(user.name);

		/*если чтение свойства вернуло null, то свойство не прочиталось*/
		if(props == null) {
			log.log(Level.INFO, "Wrong username or encoding error in config.ini");
			return Priveledge.wrong_pass;
		}
		
		/*в массив userData на [0] положится пароль из конфига 
		  а на [1] его привелегия в виде строки*/
		String[] userData = props.split(",");

		/*формируем из строки привелегии enum и кладем его в user`а*/
		user.priveledge = Priveledge.valueOf(userData[1]);
		if(userData[0].equals(user.password))
			return user.priveledge;
		else
			return Priveledge.wrong_pass;


	}
}
