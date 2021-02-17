package network.event;

import Gamestate.CardDefinition;
import network.NetEvent;
import network.NetEventTypeID;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static Client.ClientEnvironment.cardDefinitionManager;
import static network.NetEventTypeID.INTRO_CARD;

public class NotifyPhaseChangeNetEvent extends NetEvent {
    public int newPhase;

    public NotifyPhaseChangeNetEvent(int newPhase) {
        this.newPhase = newPhase;
    }

    public NotifyPhaseChangeNetEvent(DataInputStream dis) throws IOException {
        super(dis);
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        dos.writeInt(newPhase);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        super.deserialize(dis);
        newPhase = dis.readInt();
    }

    @Override
    public int eventTypeIdentifier() {
        return NetEventTypeID.NOTIFY_PHASE_CHANGE;
    }

    @Override
    public String toString() {
        return "NotifyPhaseChange[authorID="+authorID+", newPhase=\""+newPhase+"\"]";
    }
}
