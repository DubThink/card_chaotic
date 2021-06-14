package network;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class NetOwnedObject extends NetSerializable {
    int owner;

    public NetOwnedObject() {
    }

    public NetOwnedObject(DataInputStream dis) throws IOException {
        deserialize(dis);
    }
}
