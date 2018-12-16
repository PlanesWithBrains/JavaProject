package sample;

import java.time.Duration;

import java.util.ArrayList;
import java.util.HashSet;

public class Statistic {
    static ArrayList<ClientData> client;
    static HashSet<Module> modules = new HashSet<Module>();
    static HashSet<String> modulesName = new HashSet<String>();
    static HashSet<String> address = new HashSet<>();
    static Pairs[] pairUser,pairTime,pairTU,pairAdress; // pairUser - для функции SortingByUsers, pairTime - TimePerModule, pairTU - Time per Users

    Statistic(ArrayList<ClientData> client){
        this.client = client;
        NumberofModules();
        SortingByUsers();
        TimePerModule();
        TimePerUsers();
        UsersPerCities();
    }
    void NumberofModules(){
        for(int i = 0;i<client.size();i++) {
            for (int j = 0; j < client.get(i).modules.size(); j++) {
                modulesName.add(client.get(i).modules.get(j).module);//cоздаем хэшсет с уникальными модулями(имеется ввиду отбрасываем повторения)
                modules.add(client.get(i).modules.get(j));
            }
        }
    }
    void SortingByUsers(){// для графика (кол-во человек(у)/ по каждому модулю (х)
        this.pairUser = new Pairs[modulesName.size()];
        for(int i=0;i<modulesName.size();i++){
            int count=0;
            for(int j=0;j<client.size();j++){
                for(int y=0;y<client.get(j).modules.size();y++) {
                    if ((modulesName.toArray()[i]).equals(client.get(j).modules.get(y).module) &&
                            !client.get(j).modules.get(y).moduleUsage.equals(Duration.ZERO) ) {
                        count++;
                    }
                    else{;}
                }
            }
            pairUser[i] = new Pairs<String,Integer>(((String)modulesName.toArray()[i]),count);
        }
    }
    void TimePerModule(){// время использования каждого модуля
        this.pairTime = new Pairs[modulesName.size()];
        for(int i=0;i<modulesName.size();i++){
            Duration time = Duration.ZERO;
            for(int j=0;j<client.size();j++){
                for(int y=0;y<client.get(i).modules.size();y++) {
                    if ((modulesName.toArray()[i]).equals(client.get(j).modules.get(y).module)){
                        time = time.plus(client.get(j).modules.get(y).moduleUsage);
                    }
                }
            }
            pairTime[i] = new Pairs<String,Duration>(((String)modulesName.toArray()[i]),time);
        }
    }
    void TimePerUsers(){//функция предполагает вызов двух предыдущих
        this.pairTU = new Pairs[modulesName.size()];
        for(int i=0;i<modulesName.size();i++){
            Duration avr = Duration.ZERO;
            for(int j=0;j<pairUser.length;j++) {
                if (pairTime[i].equals(pairUser[j])) {
                    avr = pairTime[i].div(((Integer)pairUser[i]._2).longValue());
                }
            }
          pairTU[i] = new Pairs<String,Duration>(((String)modulesName.toArray()[i]),avr);
        }
    }
    void UsersPerCities() {
        for (int i = 0; i < client.size(); i++) {
            address.add(client.get(i).addr.city);//cоздаем хэшсет с уникальными городами(имеется ввиду отбрасываем повторения)
        }
        this.pairAdress = new Pairs[address.size()];
        for (int i = 0;i < address.size();i++){
            int count = 0;
            for(int j = 0; j < client.size(); j++){
                if(address.toArray()[i].equals(client.get(j).addr.city))count++;
            }
            pairAdress[i] = new Pairs<String,Integer>((String)address.toArray()[i],count);//пара хранится в формате город/кол-во юзеров
            // для графика можно ипользовать только 3-4 наиболее популярных города
        }
    }
}


