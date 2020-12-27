package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class NetEvent  extends NetSerializable {
    public static final int LOCAL_USER = -1;
    public static final int SERVER_USER = 0;
    public int authorID = LOCAL_USER;

    public NetEvent(){};
    public NetEvent(DataInputStream dis) throws IOException {
        deserialize(dis);
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException{
        dos.writeInt(authorID);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        authorID = dis.readInt();
    }
    public abstract int eventTypeIdentifier();

    @Override
    public abstract String toString();
}
