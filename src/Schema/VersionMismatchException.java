package Schema;

import java.io.IOError;
import java.io.IOException;

public class VersionMismatchException extends IOException {
    public VersionMismatchException(int sourceVersion, int currentVersion, int schemaID) {
        super("Unhandled Version Mismatch: version "+sourceVersion+" cannot be upgraded to current version "+currentVersion+" (schema id="+schemaID);
    }
}
