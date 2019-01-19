import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.*;
import java.lang.*;
import java.nio.ByteBuffer;
import java.util.logging.*;

public class Server{

    public static boolean FAST = false;
    static ServerSocket server;
    static Socket connection;
    static Logger log = Logger.getLogger(Server.class.getName());
    static boolean VerifyingData = false;
    static boolean ServerStart(int port){
        try {
            server = new ServerSocket(port);
        }
        catch (Exception e){
            log.log(Level.INFO, "server failed to start");
            return false;
        }

        try {
            log.log(Level.INFO, "Waiting for connection");
            connection = server.accept();
            if(connection.getInputStream().read() == 2)
                VerifyingData = true;
            else
                VerifyingData = false;
        }
        catch (Exception e){
            return false;
        }

        log.log(Level.INFO, "Connection from " + connection.toString());
        return true;
    }

    static String SendJson(){
        String jsonClient = JsonWork.GenJsonClientData();
        try {
            connection.getOutputStream().write(jsonClient.getBytes());
        }
        catch (Exception e) {
        }
        return jsonClient;
    }
    static void ServerClose(){
        try {
            server.close();
        } catch (Exception e) {}
    }
    static void WriteTerminator(){
        try {
            connection.getOutputStream().write(0);
        } catch (Exception e) {}
    }
    public static void main(String[] args) {
        while (true) {
            Server.ServerStart(8010);
            StringBuilder file_content = new StringBuilder();

            for (int i = 0; i < 10; i++) {
                String json = Server.SendJson();
                file_content.append(json);
                file_content.append(System.getProperty("line.separator"));
                Server.log.log(Level.INFO, "Sent json number " + i + " " + json.substring(0, json.indexOf("}")) + " ...");
                try {
                    while (Server.connection.getInputStream().read() != 1) ;

                    if (Server.VerifyingData && i % 2 == 0) {
                        byte[] hash = ByteBuffer.allocate(4).putInt(json.hashCode()).array();

                        Server.log.log(Level.INFO, "Hash " + json.hashCode());
                        Server.connection.getOutputStream().write(hash);
                    }
                } catch (Exception e) {
                    Server.log.log(Level.INFO, "Error occured during waiting");
                    return;
                }
            }
            Server.WriteTerminator();
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("server_out_"+file_content.hashCode()+".json"));
                writer.write(file_content.toString());
                writer.close();
            }
            catch (Exception e){}
            Server.ServerClose();
        }

    }
}