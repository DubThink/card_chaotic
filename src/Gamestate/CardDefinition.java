package Gamestate;

import network.NetSerializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Defines the invariable portions of a card (used to generate cards)
 */
public class CardDefinition extends NetSerializable {
    public final int uid;
    public String name;

    public CardDefinition(int uid) {
        this.uid = uid;
    }

    public CardDefinition(DataInputStream dis) throws IOException {
        super(dis);
        uid = dis.readInt();
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeUTF(name);

        dos.writeInt(uid); // end cause final
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        name= dis.readUTF();
    }

    @Override
    public String toString() {
        return "CardDefinition{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                '}';
    }
}
