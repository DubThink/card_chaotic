package Gamestate.Gameobjects;

import Gamestate.GOUCard;
import Gamestate.GOUCardAction;
import Gamestate.GOUInt;
import network.NetEvent;
import network.NetEventTypeID;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static Globals.DBG.Warning;

public class GameObjectUpdateEvent extends NetEvent {
    GameObjectUpdate update;
    int objectID;

    public GameObjectUpdateEvent(GameObjectUpdate update, int objectID) {
        super();
        this.update = update;
        this.objectID = objectID;
    }

    public GameObjectUpdateEvent(DataInputStream dis) throws IOException {
        super(dis);
        int type = dis.readInt();
        update = switch (type){
            case GOUType.CARD -> new GOUCard(dis);
            case GOUType.INT -> new GOUInt(dis);
            case GOUType.CARD_ACTION -> new GOUCardAction(dis);
            default -> null;
        };

        if (update==null){
            Warning("Unhandled GOU type with id="+type);
        }
    }

    @Override
    public int eventTypeIdentifier() {
        return NetEventTypeID.GAME_OBJECT_UPDATE;
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        dos.writeInt(update.eventTypeIdentifier());
        update.serialize(dos);
    }

    @Override
    public String toString() {
        return "GameObjectUpdateEvent(#"+serial+", @"+authorID+"){" +
                "objectID=" + objectID +
                "update=" + update +
                '}';
    }
}
