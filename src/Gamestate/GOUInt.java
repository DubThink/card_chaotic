package Gamestate;

import Gamestate.Gameobjects.GOUType;
import Gamestate.Gameobjects.GameObjectUpdate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GOUInt extends GameObjectUpdate {
    public int data;
    public int actionType;

    public GOUInt(int data, int actionType) {
        this.data = data;
        this.actionType = actionType;
    }

    public GOUInt(DataInputStream dis) throws IOException {
        data = dis.readInt();
        actionType = dis.readInt();
    }

    public int eventTypeIdentifier() {
        return GOUType.INT;
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeInt(data);
        dos.writeInt(actionType);
    }

    @Override
    public String toString() {
        return "GOUInt{" +
                "data=" + data +
                ", actionType=" + actionType +
                '}';
    }
}
