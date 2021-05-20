package network.event;

import Gamestate.CardDefinition;
import network.NetEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static network.NetEventTypeID.INTRO_CARD;
import static network.NetEventTypeID.PLAYER_JOIN;

import static Client.ClientEnvironment.*;

public class IntroCardNetEvent extends NetEvent {
    CardDefinition cardDefinition;

    public IntroCardNetEvent(CardDefinition cardDefinition) {
        this.cardDefinition = cardDefinition;
    }

    public IntroCardNetEvent(DataInputStream dis) throws IOException {
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
        return INTRO_CARD;
    }

    @Override
    public String toString() {
        return "IntroCardNetEvent(#"+serial+", @"+authorID+"){" +
                "cardDefinition=" + cardDefinition +
                '}';
    }
}
