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

	static Logger log = Logger.getLogger(Program.class.getName());
	static XYChart chart;
    static XYChart chart_zoomed;
	static SwingWrapper<XYChart> sw;
	static ArrayList<XYChart> graphics = new ArrayList<XYChart>();
	static boolean update_chart = false;

	public static void main(String[] args) {
		LoggingMachine log = new LoggingMachine(Program.class, true);
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

			//LinkedList
			/*DataCollection<LinkedList<ArrayList<ClientData>>> gen_collection =
					new DataCollection<LinkedList<ArrayList<ClientData>>>(new LinkedList<ArrayList<ClientData>>());
			*/

			/*���� ��� ������� ������ � ����������*/
			ArrayList<Double> elapsed_times = new ArrayList<Double>();
            ArrayList<Double> elements = new ArrayList<Double>();
			ClientData lclient;	//����� ������� ClientData ��� ������ � ���������
			switch (input){
				case "0": log.log(Level.INFO, "Program shutdown"); Exit(property,loggingUser);
				case "1": break;
				case "2":
					int count = 0;

                    long[] elapsed;
					double avg = 0;
					do {
						lclient = GetData.Download("127.0.0.1", 8005);

						if(lclient == null)
							break;
						if (gen_collection.collection.containsKey(lclient.uniqKey)){ //�������� �� ������� ���������� �� ���� �����

							elapsed = gen_collection.GetAndAdd(lclient.uniqKey,lclient);
						}
						else {
							ArrayList<ClientData> to_add = new ArrayList<ClientData>();
							to_add.add(lclient);
							elapsed = gen_collection.Add(lclient.uniqKey, to_add);
						}

						if(count % 5000 == 1)
							elapsed = gen_collection.Remove(gen_collection.collection.keySet().toArray()[0]);
						count++;

						//if(count % 100 == 1) {
							elapsed_times.add(Double.valueOf((double)elapsed[0]) + Double.valueOf((double)elapsed[1]) / 1000.0);
							elements.add(Double.valueOf((double)gen_collection.collection.size()));
						//}

						/*��������� ������ ��� �������*/
						avg = (avg * (elapsed_times.size() - 1) + elapsed_times.get(elapsed_times.size() - 1))/elapsed_times.size();

                        if(count % 500 == 1) {
							UpdateChart(elements,elapsed_times, avg,gen_collection.collection.getClass());
						}

					}while (GetData.Next());
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

	static XYChart GetInstanceOfChart(){
        chart = QuickChart.getChart( "������ ������� ��������� � ���������",
                "���-�� ���������",
                "�����", "�����", new double[]{0}, new double[]{0});

        XYSeries average_series = chart.addSeries("�������",new double[]{0});

        average_series.setFillColor(new Color(255,0,0,40));
        average_series.setMarkerColor(new Color(255,0,0,100));

        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.StepArea);
        chart.getStyler().setChartTitleVisible(true);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideS);
        chart.getStyler().setMarkerSize(1);
        return chart;
    }
	static void UpdateChart(ArrayList<Double> elements, ArrayList<Double> elapsed_times, double avg, Class type_of_collection){
		if(!update_chart) {
			graphics.add(GetInstanceOfChart());
            graphics.add(GetInstanceOfChart());
			sw = new SwingWrapper<XYChart>(graphics);
			sw.displayChartMatrix();
			update_chart = true;
		}
		else{
			ArrayList<Double> avg_line = new ArrayList<Double>();
			for(int i = 0; i < elements.size(); i++)
				avg_line.add(Double.valueOf(avg));


			((XYChart) graphics.toArray()[1]).updateXYSeries("�����", elements, elapsed_times, null);
			((XYChart) graphics.toArray()[1]).updateXYSeries("�������", elements, avg_line, null);


			((XYChart) graphics.toArray()[0]).updateXYSeries("�����", elements.subList(elements.size()-101,elements.size()-1),
																				elapsed_times.subList(elapsed_times.size()-101,elapsed_times.size()-1),
																				null);
			((XYChart) graphics.toArray()[0]).updateXYSeries("�������", elements.subList(elements.size()-101,elements.size()-1), avg_line.subList(0,100), null);

			sw.repaintChart(0);
			sw.repaintChart(1);
		}
	}

	/*����� � ��� ��������� � �������� ��������� � ��������� ��*/
	public static void Exit(Properties property,User user) {
		SaveConfig(property, user);
		log.info("Program closed");
		System.exit(0);
	}
	public static void SaveConfig(Properties property,User user){
		property.setProperty("current_user",user.name);
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
		String props= property.getProperty(user.name);

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
