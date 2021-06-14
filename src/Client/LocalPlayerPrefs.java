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

    public String lastDisplayName;
    public String lastSuccessfulIP;

    // local only
    public String fname;

    public LocalPlayerPrefs() {
        super();
        accountName="";
        lastDisplayName="";
        lastSuccessfulIP="";//todo make localhost or something
    }

    public LocalPlayerPrefs(DataInputStream dis) throws VersionMismatchException, IOException {
        super(dis);
        deserialize(dis);
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
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        dos.writeUTF(accountName);
        dos.writeUTF(lastDisplayName);
        dos.writeUTF(lastSuccessfulIP);
    }

    @Override
    protected void deserializeFromVersion(DataInputStream dis, int dataVersion) throws VersionMismatchException, IOException {
        if(dataVersion != getVersionNumber())
            throw new VersionMismatchException(dataVersion,getVersionNumber(),getSchemaType());
        accountName = dis.readUTF();
        lastDisplayName = dis.readUTF();
        lastSuccessfulIP = dis.readUTF();
    }
}
