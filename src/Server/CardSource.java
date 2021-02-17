package Server;

import Gamestate.CardDefinition;

public class CardSource {
    // db-reflected
    public CardDefinition definition;
    public int timesAllowed;
    public int timesBanned;

    public int sumWinningBids;
    public int countWinningBids;

    public CardSource(CardDefinition definition) {
        this.definition = definition;
    }

    // per-game
    public boolean hasBeenIntroed = false;
    public boolean hasBeenBanned = false;

    public void submitWinningBid(int bid){
        sumWinningBids+=bid;
        countWinningBids++;
    }

}
