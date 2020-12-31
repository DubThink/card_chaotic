package network;

import network.event.ChatMessageNetEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class NetworkEventTransceiver extends Thread {
    BlockingQueue<NetEvent> outgoingEvents;
    BlockingQueue<NetEvent> incomingEvents;

    boolean ready;

    public NetworkEventTransceiver() {
        outgoingEvents=new LinkedBlockingQueue<>();
        incomingEvents=new LinkedBlockingQueue<>();
        ready=false;
    }

    void transceiverLoop(DataInputStream dis, DataOutputStream dos) throws Exception {
        ready = true;
        while (true) {
            while (!outgoingEvents.isEmpty()) {
                NetEvent event = outgoingEvents.poll(5, TimeUnit.MILLISECONDS);
                if (event == null) continue;
                System.out.println("Sending "+event.toString());
                dos.writeInt(event.eventTypeIdentifier());
                event.serialize(dos);
            }

            if (dis.available() > 0) {
                int id = dis.readInt();
                NetEvent rcvd=null;
                switch (id) {
                    case NetEventTypeID.CHAT_MESSAGE:
                        rcvd = new ChatMessageNetEvent(dis);
                        break;
                    default:
                        throw new RuntimeException("how " + id);
                }
                if(preprocessReceivedEvent(rcvd)) {
                    System.out.println("Received:" + rcvd.toString());
                    incomingEvents.add(rcvd);
                }
            }
        }
    }

    protected boolean preprocessReceivedEvent(NetEvent e){
        return true;
    }

    public void sendEvent(NetEvent e){
        if(!isReady())
            throw new RuntimeException("Don't do that");
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
}
