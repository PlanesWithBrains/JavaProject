import com.google.gson.Gson;
import com.sun.security.ntlm.Client;

import java.io.InputStream;
import java.net.*;
import java.lang.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.*;

public class Server {

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
            ServerStart(8012);

            for (int i = 0; i < 100; i++) {
                String json = SendJson();

                log.log(Level.INFO, "Sent json number " + i + " " + json.substring(0, json.indexOf("}")) + " ...");

                try {
                    while (connection.getInputStream().read() != 1) ;

                    if (VerifyingData && i % 2 == 0) {
                        byte[] hash = ByteBuffer.allocate(4).putInt(json.hashCode()).array();

                        log.log(Level.INFO, "Hash " + json.hashCode());
                        connection.getOutputStream().write(hash);
                    }
                } catch (Exception e) {
                    log.log(Level.INFO, "Error occured during waiting");
                    return;
                }
            }
            WriteTerminator();
            ServerClose();

        }
    }
}
