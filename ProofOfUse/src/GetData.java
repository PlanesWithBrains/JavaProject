import com.google.gson.Gson;
import com.sun.security.ntlm.Client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.lang.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.*;

public class GetData {
    static LoggingMachine log;

    static  Socket  connect2Server;
    static  boolean connectionEstablished = false;
    static  boolean VerifyingData = false;
    static  String  previousClient;
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
            try {
                if(VerifyingData)
                    connect2Server.getOutputStream().write(2);
                else
                    connect2Server.getOutputStream().write(0xDEAD);
            }
            catch (Exception e){
                log.log(Level.INFO, "recieving input stream for data verify failed");
                return null;
            }

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
            if(json[0] == 0)
                return null;
            clientSerialized = new String(json, Charset.forName("UTF-8"));
        }
        catch (Exception e){
            log.log(Level.INFO, "stream reading or recieving buffer failed");
            return null;
        }
        previousClient = clientSerialized;
        return  JsonWork.Deserialize(clientSerialized);
    }

    static  boolean[] Next(){
        boolean identical = false;
        try {
            connect2Server.getOutputStream().write(1);
            if(VerifyingData) {
                byte[] raw_hash = new byte[connect2Server.getReceiveBufferSize()];
                connect2Server.getInputStream().read(raw_hash);
                ByteBuffer bb = ByteBuffer.wrap(raw_hash);

                int recieved_hash = bb.getInt();
                log.log(Level.INFO, "Revieved hash: " + recieved_hash + " Hash of local: " + previousClient.hashCode());
                if (previousClient.hashCode() == recieved_hash)
                    identical = true;
            }
        }
        catch (Exception e){
            log.log(Level.INFO, "Error occured during conversation with server");
            return new boolean[]{false, false};
        }

        return new boolean[]{true,identical};
    }

    static void revertChanges(){
        VerifyingData = !VerifyingData;
    }
}
