package network.event;

import network.NetEvent;
import network.NetEventTypeID;

import java.io.DataInputStream;
import java.io.IOException;

public class SyncCompleteNetEvent extends NetEvent {

    public SyncCompleteNetEvent() {
        super();
    }

    public SyncCompleteNetEvent(DataInputStream dis) throws IOException {
        super(dis);
    }

    @Override
    public int eventTypeIdentifier() {
        return NetEventTypeID.SYNC_COMPLETE;
    }

    @Override
    public String toString() {
        return "SyncCompleteNetEvent(#"+serial+", @"+authorID+"){}";
    }
}
