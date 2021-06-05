package Gamestate;

import Schema.SchemaTypeID;
import Schema.VersionMismatchException;
import Schema.VersionedSerializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Account extends VersionedSerializable {
    private static final int SCHEMA_VERSION_NUMBER = 1;

    public final int accountUID;
    public String accountName;

    public Account(int uid, String accountName) {
        super();
        accountUID = uid;
        this.accountName = accountName;
    }

    public Account(DataInputStream dis) throws VersionMismatchException, IOException {
        super(dis);
        accountUID = dis.readInt();
    }

    @Override
    public int getVersionNumber() {
        return SCHEMA_VERSION_NUMBER;
    }

    @Override
    public int getSchemaType() {
        return SchemaTypeID.ACCOUNT;
    }

    @Override
    protected void deserializeFromVersion(DataInputStream dis, int dataVersion) throws VersionMismatchException, IOException {
        throw new VersionMismatchException(dataVersion, getVersionNumber(), getSchemaType());
    }


    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        dos.writeUTF(accountName);

        // final
        dos.writeInt(accountUID);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        accountName = dis.readUTF();
    }
}
