package Gamestate;

import network.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Player extends NetSerializable {
    public final int playerIndex;

    public Account account;

    public String displayName;

    public CardStack hand;

    public Player(int playerIndex, Account account, String displayName) {
        this.displayName = displayName;
        this.playerIndex = playerIndex;
        this.account = account;
        hand = new CardStack();
    }

    public Player(DataInputStream dis) throws IOException {
        super(dis);
        deserialize(dis);
        playerIndex = dis.readInt();
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeUTF(displayName);
        account.serialize(dos);

        hand.serialize(dos);

        // final
        dos.writeInt(playerIndex);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        displayName = dis.readUTF();
        account = new Account(dis);

        hand = new CardStack(dis);
    }
}
