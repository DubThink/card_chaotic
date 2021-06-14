package Schema;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TemplateSchemaClass extends VersionedSerializable{
    private static final int SCHEMA_VERSION_NUMBER = 1;

    public TemplateSchemaClass() {
        super();
    }

    public TemplateSchemaClass(DataInputStream dis) throws VersionMismatchException, IOException {
        super(dis);
        deserialize(dis);
    }

    @Override
    public int getVersionNumber() {
        return SCHEMA_VERSION_NUMBER;
    }

    @Override
    public int getSchemaType() {
        return SchemaTypeID.RESERVED_INVALID_SCHEMA;
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
    }

    @Override
    protected void deserializeFromVersion(DataInputStream dis, int dataVersion) throws VersionMismatchException, IOException {
        if(dataVersion != getVersionNumber())
            throw new VersionMismatchException(dataVersion,getVersionNumber(),getSchemaType());
    }
}
