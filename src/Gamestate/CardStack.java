package Gamestate;

import network.NetOwnedObject;
import network.NetSerializerUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CardStack extends NetOwnedObject {
    ArrayList<Card> cards;
    boolean publicView;


    public CardStack() {
        cards=new ArrayList<>();
    }

    public CardStack(DataInputStream dis) throws IOException {
        super(dis);
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        NetSerializerUtils.serializeArrayList(cards,dos);
        dos.writeBoolean(publicView);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        NetSerializerUtils.deserializeArrayList(cards,dis,Card::new);
        publicView = dis.readBoolean();
    }
}
