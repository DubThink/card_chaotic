package Gamestate;

import network.NetSerializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static Client.ClientEnvironment.cardDefinitionManager;

public class Card extends NetSerializable {
    public CardDefinition definition;
    public boolean tapped;
    public boolean flipped=true;

    public Card(CardDefinition definition) {
        this.definition = definition;
        tapped = false;
    }

    public Card(DataInputStream dis) throws IOException {
        super(dis);
        System.out.println("deserialize");
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
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        definition = cardDefinitionManager.getDefinition(dis.readInt());
        tapped = dis.readBoolean();

    }
}
