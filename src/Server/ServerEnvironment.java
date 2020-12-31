package Server;

import Gamestate.Player;
import UI.UIBase;
import network.NetEvent;
import network.NetworkClientHandler;

import java.util.ArrayList;

public class ServerEnvironment {
    public static ArrayList<NetworkClientHandler> jipHandlers;
    public static ArrayList<SvPlayer> svPlayers;
    public static CardSourceManager cardSourceManager;
    public static UIBase uiRoot;

    private static int nextPlayerUID=1;

    static {
        svPlayers = new ArrayList<>();
        jipHandlers = new ArrayList<>();
    }

    public static int getPlayerCount(){
        int ct=0;
        for(SvPlayer svPlayer: svPlayers) {
            if (svPlayer.active)
                ct++;
        }
        return ct;
    }


    public static int getPlayerUIDByUsername(String s){
        for(SvPlayer svPlayer: svPlayers){
            if(svPlayer.player!=null && svPlayer.player.username.equals(s))
                return svPlayer.player.uid;
        }
        return -1;
    }

    public static void clientConnect(NetworkClientHandler handler){
        jipHandlers.add(handler);
    }

    public static SvPlayer playerConnect(String name, NetworkClientHandler handler){
        jipHandlers.remove(handler);
        int existingPlayerIndex = getPlayerUIDByUsername(name);
        if(existingPlayerIndex==-1){
            SvPlayer newPlayer = new SvPlayer(new Player(nextPlayerUID++,name), handler);
            svPlayers.add(newPlayer);
            return newPlayer;
        } else {
            // TODO check that the previous handler isn't still connected
            getPlayerByUID(existingPlayerIndex).handler.interrupt();
            getPlayerByUID(existingPlayerIndex).handler = handler;
            return getPlayerByUID(existingPlayerIndex);
        }


    };

    public static SvPlayer getPlayerByUID(int uid){
        return svPlayers.get(uid-1);
    }

    public static void broadcast(NetEvent event, boolean reflect){
        for (SvPlayer player: svPlayers){
            if(!player.handler.isReady())
                continue;
            if(reflect||player.handler.getClientUID()!=event.authorID)
                player.handler.sendEvent(event);
        }
    }
}
