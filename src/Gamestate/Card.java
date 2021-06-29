package Gamestate;

import aew.Util;
import network.NetSerializable;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static Client.ClientEnvironment.cardDefinitionManager;
import static Gamestate.CardDefinition.ARCHETYPE_BEING;

public class Card extends NetSerializable {
    public CardDefinition definition;
    public boolean tapped;
    public boolean flipped;

    public Counter health;
    public Counter counter1;

    private CardStack owningStack;

    public static final int HEALTH_COLOR = Util.pColor(new Color(255, 148, 143));
    public static final int COUNTER_COLOR = Util.pColor(new Color(160, 167,255));

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

    }

    void setOwningStack(CardStack owningStack){
        this.owningStack = owningStack;
    }
}
