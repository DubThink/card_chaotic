package Client;

import Schema.SchemaTypeID;
import Schema.VersionMismatchException;
import Schema.VersionedSerializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LocalPlayerPrefs extends VersionedSerializable {
    private static final int SCHEMA_VERSION_NUMBER = 1;

    public String accountName;

    // local only
    public String fname;

    public LocalPlayerPrefs() {
        super();
        accountName="";
    }

    public LocalPlayerPrefs(DataInputStream dis) throws VersionMismatchException, IOException {
        super(dis);
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        dos.writeUTF(accountName);
    }

    @Override
    public int getVersionNumber() {
        return SCHEMA_VERSION_NUMBER;
    }

    @Override
    public int getSchemaType() {
        return SchemaTypeID.LOCAL_PLAYER_PREFS;
    }

    @Override
    protected void deserializeFromVersion(DataInputStream dis, int i) throws VersionMismatchException, IOException {
        throw new VersionMismatchException(i,getVersionNumber(),getSchemaType());
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        accountName = dis.readUTF();
    }
}
