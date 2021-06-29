package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class NetEvent extends NetSerializable {
    public static final int LOCAL_USER = -1;
    public static final int SERVER_USER = 0;
    public final int serial;
    public int authorID = LOCAL_USER;
    private static int nextSerial = 0;

    public NetEvent(){serial=nextSerial++;};
    public NetEvent(DataInputStream dis) throws IOException {
        serial=dis.readInt();
        deserialize(dis);
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException{
        dos.writeInt(serial);
        dos.writeInt(authorID);
    }

    protected void deserialize(DataInputStream dis) throws IOException {
        authorID = dis.readInt();
    }
    public abstract int eventTypeIdentifier();

    @Override
    public abstract String toString();
}
