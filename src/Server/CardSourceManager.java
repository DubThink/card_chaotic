package Server;

import Gamestate.CardDefinition;
import network.NetworkClientHandler;
import network.event.DefineCardNetEvent;

import java.util.ArrayList;
import java.util.Random;

public class CardSourceManager {
    ArrayList<CardSource> cardSources;
    private int nextCardID;
    int goodCardCount;
    Random random;

    public CardSourceManager() {
        this.cardSources = new ArrayList<>();
        cardSources.add(new CardSource(new CardDefinition(0, "Monkey House","Abode","+1 shelter","Not to be confused with Ape Trailer Home", "monkeyhouse.png")));
        goodCardCount = 0;
        random = new Random();
        nextCardID = cardSources.size();
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
        return  !source.hasBeenBanned && (timesShown<4 || (source.timesBanned/timesShown)<.6);
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

    public void defineAllCards(NetworkClientHandler handler){
        for(CardSource source: cardSources){
            if(isGoodCard(source)){
                handler.sendSyncingEvent(new DefineCardNetEvent(source.definition));
            }
        }
    }

    protected void putCardSource(CardSource source){
        if(source.definition.uid!=cardSources.size())
            throw new RuntimeException("Can't create a card in a range that already exists");
        cardSources.add(source);
    }

    public CardSource allocateNextCardSource(){
        CardSource newCard = new CardSource(new CardDefinition(nextCardID++));
        putCardSource(newCard);
        return newCard;
    }

    public void applyCardDefinitionUpdate(CardDefinition definition){
        if(definition.uid<0)
            throw new RuntimeException("Should not be defining placeholder card definition in source");
        if(definition.uid>=cardSources.size())
            throw new RuntimeException("Not in range");
        cardSources.get(definition.uid).updateDefinition(definition);
    }
}
