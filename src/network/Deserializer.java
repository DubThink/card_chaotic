package network;

import java.io.DataInputStream;
import java.io.IOException;

public interface  Deserializer <T extends NetSerializable> {
    T deserialize(DataInputStream s) throws IOException;
}
