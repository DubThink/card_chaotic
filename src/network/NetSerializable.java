package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class NetSerializable {

    public NetSerializable(){};

    public NetSerializable(DataInputStream dis) throws IOException{
        deserialize(dis);
    }

    public abstract void serialize(DataOutputStream dos) throws IOException;
    protected abstract void deserialize(DataInputStream dis) throws IOException;
}
