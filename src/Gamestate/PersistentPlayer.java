package Gamestate;

import Schema.SchemaTypeID;
import Schema.VersionMismatchException;
import Schema.VersionedSerializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PersistentPlayer extends VersionedSerializable {
    private static final int SCHEMA_VERSION_NUMBER = 1;

    public String loginUsername;
    public String loginHashedPass;

    public PersistentPlayer(int uid) {
        super();
    }

    public PersistentPlayer(DataInputStream dis) throws VersionMismatchException, IOException {
        super(dis);
    }

    @Override
    public int getVersionNumber() {
        return SCHEMA_VERSION_NUMBER;
    }

    @Override
    public int getSchemaType() {
        return SchemaTypeID.PERSISTENT_PLAYER;
    }

    @Override
    protected void deserializeFromVersion(DataInputStream dis, int i) throws VersionMismatchException, IOException {
        throw new VersionMismatchException(i, getVersionNumber(), getSchemaType());
    }


    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        dos.writeUTF(loginUsername);
        dos.writeUTF(loginHashedPass);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        loginUsername = dis.readUTF();
        loginHashedPass = dis.readUTF();
    }
}
