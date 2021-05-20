package network;

import Gamestate.ClientGamestate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import static Client.ClientEnvironment.sysMessage;
import static network.NetEvent.LOCAL_USER;

public class NetworkClient extends NetworkEventTransceiver {

    int clientUID;
    Socket socket;

    @Override
    public void run() {
        try {
            InetAddress ip = InetAddress.getByName("localhost");

            sysMessage("Connecting to "+ip.getHostAddress()+"...");

            socket = new Socket(ip, 5056);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            NetClientHandshake clientHandshake = new NetClientHandshake(ClientGamestate.username);
            clientHandshake.serialize(dos);

            System.out.println("Sending handshake "+clientHandshake);
            int connectionEstablishTimeout=0;
            while(dis.available()==0 && connectionEstablishTimeout<2000){
                connectionEstablishTimeout+=10;
                Thread.sleep(10);
            }
            if(connectionEstablishTimeout>=2000){
                System.out.println("Timed out waiting for server handshake");
                return;
            }
            System.out.println("beat");
            NetServerHandshake serverHandshake = new NetServerHandshake(dis);
            System.out.println("Received "+serverHandshake);

            if(serverHandshake.success) {
                clientUID = serverHandshake.clientID;
                sysMessage("Connected");
                transceiverLoop(dis, dos);
            } else {
                System.out.println(serverHandshake.message);
                sysMessage("Error: "+serverHandshake.message);
            }

            //dis.close();
            //dos.close();
        } catch (RuntimeException runtimeException){
            runtimeException.printStackTrace();
            System.err.println(runtimeException.toString());
            throw runtimeException;
        } catch (Exception e){
            System.err.println(e.toString());
            e.printStackTrace();
            interrupt();
        } finally {
            try {
                socket.close();
            } catch (IOException ioException){
                System.err.println("While handling error, :"+ioException);
            }
        }
    }

    @Override
    protected boolean preprocessRxEvent(NetEvent e) {
        if(e.authorID==clientUID)
            e.authorID = LOCAL_USER;
        return true;
    }

    @Override
    public String toString() {
        return "NetworkClient{}";
    }

    public int getClientUID() {
        return clientUID;
    }
}
