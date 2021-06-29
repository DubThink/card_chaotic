package Server;

import Gamestate.Account;
import Gamestate.Player;
import Globals.Assert;
import Schema.AccountManager;
import UI.UILogView;
import UI.UIPanel;
import core.AdvancedApplet;
import network.NetEvent;
import network.NetworkClientHandler;

import java.util.ArrayList;

public class ServerEnvironment {
    public static ArrayList<NetworkClientHandler> jipHandlers;
    public static ArrayList<SvPlayer> svPlayers;
    public static CardSourceManager cardSourceManager;
    public static AccountManager accountManager;

    // server is index 0
    private static int nextPlayerIndex = 1;

    public static UIPanel phasePanel;
    public static UILogView serverlog;



    static {
        svPlayers = new ArrayList<>();
        jipHandlers = new ArrayList<>();
        cardSourceManager = new CardSourceManager();
    }

    public static boolean SERVER;
    public static void SERVER_ONLY(){
        Assert.bool(SERVER);
    }

    public static int getPlayerCount(){
        int ct=0;
        for(SvPlayer svPlayer: svPlayers) {
            if (svPlayer.active)
                ct++;
        }
        return ct;
    }

    public static SvPlayer getPlayerByIndex(int index){
        return svPlayers.get(index-1);
    }

    public static int getPlayerIndexByAccountUID(int accountUID){
        for(SvPlayer svPlayer: svPlayers){
            if(svPlayer.player!=null && svPlayer.player.account.accountUID == accountUID)
                return svPlayer.player.playerIndex;
        }
        return -1;
    }

    public static int getPlayerIndexByDisplayName(String s){
        for(SvPlayer svPlayer: svPlayers){
            if(svPlayer.player!=null && svPlayer.player.displayName.equals(s))
                return svPlayer.player.playerIndex;
        }
        return -1;
    }

    public static int getPlayerIndexByAccountName(String s){
        Account a = accountManager.getAccountByName(s);
        if(a==null)
            return -1;
        for(SvPlayer svPlayer: svPlayers){
            if(svPlayer.player!=null && svPlayer.player.account == a)
                return svPlayer.player.playerIndex;
        }
        return -1;
    }

    public static SvPlayer getPlayerByDisplayName(String s){
        int uid = getPlayerIndexByDisplayName(s);
        if(uid==-1)
            return null;
        return getPlayerByIndex(uid);
    }

    public static SvPlayer getPlayerByAccountName(String s){
        int uid = getPlayerIndexByAccountName(s);
        if(uid==-1)
            return null;
        return getPlayerByIndex(uid);
    }

    public static void clientConnect(NetworkClientHandler handler){
        jipHandlers.add(handler);
    }

    public static SvPlayer playerConnect(Account account, String displayName, NetworkClientHandler handler){
        jipHandlers.remove(handler);

        int existingPlayerIndex = getPlayerIndexByAccountUID(account.accountUID);

        if(existingPlayerIndex==-1){
            // first time this account is connecting
            SvPlayer newPlayer = new SvPlayer(new Player(nextPlayerIndex++, account, displayName), handler);
            svPlayers.add(newPlayer);
            return newPlayer;
        } else {
            // account was previously connected, reestablish
            SvPlayer existingPlayer = getPlayerByIndex(existingPlayerIndex);
            if(existingPlayer.active || existingPlayer.handler.isReady())
                throw new RuntimeException("Attempting to connect a connected player");
            existingPlayer.handler.interrupt();
            existingPlayer.handler = handler;
            return existingPlayer;
        }


    };

    public static void broadcast(NetEvent event, boolean reflect){
        if(event.authorID==NetEvent.LOCAL_USER){
            svLog("SV --E "+event);
        }
        for (SvPlayer player: svPlayers){
            if(!player.handler.isReady())
                continue;
            if(reflect||player.handler.getClientUID()!=event.authorID)
                player.handler.sendEvent(event);
        }
    }

    public static void send(SvPlayer player, NetEvent event){
        if(!player.handler.isReady())
            throw new RuntimeException("player not ready");
        player.handler.sendEvent(event);
    }

    public static void svLog(String s) {
        //String ms = millis() / 1000 + "." + String.format("%03d", millis() % 1000) + " : " + s;
        String s2 = s.replace("\n","\\n");
        System.out.println(s2);
        serverlog.addLine(s2);
    }

    public static void svErr(String s) {
        //String ms = millis() / 1000 + "." + String.format("%03d", millis() % 1000) + " : " + s;
        String s2 = s.replace("\n","\\n");
        System.err.println(s2);
        serverlog.addLine(AdvancedApplet.hyperText("/]")+s2);
    }
}
