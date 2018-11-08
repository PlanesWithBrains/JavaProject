import sun.awt.image.ImageWatched;

import java.lang.*;
import java.util.logging.*;
import java.util.*;

/*Класс-обертка над коллекциями, которая принимает любой тип коллекции*/
/*Но методы будут выполняться только если тип ArrayList<> или HashMap<>*/
public class DataCollection<T> {
    /*log - оберточный лог
    * collection - какая-то коллекция типа <T>
    * collection_class - переменная, содержащая информацию о типе коллекции*/
    LoggingMachine log;
    T collection;
    Class collection_class;

    /*Конструктор инициализирует поля класса*/
    DataCollection(T outer_collection){
        collection = outer_collection;
        collection_class = outer_collection.getClass();
        log = new LoggingMachine(DataCollection.class ,true);
    }

    public long[] Add(Object... arg){

        /*/-------Замер времени начат-------\*/

        long stop_ns,start_ns = 0;
        long stop_ms,start_ms = 0;
        /*проверяем, что collection это HashMap*/
        if(collection instanceof HashMap) {
            start_ms = System.currentTimeMillis();
            start_ns = System.nanoTime();
            /*arg[0] - key, arg[1] - value*/
            ((HashMap) collection).put(arg[0], arg[1]);
        }


        if(collection instanceof LinkedList) {
            start_ms = System.currentTimeMillis();
            start_ns = System.nanoTime();
            /*arg[0] - value*/
            ((LinkedList) collection).add(arg[0]);
        }

        if(collection instanceof ArrayList) {
            start_ms = System.currentTimeMillis();
            start_ns = System.nanoTime();
            /*arg[0] - value*/
            ((ArrayList) collection).add(arg[0]);
        }

        stop_ns = System.nanoTime();
        stop_ms = System.currentTimeMillis();
        long elapsed_ms = stop_ms - start_ms;
        long elapsed_ns = stop_ns - start_ns;

        /*\-------Замер врмени окончен-------/*/
        try {
            log.log(Level.INFO, "Added element to " + collection.getClass().getTypeName() + " (id " + arg[0].hashCode() + ")[" +
                    collection_class.cast(collection).getClass().getMethod("size").invoke(collection) + "] elapsed time: " +
                    elapsed_ms + "ms " + elapsed_ns + "ns");
        }
        catch (Exception e){
            log.log(Level.WARNING,"~~Information about time is unavailable~~");
        }

        /*возвращаем посчитанное время - пригодится для графиков*/
        return new long[]{elapsed_ms,elapsed_ns};
    }

    public long[] GetAndAdd(Object key, Object value){

        /*/-------Замер времени начат-------\*/
        long stop_ns,start_ns;
        long stop_ms,start_ms;
        start_ms = System.currentTimeMillis();
        start_ns = System.nanoTime();

        /*проверяем, что collection это HashMap, получаем ArrayList и добавляем к нему value*/
        if(collection instanceof HashMap)
            ((ArrayList)((HashMap) collection).get(key)).add(value);
        stop_ns = System.nanoTime();
        stop_ms = System.currentTimeMillis();
        long elapsed_ms = stop_ms - start_ms;
        long elapsed_ns = stop_ns - start_ns;

        /*\-------Замер врмени окончен-------/*/
        try {
            log.log(Level.INFO, "Added element to ArrayList (" + ((ClientData) value).hashCode() + ")["
                    + ((ArrayList) ((HashMap) collection).get(key)).size() + "] elapsed time: " +
                    elapsed_ms + "ms " + elapsed_ns + "ns");
        }
        catch (Exception e){
            log.log(Level.WARNING,"~~Information about time is unavailable~~");
        }
        /*возвращаем посчитанное время - пригодится для графиков*/
        return new long[]{elapsed_ms,elapsed_ns};
    }

    public long[] Remove(Object key){

        /*/-------Замер времени начат-------\*/
        long stop_ns,start_ns = 0;
        long stop_ms,start_ms = 0;

        /*проверяем, что collection это HashMap и удаляем пару <key,ArrayList<>>*/
        if(collection instanceof HashMap) {
            start_ms = System.currentTimeMillis();
            start_ns = System.nanoTime();
            ((HashMap) collection).remove(key);
        }

        if(collection instanceof LinkedList) {
            start_ms = System.currentTimeMillis();
            start_ns = System.nanoTime();
            ((LinkedList) collection).remove();
        }

        stop_ns = System.nanoTime();
        stop_ms = System.currentTimeMillis();

        long elapsed_ms = stop_ms - start_ms;
        long elapsed_ns = stop_ns - start_ns;

        /*\-------Замер врмени окончен-------/*/
        log.log(Level.INFO,"Removed element from " + collection.getClass().getTypeName() + " (id " + key.hashCode() + ")[" +
                + 1 + "] elapsed time: " +
                elapsed_ms + "ms "  + elapsed_ns + "ns");

        /*возвращаем посчитанное время - пригодится для графиков*/
        return new long[]{elapsed_ms,elapsed_ns};
    }

}

