package network.event;

import Gamestate.Player;
import network.NetEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static network.NetEventTypeID.PLAYER_JOIN;

public class PlayerLeaveNetEvent extends NetEvent {
    public int playerUID;

    public PlayerLeaveNetEvent(int playerUID) {
        this.playerUID = playerUID;
    }

    public PlayerLeaveNetEvent(DataInputStream dis) throws IOException {
        super(dis);
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        dos.writeInt(playerUID);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        super.deserialize(dis);
        playerUID = dis.readInt();
    }

    @Override
    public int eventTypeIdentifier() {
        return PLAYER_JOIN;
    }

    @Override
    public String toString() {
        return "PlayerLeave[authorID="+authorID+", playerUID=\""+playerUID+"\"]";
    }
}
