package network.event;

import Gamestate.CardDefinition;
import network.NetEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static Client.ClientEnvironment.cardDefinitionManager;
import static network.NetEventTypeID.ADD_FLIGHT;
import static network.NetEventTypeID.INTRO_CARD;

public class AddFlightNetEvent extends NetEvent {
    public CardDefinition cardDefinition;

    public AddFlightNetEvent(CardDefinition cardDefinition) {
        this.cardDefinition = cardDefinition;
    }

    public AddFlightNetEvent(DataInputStream dis) throws IOException {
        super(dis);
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        dos.writeInt(cardDefinition.uid);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        super.deserialize(dis);
        cardDefinition = cardDefinitionManager.getDefinition(dis.readInt());
    }

    @Override
    public int eventTypeIdentifier() {
        return ADD_FLIGHT;
    }

    @Override
    public String toString() {
        return "AddFlightNetEvent(#"+serial+", @"+authorID+"){" +
                "cardDefinition=" + cardDefinition +
                '}';
    }
}
