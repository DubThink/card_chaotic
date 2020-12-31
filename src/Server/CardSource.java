package Server;

import Gamestate.CardDefinition;

public class CardSource {
    // db-reflected
    public CardDefinition definition;
    public int timesAllowed;
    public int timesBanned;

    // per-game
    public boolean hasBeenIntroed = false;
    public boolean hasBeenBanned = false;


}
