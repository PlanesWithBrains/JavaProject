package sample;

import Controllers.StatisticController;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Statistic {
    static ArrayList<ClientData> client;
    static HashSet<Module> modules = new HashSet<Module>();
    static HashSet<String> modulesName = new HashSet<String>();
    static HashSet<String> address = new HashSet<>();
    public static Pairs[] pairUser;
    public static Pairs[] pairTime;
    public static Pairs[] pairTU;
    public static Pairs[] pairAdress; // pairUser - для функции SortingByUsers, pairTime - TimePerModule, pairTU - Time per Users

    public Statistic(ArrayList<ClientData> client){
        this.client = client;
        NumberofModules();
        SortingByUsers();
        TimePerModule();
        TimePerUsers();
        UsersPerCities();
    }
    void SortStatisticPairs(int n,Pairs obj[]){
            for (int gap = n / 2; gap > 0; gap /= 2) {
                for (int i = gap; i < n; i += 1) {
                    if(obj[i]._2 instanceof Integer) {
                        Pairs<String, Integer> temp;
                        temp = obj[i];
                        int j;
                        for (j = i; j >= gap && (int)obj[j - gap]._2 < temp._2; j -= gap)
                            obj[j] = obj[j - gap];
                        obj[j] = temp;
                    }
                    if(obj[i]._2 instanceof Duration){
                        Pairs<String,Duration> temp;
                        temp = obj[i];
                        int j;
                        for (j = i; j >= gap && ((Duration)obj[j - gap]._2).minus(temp._2).isNegative(); j -= gap)
                            obj[j] = obj[j - gap];
                        obj[j] = temp;
                    }
                }
            }

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
        SortStatisticPairs(pairUser.length,pairUser);
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
        SortStatisticPairs(pairTime.length,pairTime);
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
        SortStatisticPairs(pairTU.length,pairTU);
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
        SortStatisticPairs(pairAdress.length,pairAdress);
    }
    public static void RangeSelection(Object a,Object b,Pairs obj[]){// a - нижняя граница, b - верхняя, Pairs - тот график для которого
        // Что это и как это юзать:
        // Функция отбора по диапазону, как прикрутить в гуй вам лучше знать
        // если нету верхней или нижней границы кидать на вход параметра null (с дюрейшен на вводе придется по ебаться(скорее всего по формату конструктора))
        // функция перезаписывает исходный массив с отобранными элементами
        // Моя идея реализации в гуе(но это может быть неосуществимый бред):
        // Сделать кнопку(?) на каждом окне графика xchart'a(теперь уже Саша переписал графики в javafx)
        // после ее нажатия появляется окошко для ввода диапазонов, если ничего не введенно в консоль (один из параметров)
        // вернуть на его место null, если не введены оба - вывести сообщение об ошибке.
        // Соответственно на каком из графиков была ткнута кнопка, то массив задающий этот график поступает сюда на вход
        // (по поводу массивов какой-куда кидать - пиши мне)
        // ФУНКЦИЯ НЕ ПРОВЕРЯЛАСЬ (за неимением таковой возможности)
        // (Ввод Duration'a можно реализовать(?) так как у нас сделано со временем в КПО)
        // пример того как ее можно вызвать: строка 71 данного файла(имеется в виду туда прописать вызов, с необходимыми дюрейшенами)
        boolean flag = true;
        Pairs<Object,Object>[] Obj = new Pairs[obj.length];// временный массив
        if (a == null && b!= null){
            if (obj[0]._2 instanceof Integer && b instanceof Integer){
                for(int i=0,j = 0;i<obj.length;i++){
                    if((int)obj[i]._2 < (int)b) {
                        Obj[j] = new Pairs<Object, Object>(obj[i]._1, obj[i]._2);
                        j++;
                    }
                }
            }
            if (obj[0]._2 instanceof Duration && b instanceof Duration){
                for(int i=0,j = 0;i<obj.length;i++){
                    if(((Duration)obj[i]._2).minus((Duration)b).isNegative()) {
                        Obj[j] = new Pairs<Object, Object>(obj[i]._1, obj[i]._2);
                        j++;
                    }
                }
            }
            if (!(b instanceof Duration || b instanceof Integer ))
                System.out.println("ошибка соответствия типов (b)");
        }
        if (b == null && a!=null) {
            if (obj[0]._2 instanceof Integer && a instanceof Integer) {
                for (int i = 0, j = 0; i < obj.length; i++) {
                    if ((int) obj[i]._2 > (int) a) {
                        Obj[j] = new Pairs<Object, Object>(obj[i]._1, obj[i]._2);
                        j++;
                    }
                }
            }
            if (obj[0]._2 instanceof Duration && a instanceof Duration) {
                for (int i = 0, j = 0; i < obj.length; i++) {
                    if (!((Duration) obj[i]._2).minus((Duration) a).isNegative()) {
                        Obj[j] = new Pairs<Object, Object>(obj[i]._1, obj[i]._2);
                        j++;
                    }
                }
            }
            if (!(a instanceof Duration || a instanceof Integer )) {
                System.out.println("ошибка соответствия типов(a)");
                StatisticController.addConsoleLog("ошибка соответствия типов(a)");
            }
        }
        if (a != null && b != null){
            if (obj[0]._2 instanceof Duration && (a instanceof Duration && b instanceof Duration)) {
                for (int i = 0,j=0; i < obj.length; i++) {
                    if (!((Duration) obj[i]._2).minus((Duration) a).isNegative() && ((Duration) obj[i]._2).minus((Duration) b).isNegative()) {
                        Obj[j] = new Pairs<Object, Object>(obj[i]._1, obj[i]._2);
                        j++;
                    }
                }
            }
            if (obj[0]._2 instanceof Integer && (a instanceof Integer && b instanceof Integer)) { //
                for (int i = 0,j=0; i < obj.length; i++) {
                    if ((int) obj[i]._2 > (int) a && (int)obj[i]._2 < (int)b) {
                        Obj[j] = new Pairs<Object, Object>((Object)obj[i]._1, (Object)obj[i]._2);
                        j++;
                    }
                }
            }
            if(!(a instanceof Duration || a instanceof Integer ) && !(b instanceof Duration || b instanceof Integer)) {
                System.out.println("ошибка соответствия типов(a && b)");
                StatisticController.addConsoleLog("ошибка соответствия типов(a && b)");
            }
        }
        if (a == null && b == null){
            System.out.println("Не правильное считывание данных (оба null)");
            flag = false;
        }
        if (flag) {
            Arrays.fill(obj, null);// очищяем исходный массив
            for (int i = 0; i < Obj.length; i++) {
                if(Obj[i] != null)
                    obj[i] = Obj[i];
            }
        }
    }
}


