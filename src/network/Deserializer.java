package network;

import java.io.DataInputStream;
import java.io.IOException;

public interface  Deserializer <T extends NetSerializable> {
    T testdeserialize(DataInputStream s) throws IOException;
}
