package Client;

import Gamestate.CardDefinition;
import network.NetEvent;
import network.event.DefineCardNetEvent;

import java.util.ArrayList;

import static Client.ClientEnvironment.*;

public class CardDefinitionManager {
    ArrayList<CardDefinition> cards;

    public CardDefinitionManager() {
        cards = new ArrayList<>();
    }

    public CardDefinition getDefinition(int uid){
        if(uid>=cards.size())
            throw new RuntimeException("Card not defined with uid "+uid);
        CardDefinition card = cards.get(uid);
        if(card==null)
            throw new RuntimeException("Card not defined/null with uid "+uid);
        return card;
    }

    public void handleNetEvent(DefineCardNetEvent event){
        if(event.cardDefinition.uid<cards.size() && cards.get(event.cardDefinition.uid) != null)
            return;
        cards.ensureCapacity(event.cardDefinition.uid+1);
        cards.add(event.cardDefinition.uid, event.cardDefinition);
    }

    public ArrayList<CardDefinition> getCardList() {
        return cards;
    }
}
