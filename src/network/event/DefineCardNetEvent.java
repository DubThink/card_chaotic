package network.event;

import Gamestate.CardDefinition;
import Gamestate.Player;
import network.NetEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static network.NetEventTypeID.DEFINE_CARD;
import static network.NetEventTypeID.PLAYER_JOIN;

public class DefineCardNetEvent extends NetEvent {
    public CardDefinition cardDefinition;

    public DefineCardNetEvent(CardDefinition cardDefinition) {
        this.cardDefinition = cardDefinition;
    }

    public DefineCardNetEvent(DataInputStream dis) throws IOException {
        super(dis);
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        cardDefinition.serialize(dos);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        super.deserialize(dis);
        cardDefinition = new CardDefinition(dis);
    }

    @Override
    public int eventTypeIdentifier() {
        return DEFINE_CARD;
    }

    @Override
    public String toString() {
        return "DefineCardNetEvent(#"+serial+", @"+authorID+"){" +
                "cardDefinition=" + cardDefinition +
                '}';
    }
}
