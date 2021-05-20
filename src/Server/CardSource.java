package Server;

import Gamestate.CardDefinition;

public class CardSource {
    // db-reflected
    public CardDefinition definition;
    public int timesAllowed;
    public int timesBanned;

    public int sumWinningBids;
    public int countWinningBids;
    public int rev=-1;

    // runtime
    boolean matchesFile=false;

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

    public void updateDefinition(CardDefinition definition){
        if(definition.uid!=this.definition.uid)
            throw new RuntimeException("cannot update definition with different card");
        this.definition=definition;
        this.matchesFile=false;
        rev++;
    }

}
