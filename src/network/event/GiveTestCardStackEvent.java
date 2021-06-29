package network.event;

import Gamestate.CardDefinition;
import Gamestate.CardStack;
import network.NetEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static Client.ClientEnvironment.cardDefinitionManager;
import static network.NetEventTypeID.GIVE_TEST_CARD_STACK;

public class GiveTestCardStackEvent extends NetEvent {
    public CardStack stack;

    public GiveTestCardStackEvent(CardStack cardStack) {
        this.stack = cardStack;
    }

    public GiveTestCardStackEvent(DataInputStream dis) throws IOException {
        super(dis);
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        stack.serialize(dos);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        super.deserialize(dis);
        stack = new CardStack(dis);
    }

    @Override
    public int eventTypeIdentifier() {
        return GIVE_TEST_CARD_STACK;
    }

    @Override
    public String toString() {
        return "GiveTestCardStackEvent(#"+serial+", @"+authorID+"){" +
                "cardStack=" + stack +
                '}';
    }
}
