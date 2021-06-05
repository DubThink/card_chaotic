package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkClientHandler extends NetworkEventTransceiver{
    Socket socket;
    NetClientHandshake clientHandshake;
    NetServerHandshake serverHandshake;

    boolean synced;

    public int getClientUID() {
        return clientUID;
    }

    int clientUID;

    public NetworkClientHandler(Socket socket) {
        this.socket = socket;
        clientHandshake = null;
        serverHandshake = null;
        this.synced=false;
    }

    @Override
    public void run() {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // waiting for handshake from Client
            int connectionEstablishTimeout=0;
            while(dis.available()==0 && connectionEstablishTimeout<2000){
                connectionEstablishTimeout+=10;
                Thread.sleep(10);
            }
            if(connectionEstablishTimeout>=2000){
                System.err.println("Timed out waiting for client handshake");
                return;
            }
            System.out.println("beat");
            synchronized (this) {
                clientHandshake = new NetClientHandshake(dis);
                System.out.println("Received "+clientHandshake);
            }

            while(needsHandshake()){
                Thread.sleep(1);
            }

            serverHandshake.serialize(dos);
            clientUID = serverHandshake.clientID;
            System.out.println("Sending handshake "+serverHandshake);


            transceiverLoop(dis, dos);
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
    protected boolean preprocessTxEvent(NetEvent e) {
        if(e.authorID==NetEvent.LOCAL_USER)
            e.authorID = NetEvent.SERVER_USER;
        return true;
    }

    @Override
    protected boolean preprocessRxEvent(NetEvent e) {
        e.authorID = clientUID;
        return true;
    }

    public synchronized boolean needsHandshake(){
        return clientHandshake != null;
    }

    public synchronized NetClientHandshake getClientHandshake() {
        return clientHandshake;
    }

    public synchronized void replyServerHandshake(NetServerHandshake handshake){
        assert serverHandshake == null;
        clientHandshake = null;
        serverHandshake = handshake;
    }

    public void setSynced(boolean v){
        synced = v;
    }

    public boolean isSynced() {
        return synced;
    }

    @Override
    public boolean isReady() {
        return super.isReady() && isSynced();
    }

    @Override
    public String toString() {
        return "NetworkClientHandler{uid="+clientUID+"}";
    }

    public String describeSocket(){
        return socket.toString();
    }
}
