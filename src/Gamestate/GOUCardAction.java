package Gamestate;

import Gamestate.Gameobjects.GOUType;
import Gamestate.Gameobjects.GameObjectUpdate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static Globals.DBG.Warning;

public class GOUCardAction extends GameObjectUpdate {
    public int cardIndex;
    public CardAction action;

    public GOUCardAction(int cardIndex, CardAction action) {
        this.cardIndex = cardIndex;
        this.action = action;
    }

    public GOUCardAction(DataInputStream dis) throws IOException {
        cardIndex = dis.readInt();
        int actionType = dis.readInt();
        action = switch (actionType) {
            case Card.ACTION_FLIP -> new Card.ActionFlipCard(dis);
            case Card.ACTION_CHANGE_COUNTER -> new Card.ActionChangeCounter(dis);
            default -> null;
        };
        if (action == null) {
            Warning("Unhandled card action type with id="+actionType);
        }
    }

    public int eventTypeIdentifier() {
        return GOUType.CARD_ACTION;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeInt(cardIndex);
        dos.writeInt(action.actionTypeIdentifier());
        action.serialize(dos);
    }

    @Override
    public String toString() {
        return "GOUCardAction{" +
                "cardIndex=" + cardIndex +
                ", action=" + action +
                '}';
    }
}
