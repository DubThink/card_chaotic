package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class NetSerializerUtils {
    public static <T extends NetSerializable> void serializeArrayList(ArrayList<T> list, DataOutputStream dos) throws IOException {
        dos.writeInt(list.size());
        for(int i=0;i<list.size();i++){
            list.get(i).serialize(dos);
        }
    }

    public static <T extends NetSerializable> void deserializeArrayList(ArrayList<T> list, DataInputStream dis, Deserializer<T> deserializer) throws IOException {
        int size = dis.readInt();
        list.clear();
        list.ensureCapacity(size);
        for(int i=0;i<size;i++){
            list.add(deserializer.deserialize(dis));
        }
    }
}
