package network;

import Gamestate.ClientGamestate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static Client.ClientEnvironment.sysMessage;
import static network.NetEvent.LOCAL_USER;

public class NetworkClient extends NetworkEventTransceiver {

    int clientUID;

    @Override
    public void run() {
        try {
            InetAddress ip = InetAddress.getByName("localhost");

            sysMessage("Connecting to "+ip.getHostAddress()+"...");

            Socket s = new Socket(ip, 5056);
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

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
            System.out.println("Received "+clientHandshake);

            if(serverHandshake.success) {
                clientUID = serverHandshake.clientID;
                sysMessage("Connected");
                transceiverLoop(dis, dos);
            } else {
                System.out.println(serverHandshake.message);
                sysMessage("Error: "+serverHandshake.message);
                return;
            }

            //dis.close();
            //dos.close();
        } catch (Exception e){
            System.err.println(e.toString());
            e.printStackTrace();
            interrupt();
        }
    }

    @Override
    protected boolean preprocessReceivedEvent(NetEvent e) {
        if(e.authorID==clientUID)
            e.authorID = LOCAL_USER;
        return true;
    }
}
