package Gamestate;

import Schema.SchemaEditable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@SchemaEditable
public class Counter {
    public int value;

    public Counter(int value) {
        this.value = value;
    }

    public static Counter deserializeCounter(DataInputStream dis) throws IOException {
        boolean exists =  dis.readBoolean();
        if(exists)
            return new Counter(dis.readInt());
        return null;
    }

    public static void serializeCounter(DataOutputStream dos, Counter counter) throws IOException {
        dos.writeBoolean(counter != null);
        if(counter!=null)
            dos.writeInt(counter.value);
    }
}
