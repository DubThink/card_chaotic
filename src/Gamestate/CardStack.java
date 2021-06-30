package Gamestate;

import Gamestate.Gameobjects.GameObject;
import Gamestate.Gameobjects.GameObjectUpdate;
import Schema.SchemaEditable;
import network.NetSerializerUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static Globals.DBG.Warning;

public class CardStack extends GameObject {
    @SchemaEditable
    private ArrayList<Card> cards;
    @SchemaEditable
    private boolean publicView;

    private ArrayList<CardStackListener> listeners;



    private static final int ADD_CARD = 1;
    private static final int FLIP_CARD = 2;

    public CardStack(int owner) {
        super(owner);
        cards = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public CardStack(DataInputStream dis) throws IOException {
        super(dis);
        deserialize(dis);
        listeners = new ArrayList<>();
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        NetSerializerUtils.serializeArrayList(cards,dos);
        dos.writeBoolean(publicView);
    }

    private void deserialize(DataInputStream dis) throws IOException {
        if(cards == null)
            cards = new ArrayList<>();
        NetSerializerUtils.deserializeArrayList(cards,dis,Card::new);
        for(Card card: cards)
            card.setOwningStack(this);
        publicView = dis.readBoolean();
    }

    public void addCard(Card card) {
        assertOwned();
        localApplyUpdate(new GOUCard(card, ADD_CARD));
    }

    private void _addCard(Card card) {
        int idx = cards.size();
        cards.add(card);
        card.setOwningStack(this);
        for (CardStackListener listener: listeners)
            listener.addedCard(this, idx);
    }

    protected int getIndexOfCard(Card card) {
        return cards.indexOf(card);
    }

    public void localApplyCardAction(Card card, CardAction action) {
        assertOwned();
        localApplyUpdate(new GOUCardAction(getIndexOfCard(card), action));

    }

    @Override
    public void netApplyUpdate(GameObjectUpdate update) {
        if (update instanceof GOUCard gouCard) {
            if (gouCard.actionType == ADD_CARD) {
                _addCard(gouCard.card);
            } else {
                Warning("Action type " + gouCard.actionType + " undefined for GOUCard.");
            }
        } else if (update instanceof GOUCardAction gouCardAction) {
            Card c = getCardByIdx(gouCardAction.cardIndex);
            if(c==null)
                Warning("Missing card at idx "+gouCardAction.cardIndex);
            else
                c.netApplyUpdate(gouCardAction.action);

        } else {
            Warning("Unhandled update " + update);
        }
    }

    @Override
    protected void onReceiveOwnership() {
        System.out.println("test123");
        for (CardStackListener listener: listeners)
            listener.onOwnershipChange(this, true);
    }

    @Override
    protected void onLoseOwnership() {
        for (CardStackListener listener: listeners)
            listener.onOwnershipChange(this, false);
    }

    public Card getCardByIdx(int idx){
        return cards.get(idx);
    }

    public int getCardCount(){
        return cards.size();
    }

    public void addListener(CardStackListener listener){
        listeners.add(listener);
    }

    public void removeListener(CardStackListener listener){
        listeners.remove(listener);
    }

}
