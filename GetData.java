import com.google.gson.Gson;
import com.sun.security.ntlm.Client;

import java.io.InputStream;
import java.net.*;
import java.lang.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.*;

public class GetData {
    static LoggingMachine log;

    static  Socket connect2Server;
    static  boolean connectionEstablished = false;
    static ClientData Download(String IP, int port){
        Inet4Address inetAddr;
        if(!connectionEstablished) {
            log = new LoggingMachine(GetData.class);
            try {
                inetAddr = (Inet4Address) Inet4Address.getByName(IP);
            } catch (Exception e) {
                log.log(Level.INFO, IP + " is invalid");
                return null;
            }

            try {
                connect2Server = new Socket(inetAddr, port);
            } catch (Exception e) {
                log.log(Level.INFO, "Connection to " + IP + " failed");
                return null;
            }
            connectionEstablished = true;
        }

        InputStream istream;
        try {
            istream = connect2Server.getInputStream();
        }
        catch (Exception e){
            log.log(Level.INFO, "recieving input stream failed");
            return null;
        }

        String clientSerialized;

        try {
            byte[] json = new byte[connect2Server.getReceiveBufferSize()];
            istream.read(json);
            clientSerialized = new String(json, Charset.forName("UTF-8"));
        }
        catch (Exception e){
            log.log(Level.INFO, "stream reading or recieving buffer failed");
            return null;
        }

        return  JsonWork.Deserialize(clientSerialized);
    }

    static  boolean Next(){
        try {
            connect2Server.getOutputStream().write(1);
        }
        catch (Exception e){
            log.log(Level.INFO, "Error occured during conversation with server");
            return false;
        }

        return true;
    }
}
