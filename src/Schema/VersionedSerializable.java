package Schema;

import network.NetSerializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class VersionedSerializable extends NetSerializable {
    public VersionedSerializable(){}
    public VersionedSerializable(DataInputStream dis) throws VersionMismatchException, IOException {
        int vnum = dis.readInt();
        if(vnum==getVersionNumber())
            deserialize(dis);
        else if(vnum<getVersionNumber())
            deserializeFromVersion(dis,vnum);
        else
            throw new IOException("Version number from stream exceeds current version.");
    }

    public abstract int getVersionNumber();
    public abstract int getSchemaType();
    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeInt(getVersionNumber());
    }

    protected abstract void deserializeFromVersion(DataInputStream dis, int i) throws VersionMismatchException, IOException;

}
