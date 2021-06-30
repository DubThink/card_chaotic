package Gamestate;

import Globals.DBG;
import Globals.GlobalEnvironment;
import aew.Util;
import network.NetSerializable;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static Client.ClientEnvironment.cardDefinitionManager;
import static Gamestate.CardDefinition.ARCHETYPE_BEING;

public class Card extends NetSerializable implements Counter.CounterListener {
    public CardDefinition definition;
    public boolean tapped;
    public boolean flipped;

    public Counter health;
    public Counter counter1;

    private CardStack owningStack;

    public static final int HEALTH_COLOR = Util.pColor(new Color(255, 148, 143));
    public static final int COUNTER_COLOR = Util.pColor(new Color(160, 167,255));

    private int lastUpdateMS;

    public Card(CardDefinition definition) {
        this.definition = definition;
        tapped = false;
    }

    public Card initializeCard(){
        if(definition.archetype == ARCHETYPE_BEING){
            health = new Counter(definition.healthDefaultValue);
        }
        if(definition.hasCounter ||true){
            counter1 = new Counter(definition.counterDefaultValue);
        }
        return this;
    }

    public Card(DataInputStream dis) throws IOException {
        super(dis);
        deserialize(dis);
    }

    public void setTapped(boolean tapped) {
        if(this.tapped != tapped){
        }
        this.tapped = tapped;
    }

    public int getLastUpdateMS() {
        return lastUpdateMS;
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeInt(definition.uid);
        dos.writeBoolean(tapped);
        Counter.serializeCounter(dos,health);
        Counter.serializeCounter(dos,counter1);

    }

    protected void deserialize(DataInputStream dis) throws IOException {
        definition = cardDefinitionManager.getDefinition(dis.readInt());
        tapped = dis.readBoolean();
        health = Counter.deserializeCounter(dis);
        counter1 = Counter.deserializeCounter(dis);
        if(health!=null)
            health.setListener(this);
        if(counter1!=null)
            counter1.setListener(this);

    }

    void setOwningStack(CardStack owningStack){
        this.owningStack = owningStack;
    }

    void netApplyUpdate(CardAction action) {
        //lastUpdateMS = GlobalEnvironment.simTimeMS();
        if(action instanceof ActionFlipCard) {
            flipped=!flipped;
        } else if(action instanceof ActionChangeCounter changeCounter) {
            if(changeCounter.counterIdx == 0)
                health.netApplyDelta(changeCounter.delta);
            else if(changeCounter.counterIdx == 1)
                counter1.netApplyDelta(changeCounter.delta);

        } else {
            DBG.Warning("wtf are do");
        }
    }

    public static final int ACTION_FLIP = 1;

    @Override
    public void counterValueChange(int delta, Counter counter) {
        owningStack.localApplyCardAction(this, new ActionChangeCounter(delta, counter==health?0:1));
    }

    public static class ActionFlipCard extends CardAction {

        public ActionFlipCard() {
            super();
        }

        public ActionFlipCard(DataInputStream dis) throws IOException {
            super(dis);
        }

        public int actionTypeIdentifier() {
            return ACTION_FLIP;
        }

        public String toString() {
            return "ActionFlipCard{}";
        }
    }

    public static final int ACTION_CHANGE_COUNTER = 2;
    public static class ActionChangeCounter extends CardAction {
        int delta;
        int counterIdx;

        public ActionChangeCounter(int delta, int counterIdx) {
            this.delta = delta;
            this.counterIdx = counterIdx;
        }

        public ActionChangeCounter(DataInputStream dis) throws IOException {
            super(dis);
            delta = dis.readInt();
            counterIdx = dis.readInt();
        }

        @Override
        public void serialize(DataOutputStream dos) throws IOException {
            dos.writeInt(delta);
            dos.writeInt(counterIdx);
        }

        public int actionTypeIdentifier() {
            return ACTION_CHANGE_COUNTER;
        }

        public String toString() {
            return "ActionChangeCounter{" +
                    "delta=" + delta +
                    ", counterIdx=" + counterIdx +
                    '}';
        }
    }
}
