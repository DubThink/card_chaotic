package Server;

import Schema.SchemaTypeID;
import Schema.VersionMismatchException;
import Schema.VersionedSerializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CardLibraryMetadata extends VersionedSerializable {
    private static final int SCHEMA_VERSION_NUMBER = 1;

    public int maxCardID;

    public CardLibraryMetadata() {
        super();
    }

    public CardLibraryMetadata(DataInputStream dis) throws VersionMismatchException, IOException {
        super(dis);
        deserializeVersioned(dis);
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        dos.writeInt(maxCardID);
    }

    @Override
    public int getVersionNumber() {
        return SCHEMA_VERSION_NUMBER;
    }

    @Override
    public int getSchemaType() {
        return SchemaTypeID.CARD_LIBRARY_METADATA;
    }

    @Override
    protected void deserializeFromVersion(DataInputStream dis, int dataVersion) throws VersionMismatchException, IOException {
        if(dataVersion != getVersionNumber())
            throw new VersionMismatchException(dataVersion,getVersionNumber(),getSchemaType());
        maxCardID = dis.readInt();
    }
}
