package Server;

import Gamestate.CardDefinition;
import Schema.SchemaTypeID;
import Schema.VersionMismatchException;
import Schema.VersionedSerializable;
import Schema.SchemaEditViewOnly;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CardSource extends VersionedSerializable {
    private static final int SCHEMA_VERSION_NUMBER = 1;

    // db-reflected
    public CardDefinition definition;
    public int timesAllowed;
    public int timesBanned;

    public int sumWinningBids;
    public int countWinningBids;
    @SchemaEditViewOnly
    public int rev=-1;

    private int testv;

    // per-game
    @SchemaEditViewOnly
    public boolean hasBeenIntroed = false;
    public boolean hasBeenBanned = false;


    // runtime
    boolean matchesFile=false;

    public CardSource(CardDefinition definition) {
        this.definition = definition;
    }

    public CardSource(DataInputStream dis) throws VersionMismatchException, IOException {
        super(dis);
        deserializeVersioned(dis);
    }

    public void submitWinningBid(int bid){
        sumWinningBids+=bid;
        countWinningBids++;
    }

    public void updateDefinition(CardDefinition definition){
        if(definition.uid!=this.definition.uid)
            throw new RuntimeException("cannot update definition with different card");
        if(definition.authorAccountUID!=this.definition.authorAccountUID)
            throw new RuntimeException("account id mismatch while updating definition");
        this.definition=definition;
        this.matchesFile=false;
        anyCardMismatchFile=true;
        rev++;
    }

    static public boolean anyCardMismatchFile;


    @Override
    public int getVersionNumber() {
        return SCHEMA_VERSION_NUMBER;
    }

    @Override
    public int getSchemaType() {
        return SchemaTypeID.CARD_SOURCE;
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        definition.serialize(dos);
        dos.writeInt(timesAllowed);
        dos.writeInt(timesBanned);

        dos.writeInt(sumWinningBids);
        dos.writeInt(countWinningBids);
        dos.writeInt(rev);
    }

    @Override
    protected void deserializeFromVersion(DataInputStream dis, int dataVersion) throws VersionMismatchException, IOException {
        if(dataVersion != getVersionNumber())
            throw new VersionMismatchException(dataVersion, getVersionNumber(), getSchemaType());
        definition = new CardDefinition(dis);
        timesAllowed = dis.readInt();
        timesBanned = dis.readInt();

        sumWinningBids = dis.readInt();
        countWinningBids = dis.readInt();
        rev = dis.readInt();
    }

    @Override
    public String toString() {
        return "CardSource{" +
                "id=" + definition.uid +
                ", rev=" + rev +
                ", name=\"" + definition.name +
                "\"}";
    }
}
