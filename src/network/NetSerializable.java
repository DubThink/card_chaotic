package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class NetSerializable {

    public NetSerializable(){};

    public NetSerializable(DataInputStream dis) throws IOException {}

    public abstract void serialize(DataOutputStream dos) throws IOException;

    /** Deserializes this class's fields. Should not handle parent class */
    protected abstract void deserialize(DataInputStream dis) throws IOException;
}
