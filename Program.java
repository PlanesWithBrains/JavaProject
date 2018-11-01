//
import java.io.*;
import java.lang.*;
import java.util.logging.*;
import java.io.*;
import java.util.Properties;
import java.util.Scanner;
import java.util.*;



enum Priveledge{user,root,wrong_pass}

class User{
	Priveledge priveledge;
	String name;
	String password;
}

public class Program {

	static Logger log = Logger.getLogger(Program.class.getName());
	public static void main(String[] args) {
		// TODO Auto-generated method stub
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
			log.log(Level.INFO,"File not found");
		}
		catch(IOException e) {
			log.log(Level.INFO,"IO exception while loading property");

		}

		
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
	HashMap<Long,ArrayList<ClientData>> clients = new HashMap<Long,ArrayList<ClientData>>();
	ClientData lclient = new ClientData();//копия объекта ClientData для записи в коллекцию
	switch (input){
		case "0": Exit(property,loggingUser);
		case "1": break;
		case "2":
			do {
				//System.out.println(GetData.Download("127.0.0.1", 1998).toString());
				lclient=GetData.Download("127.0.0.1", 1998);
				ArrayList<ClientData> list_client = new ArrayList<ClientData>();

				if (clients.containsKey(lclient.uniqKey)){ //проверка на условие встречался ли ключ ранее
					//я очень не уверен в get ключа, TODO check it
					clients.get(lclient.uniqKey).add(lclient);
				}
				else {
					list_client.add(lclient);
					clients.put(lclient.uniqKey, list_client);

					//("тут должен быть ключ",ArrayList"Объект класса ClientData");
				}


			}
			while (GetData.Next());
			break;
		/*если свойство debug содержит true, то тогда меняем его на false, иначе меняем false на true*/
		case "3": property.setProperty("debug", property.getProperty("debug").equals("true") ? "false" : "true");
			break;
		/*если свойство autotest содержит true, то тогда меняем его на false, иначе меняем false на true*/
		case "4": property.setProperty("autotest", property.getProperty("autotest").equals("true") ? "false" : "true");
			break;
	}
}
		
	}
	
	/*пишем в лог сообщение о закрытии программы и закрываем ее*/
	public static void Exit(Properties property,User user) {
		property.setProperty("current_user",user.name);
		try {
			property.store(new FileOutputStream("config.ini"), null);
		}
		catch (Exception e){
			log.log(Level.INFO,"Config file could not be saved");
		}
		log.info("Program closed");
		System.exit(0);
	}
	
	/* Функция проверяет на соответствие введенные пользователем данные
	 * и возвращает доступную ему привелегию*/
	public static Priveledge Verify(User user, Properties property) {
		log.info("Verifying entered data");
		
		/*получаем в строку свойства для введенного ника*/
		String props= property.getProperty(user.name);
		
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
