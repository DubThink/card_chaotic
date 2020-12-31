package Server;

import java.util.ArrayList;
import java.util.Random;

public class CardSourceManager {
    ArrayList<CardSource> cardSources;
    int goodCardCount;
    Random random;

    public CardSourceManager() {
        this.cardSources = new ArrayList<>();
        goodCardCount = 0;
        random = new Random();
    }

    public CardSource randomCardSource(){
        int pick = random.nextInt(goodCardCount);
        int testPick = -1;
        int idx=-1;
        while (testPick<pick){
            idx++;
            if(isGoodCard(cardSources.get(idx)))
                testPick++;
        }

        return cardSources.get(idx);
    }

    boolean isGoodCard(CardSource source){
        float timesShown = source.timesBanned + source.timesAllowed;
        return  !source.hasBeenBanned && (timesShown<4 || (source.timesBanned/timesShown)>.6);
    }

    public void banCard(CardSource source){
        if(!source.hasBeenBanned) {
            source.timesBanned++;
            goodCardCount--;
        }
        source.hasBeenBanned=true;
        source.hasBeenIntroed=true;
    }

    public void allowCard(CardSource source){
        if(!source.hasBeenIntroed)
            source.timesAllowed++;
        source.hasBeenIntroed=true;
    }
}
