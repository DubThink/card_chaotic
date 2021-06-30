package Gamestate;

import network.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static Server.ServerEnvironment.SERVER_ONLY;
import static network.NetEvent.LOCAL_USER;

public class Player extends NetSerializable {
    // TODO refactor for better name
    /** same thing as a clientID */
    public final int playerIndex;

    public Account account;

    public String displayName;

    public CardStack hand;

    public Player(int playerIndex, Account account, String displayName) {
        SERVER_ONLY();
        this.displayName = displayName;
        this.playerIndex = playerIndex;
        this.account = account;
        hand = new CardStack(playerIndex);
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

    private void deserialize(DataInputStream dis) throws IOException {
        displayName = dis.readUTF();
        account = new Account(dis);

        hand = new CardStack(dis);
    }

    @Override
    public String toString() {
        return "Player{" +
                "index=" + playerIndex +
                ", account=" + account +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
