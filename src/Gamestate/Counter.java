package Gamestate;

import Globals.GlobalEnvironment;
import Schema.SchemaEditable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@SchemaEditable
public class Counter {
    @SchemaEditable
    private int value;
    CounterListener listener;
    private int lastUpdateTimestamp;

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

    public int getValue() {
        return value;
    }

    public int getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void localSetValue(int value) {
        if(listener!=null)
            listener.counterValueChange(value-this.value, this);
        //this.value = value;
    }

    public void netApplyDelta(int value) {
        this.value += value;
        lastUpdateTimestamp = GlobalEnvironment.simTimeMS();
    }

    public void setListener(CounterListener listener) {
        this.listener = listener;
    }

    public interface CounterListener {
        void counterValueChange(int delta, Counter counter);
    }
}
