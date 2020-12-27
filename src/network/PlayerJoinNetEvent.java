package network;

import Gamestate.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static network.NetEventTypeID.PLAYER_JOIN;

public class PlayerJoinNetEvent extends NetEvent {
    public Player player;

    public PlayerJoinNetEvent(Player player) {
        this.player = player;
    }

    public PlayerJoinNetEvent(DataInputStream dis) throws IOException {
        super(dis);
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        player.serialize(dos);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        super.deserialize(dis);
        player = new Player(dis);
    }

    @Override
    public int eventTypeIdentifier() {
        return PLAYER_JOIN;
    }

    @Override
    public String toString() {
        return "PlayerJoin[authorID="+authorID+", player=\""+player+"\"]";
    }
}
