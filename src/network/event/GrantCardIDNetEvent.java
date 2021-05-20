package network.event;

import network.NetEvent;
import network.NetEventTypeID;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GrantCardIDNetEvent extends NetEvent {
    public int id;

    public GrantCardIDNetEvent(int id) {
        this.id = id;
    }

    public GrantCardIDNetEvent(DataInputStream dis) throws IOException {
        super(dis);
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        dos.writeInt(id);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        super.deserialize(dis);
        id = dis.readInt();

    }

    @Override
    public int eventTypeIdentifier() {
        return NetEventTypeID.GRANT_CARD_ID;
    }

    @Override
    public String toString() {
        return "GrantCardIDNetEvent(#"+serial+", @"+authorID+"){" +
                "id='" + id + '\'' +
                '}';
    }
}
