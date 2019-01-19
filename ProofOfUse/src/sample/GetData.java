package sample;

import java.io.InputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.logging.Level;

public class GetData {
    static LoggingMachine log;

    static  Socket  connect2Server;
    static  boolean connectionEstablished = false;
    static  boolean VerifyData = false;             //TODO исправить проверку
    static  String  previousClient;
    static  int     RecievedCount = 0;
    static public ClientData Download(String IP, int port){
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
            try {
                if(VerifyData)
                    connect2Server.getOutputStream().write(2);
                else
                    connect2Server.getOutputStream().write(-1);
            }
            catch (Exception E){
                log.log(Level.INFO, "VerifyData response failed");
            }
        }
        catch (Exception e){
            log.log(Level.INFO, "receiving input stream failed");
            return null;
        }

        String clientSerialized;

        try {
            byte[] json = new byte[connect2Server.getReceiveBufferSize()];
            istream.read(json);
            RecievedCount++;
            if(json[0] == 0)
                return null;
            clientSerialized = new String(json, Charset.forName("UTF-8"));

            if(RecievedCount%2 == 0)
                previousClient = clientSerialized;
        }
        catch (Exception e){
            log.log(Level.INFO, "stream reading or receiving buffer failed");
            return null;
        }

        return  JsonWork.Deserialize(clientSerialized);
    }

    static  boolean[] Next(){
        boolean identical = false;
        try {
            connect2Server.getOutputStream().write(1);
            if(VerifyData) {
                byte[] raw_hash = new byte[connect2Server.getReceiveBufferSize()];
                connect2Server.getInputStream().read(raw_hash);
                ByteBuffer bb = ByteBuffer.wrap(raw_hash);

                int recieved_hash = bb.order(ByteOrder.LITTLE_ENDIAN).getInt();
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

    static void BreakConnection(){
        connectionEstablished = false;
    }
}
