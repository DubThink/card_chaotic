package Gamestate;

import network.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Player extends NetSerializable {
    public final int uid;
    public String username;
    public CardStack hand;

    public Player(int uid, String username) {
        this.username = username;
        this.uid = uid;
        hand = new CardStack();
    }

    public Player(DataInputStream dis) throws IOException {
        super(dis);
        uid = dis.readInt();
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeUTF(username);
        dos.writeInt(uid);
        hand.serialize(dos);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        // uid = dis.readInt(); // in constructor since field is final
        username = dis.readUTF();
        hand = new CardStack(dis);
    }
}
