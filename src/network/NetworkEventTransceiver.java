package network;

import core.ExceptionNotify;
import network.event.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class NetworkEventTransceiver extends Thread {
    public static final int MILLIS_BETWEEN_KEEPALIVES = 3000;
    public static final int KEEPALIVE_TIMEOUT_MILLIS = 15000;

    public int millisSinceLastSend;
    public int millisSinceLastReceive;

    BlockingQueue<NetEvent> outgoingEvents;
    BlockingQueue<NetEvent> incomingEvents;

    Socket socket;

    boolean ready;
    public boolean connectionDropped;

    public ExceptionNotify notifyConnectionFailed;

    public NetworkEventTransceiver() {
        outgoingEvents=new LinkedBlockingQueue<>();
        incomingEvents=new LinkedBlockingQueue<>();
        ready=false;
    }

    void transceiverLoop(DataInputStream dis, DataOutputStream dos) throws Exception {
        ready = true;
        try {
            while (true) {
                while (!outgoingEvents.isEmpty()) {
                    NetEvent event = outgoingEvents.poll(5, TimeUnit.MILLISECONDS);
                    if (event == null) continue;
                    if (preprocessTxEvent(event)) {
                        if(event instanceof KeepaliveNetEvent)
                            ;//System.out.println(" --> KA update "+rcvd.toString());
                        else
                            System.out.println("--> Sending:  " + event.toString());
                        dos.writeInt(event.eventTypeIdentifier());
                        event.serialize(dos);
                        millisSinceLastSend = 0;
                    }
                }

                if (dis.available() > 0) {
                    int id = dis.readInt();
                    //System.out.println("rcvid " + id);
                    NetEvent rcvd = switch (id) {
                        case NetEventTypeID.CHAT_MESSAGE -> new ChatMessageNetEvent(dis);
                        case NetEventTypeID.DEFINE_CARD -> new DefineCardNetEvent(dis);
                        case NetEventTypeID.INTRO_CARD -> new IntroCardNetEvent(dis);
                        case NetEventTypeID.ADD_FLIGHT -> new AddFlightNetEvent(dis);
                        case NetEventTypeID.PLAYER_JOIN -> new PlayerJoinNetEvent(dis);
                        case NetEventTypeID.NOTIFY_PHASE_CHANGE -> new NotifyPhaseChangeNetEvent(dis);
                        case NetEventTypeID.DEFINE_IMAGE -> new ImageNetEvent(dis);
                        case NetEventTypeID.REQUEST_CARD_ID -> new RequestCardIDNetEvent(dis);
                        case NetEventTypeID.GRANT_CARD_ID -> new GrantCardIDNetEvent(dis);
                        case NetEventTypeID.UPDATE_CARD_DEFINITION -> new UpdateCardDefinitionNetEvent(dis);
                        case NetEventTypeID.SYNC_COMPLETE -> new SyncCompleteNetEvent(dis);
                        case NetEventTypeID.KEEPALIVE -> new KeepaliveNetEvent(dis);
                        default -> throw new RuntimeException("NetEvent with ID " + id + " is unhandled.");
                    };
                    if(rcvd instanceof KeepaliveNetEvent){
                        //System.out.println(" <-- KA update "+rcvd.toString());
                    } else if (preprocessRxEvent(rcvd)) {
                        System.out.println("<-- Received: " + rcvd.toString());
                        incomingEvents.add(rcvd);
                    }
                    millisSinceLastReceive = 0;
                }
            }
        } catch (SocketException se){
            shutdown(se.getLocalizedMessage());
            if(notifyConnectionFailed!=null)
                notifyConnectionFailed.fire(se);
        }
    }

    public void shutdown(String reason){
        ready=false;
        outgoingEvents.clear();
        incomingEvents.clear();
        connectionDropped=true;
        printErrorMessage("Connection '"+this+"' dropped for reason: '"+reason+"'");

    }

    public void forciblyDisconnect(){
        try {
            socket.close();
        } catch (IOException ioException) {
            printErrorMessage("while forcibly disconnecting: '"+ioException.getLocalizedMessage()+"'");
        }
        ready=false;
        connectionDropped=true;
    }


    protected boolean preprocessTxEvent(NetEvent e){
        return true;
    }

    protected boolean preprocessRxEvent(NetEvent e){
        return true;
    }

    public void sendEvent(NetEvent e){
        if(!isReady())
            throw new RuntimeException("Don't do that");
        outgoingEvents.add(e);
    }

    /**
     * Explicit call to override the isReady() check for events sent while syncing client
     */
    public void sendSyncingEvent(NetEvent e){
        outgoingEvents.add(e);
    }

    public NetEvent pollEvent(){
        if(incomingEvents.isEmpty())
            return null;
        return incomingEvents.poll();
    }

    public boolean hasReceivedEvents(){
        return !incomingEvents.isEmpty();
    }

    public boolean isReady() {
        return ready;
    }

    protected void printErrorMessage(String s) {
        System.err.println(s);
    }

    protected void printMessage(String s) {
        System.out.println(s);
    }

    public void updateTimeouts(int dt){
        if(!isReady())
            return;
        millisSinceLastReceive+=dt;
        millisSinceLastSend+=dt;
        if(millisSinceLastSend > MILLIS_BETWEEN_KEEPALIVES){
            sendEvent(new KeepaliveNetEvent());
            millisSinceLastSend=0;
        }
        if(millisSinceLastReceive > KEEPALIVE_TIMEOUT_MILLIS){
            printErrorMessage("ping timeout reached, forcibly disconnecting "+this.toString());
        }
    }
}
