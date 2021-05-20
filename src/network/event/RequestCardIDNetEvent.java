package network.event;

import Gamestate.CardDefinition;
import network.NetEvent;
import network.NetEventTypeID;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static network.NetEventTypeID.DEFINE_CARD;

public class RequestCardIDNetEvent extends NetEvent {

    public RequestCardIDNetEvent() {
        super();
    }

    public RequestCardIDNetEvent(DataInputStream dis) throws IOException {
        super(dis);
    }

    @Override
    public int eventTypeIdentifier() {
        return NetEventTypeID.REQUEST_CARD_ID;
    }

    @Override
    public String toString() {
        return "RequestCardIDNetEvent(#"+serial+", @"+authorID+"){}";
    }
}
