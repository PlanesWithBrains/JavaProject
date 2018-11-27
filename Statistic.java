import java.time.Duration;
import javafx.util.Pair;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;

public class Statistic {
    ArrayList<ClientData> client;
    HashSet<Module> modules = new HashSet<Module>();
    Object[] pairUser,pairTime; // pairUser - для функции SortingByUsers, pairTime - TimePerModule

    Statistic(ArrayList<ClientData> client){
        this.client = client;
    }
    void NumberofModules(){
        for(int i = 0;i<client.size();i++){
            for(int j = 0; j < client.get(i).modules.size(); j++)
            modules.add(client.get(i).modules.get(j));//cоздаем хэшсет с уникальными модулями(имеется ввиду отбрасываем повторения)
        }
    }
    void SortingByUsers(){// для графика (кол-во человек(у)/ по каждому модулю (х)
        this.pairUser = new Object[modules.size()];
        for(int i=0;i<modules.size();i++){
            int count=0;
            for(int j=0;j<client.size();j++){
                for(int y=0;y<client.get(i).modules.size();y++) {
                    if (((Module) modules.toArray()[j]).module.equals(client.get(j).modules.get(y)))count++;
                }
            }
            pairUser[i] = new Pairs<String,Integer>(((Module)modules.toArray()[i]).module,count);
        }
    }
    void TimePerModule(){// время использования каждого модуля
        this.pairTime = new Object[modules.size()];
        for(int i=0;i<modules.size();i++){
            Duration time = null;
            for(int j=0;j<client.size();j++){
                for(int y=0;y<client.get(i).modules.size();y++) {
                    if (((Module) modules.toArray()[j]).module.equals(client.get(j).modules.get(y))){
                        time.plus(client.get(j).modules.get(y).moduleUsage);
                    }
                }
            }
            pairTime[i] = new Pairs<String,Duration>(((Module)modules.toArray()[i]).module,time);
        }
    }
    void TimePerUsers(){//функция предполагает вызов двух предыдущих
        for(int i=0;i<modules.size();i++){
            for(int j=0;j<modules.size();j++)
            if(pairTime[i].equals(pairUser[j])){

            }
        }

    }
}


