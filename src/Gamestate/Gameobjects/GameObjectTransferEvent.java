package Gamestate.Gameobjects;

import Gamestate.GOUCard;
import network.NetEvent;
import network.NetEventTypeID;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static Globals.DBG.Warning;
import static Globals.GlobalEnvironment.netInterface;

public class GameObjectTransferEvent extends NetEvent {
    int objectID;
    int newOwner;

    public GameObjectTransferEvent(int objectID, int newOwner) {
        this.objectID = objectID;
        this.newOwner = newOwner;
    }

    public GameObjectTransferEvent(DataInputStream dis) throws IOException {
        super(dis);
        objectID = dis.readInt();
        newOwner = netInterface.translateUserIDToLocal(dis.readInt());

    }

    @Override
    public int eventTypeIdentifier() {
        return NetEventTypeID.GAME_OBJECT_TRANSFER;
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        dos.writeInt(objectID);
        dos.writeInt(netInterface.translateUserIDToNet(newOwner));
    }

    @Override
    public String toString() {
        return "GameObjectTransferEvent(#"+serial+", @"+authorID+"){" +
                "objectID=" + objectID +
                "newOwner=" + newOwner +
                '}';
    }
}
