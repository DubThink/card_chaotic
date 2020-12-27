package Gamestate;

import java.util.ArrayList;

public class ServerGamestate {
    public static ArrayList<PlayerIdentity> playerIdentities;
    private static int nextPlayerUID=1;

    static {
        playerIdentities = new ArrayList<>();
    }

    public static int getPlayerUIDByUsername(String s){
        for(PlayerIdentity identity: playerIdentities){
            if(identity.username.equals(s))
                return identity.uid;
        }
        return -1;
    }

    public static PlayerIdentity addPlayer(String s){
        assert getPlayerUIDByUsername(s)==-1;
        PlayerIdentity newPI = new PlayerIdentity(nextPlayerUID++, s);
        playerIdentities.add(newPI);
        return newPI;
    }
}
