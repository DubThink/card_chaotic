package network.event;

import network.NetEvent;
import network.NetEventTypeID;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class KeepaliveNetEvent extends NetEvent {

    public KeepaliveNetEvent() {}

    public KeepaliveNetEvent(DataInputStream dis) throws IOException {
        super(dis);
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        super.deserialize(dis);

    }

    @Override
    public int eventTypeIdentifier() {
        return NetEventTypeID.KEEPALIVE;
    }

    @Override
    public String toString() {
        return "KeepAliveNetEvent(#"+serial+", @"+authorID+"){}";
    }
}
