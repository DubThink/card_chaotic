package Gamestate;

import network.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Player extends NetSerializable {
    public final int uid;
    String username;

    public Player(int uid, String username) {
        this.username = username;
        this.uid = uid;
    }

    public Player(DataInputStream dis) throws IOException {
        super(dis);
        uid = dis.readInt();
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeUTF(username);
        dos.writeInt(uid);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        // uid = dis.readInt(); // in constructor since field is final
        username = dis.readUTF();
    }
}
