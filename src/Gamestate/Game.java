package Gamestate;

import network.*;
import network.event.PlayerJoinNetEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Game extends NetSerializable {
    ArrayList<Player> players;


    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        NetSerializerUtils.serializeArrayList(players, dos);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        NetSerializerUtils.deserializeArrayList(players, dis, Player::new);
    }
    
    class PlayerJoinEventHandler implements NetEventHandler<PlayerJoinNetEvent> {
        @Override
        public void handleEvent(PlayerJoinNetEvent event) {
            players.add(event.player);
        }
    }
}
