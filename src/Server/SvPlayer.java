package Server;

import Gamestate.Player;
import network.NetworkClientHandler;

public class SvPlayer {
    public Player player;
    public NetworkClientHandler handler;
    public boolean wasReady = false;
    public boolean active = false;

    public int currentNewCardID=-1;

    public SvPlayer(Player player, NetworkClientHandler handler) {
        this.player = player;
        this.handler = handler;
    }
}
