package Gamestate;

import Gamestate.Gameobjects.GOUType;
import Gamestate.Gameobjects.GameObjectUpdate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GOUCard extends GameObjectUpdate {
    public Card card;
    public int actionType;

    public GOUCard(Card card, int actionType) {
        this.card = card;
        this.actionType = actionType;
    }

    public GOUCard(DataInputStream dis) throws IOException {
        card = new Card(dis);
        actionType = dis.readInt();
    }

    public int eventTypeIdentifier() {
        return GOUType.CARD;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        card.serialize(dos);
        dos.writeInt(actionType);
    }

    @Override
    public String toString() {
        return "GOUCard{" +
                "card=" + card +
                ", actionType=" + actionType +
                '}';
    }
}
