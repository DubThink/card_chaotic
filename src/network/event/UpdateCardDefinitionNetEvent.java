package network.event;

import Gamestate.CardDefinition;
import network.NetEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static network.NetEventTypeID.DEFINE_CARD;
import static network.NetEventTypeID.UPDATE_CARD_DEFINITION;

public class UpdateCardDefinitionNetEvent extends NetEvent {
    public CardDefinition cardDefinition;

    public UpdateCardDefinitionNetEvent(CardDefinition cardDefinition) {
        this.cardDefinition = cardDefinition;
    }

    public UpdateCardDefinitionNetEvent(DataInputStream dis) throws IOException {
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
        return UPDATE_CARD_DEFINITION;
    }

    @Override
    public String toString() {
        return "UpdateCardDefinitionNetEvent(#"+serial+", @"+authorID+"){" +
                "cardDefinition=" + cardDefinition +
                '}';
    }
}
