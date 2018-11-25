/*����*/
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
		/*����� � ��� � ������� ���������*/
		log.info("Program launched");

		/*������� ����� ��� ������� � ������ ��� ������ �������*/
		FileInputStream configFile;
		Properties property = new Properties();

		/*������ ���� �������*/
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
		/*�������� �� ������ ��� ���������� ������������� �����*/
		String currentUser = property.getProperty("current_user");

		log.info("Last user is " + currentUser);

		System.out.println("\tWelcome, " + currentUser);

		/* ���� ����������� ���� ����� ����� ��������� � ������, � ������ ������������� ����� ������
		 * � ��� �� ��� goto, �� ����� �� �� ����...*/
		while(true) {
			System.out.println("================MENU================");
			System.out.println("\tpress 1 - To sign in profile");
			System.out.println("\tpress 2 - To continue work");
			System.out.println("\tpress 0 - To exit");
			System.out.println("====================================");

			/*����� ��������� ����� ������������*/
			Scanner scanner = new Scanner(System.in);
			String input = scanner.nextLine();

			/*������� ������������*/
			User loggingUser = new User();

			/* switch �� ����� ����, ��� ���� ������������
			 * ����� ���� ������� if, �� ����� ��������� ����������� -
			 * ����� ����� �� �������� � switch*/
			switch(input) {
				case "0": Exit(property,loggingUser);
					break;
				case "1":

					/*��������� ������������ �� ����� ������������ (��������)*/
					System.out.println("=========Enter profile name=========");
					loggingUser.name = scanner.nextLine();
					System.out.println("=========Enter your password========");
					loggingUser.password = scanner.nextLine();

					break;

				case "2":
					loggingUser.name = property.getProperty("current_user");
					loggingUser.password = property.getProperty(loggingUser.name).split(",")[0];
			}

			/*������ Priveledge ������� �� Verify ��� ������������ � ������� �� ���� ������*/
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
	/* ������ � ���������� ���� HashMap ��� ������-���������� ClientData
	��� ��������� �������� �������� �� ���� ������ ������� �� ��������� �����
	commit by Anton
*/

			/*� �������� ���������� ������ ������ ���*/
			DataCollection<HashMap<Long,ArrayList<ClientData>>> gen_collection =
					new DataCollection<HashMap<Long,ArrayList<ClientData>>>(new HashMap<Long,ArrayList<ClientData>>());
			switch (input){
				case "0": log.log(Level.INFO, "Program shutdown"); Exit(property,loggingUser);
				case "1": break;
				case "2":
					int count = 0;

					double avg = 0;
					//������ ������ � ������� � ����� ������� ���������� � ��������� ������
					Runnable recieve_data = () -> {
						do {
							ClientData lclient = GetData.Download("127.0.0.1", 8005);

							if (lclient == null)
								break;
							if (gen_collection.collection.containsKey(lclient.uniqKey)) { //�������� �� ������� ���������� �� ���� �����

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
				/*���� �������� debug �������� true, �� ����� ������ ��� �� false, ����� ������ false �� true*/
				case "3": 	property.setProperty("debug", property.getProperty("debug").equals("true") ? "false" : "true");
							property.setProperty("log", property.getProperty("log").equals("true") ? "false" : "true");
							log.revertChanges();
					break;
				/*���� �������� autotest �������� true, �� ����� ������ ��� �� false, ����� ������ false �� true*/
				case "4": property.setProperty("autotest", property.getProperty("autotest").equals("true") ? "false" : "true");
					break;
			}

			SaveConfig(property, loggingUser);
		}

	}

	/*����� � ��� ��������� � �������� ��������� � ��������� ��*/
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
	/* ������� ��������� �� ������������ ��������� ������������� ������
	 * � ���������� ��������� ��� ����������*/
	public static Priveledge Verify(User user, Properties property) {
		log.info("Verifying entered data");

		/*�������� � ������ �������� ��� ���������� ����*/
		String props = property.getProperty(user.name);

		/*���� ������ �������� ������� null, �� �������� �� �����������*/
		if(props == null) {
			log.log(Level.INFO, "Wrong username or encoding error in config.ini");
			return Priveledge.wrong_pass;
		}
		
		/*� ������ userData �� [0] ��������� ������ �� ������� 
		  � �� [1] ��� ���������� � ���� ������*/
		String[] userData = props.split(",");

		/*��������� �� ������ ���������� enum � ������ ��� � user`�*/
		user.priveledge = Priveledge.valueOf(userData[1]);
		if(userData[0].equals(user.password))
			return user.priveledge;
		else
			return Priveledge.wrong_pass;


	}
}
