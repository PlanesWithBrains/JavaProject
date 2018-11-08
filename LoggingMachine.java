import java.io.*;
import java.lang.*;
import java.lang.reflect.Type;
import java.util.logging.*;
import java.util.*;

public class LoggingMachine {
    /*  Log - Стандартный логгер
    *   textLog - Строка, повсторяющая сообщения логгера - она выведется в файл
    *   fs - Файловый поток
    *   number_of_entries - Кол-во вызовов, и, соответственно, сообщений
    *   make_log - включает/выключает логгирование*/
    Logger Log;
    StringBuilder textLog;
    FileOutputStream fs;
    long number_of_entries;
    static boolean make_log;

    /*Обертка над стандартным логом*/
    void log(Level lvl, String message){
        if(make_log) {
            Log.log(lvl, message);
            number_of_entries++;
        }
    }

    /*Обертка над стандартным логом*/
    void info(String message){
        if(make_log)
            Log.info(message);
    }

    /*Конструктор принемает тип класса, для которого будет производиться логгирование
    * (именно тип - информация о полях, методах, вложенных классах и тп)*/
    LoggingMachine(Type Class, boolean do_logging){
        make_log = do_logging;

        Log = Logger.getLogger(Class.getTypeName());
        StringBuilder textLog = new StringBuilder();

        /*Хэндлер автоматически заносит в textLog данные при вызове обертки методов Log*/
        /*Внутри определены методы для: 1) Записи лога
                                        2) Сброса потока лога (?)
                                        3) Закрытия лога*/
        Handler textLog_handle = new Handler() {

            @Override
            public void publish(LogRecord logRecord) {
                if(make_log) {
                    textLog.append(logRecord.getLevel());
                    textLog.append('\t');
                    textLog.append(new Date(logRecord.getMillis()).toString());
                    textLog.append('\t');
                    textLog.append(logRecord.getMessage());
                    textLog.append(System.getProperty("line.separator"));
                }
            }

            @Override
            public void flush() {
                return;
            }

            @Override
            public void close() throws SecurityException {
                try{
                    if(make_log) {
                        fs = new FileOutputStream(Class.getTypeName() + "_log" + ".txt", true);
                        fs.write(textLog.toString().getBytes());
                    }
                }
                catch(Exception e){
                    log(Level.SEVERE, "Writing log file failed");
                }
            }
        };

        /*Подключаем хэндлер к стандартному логгеру*/
        Log.addHandler(textLog_handle);
    }

    static void revertChanges(){
        make_log = false;
    }

}
