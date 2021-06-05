package Schema;

public class SaveRequest {
    String targetFile;
    VersionedSerializable data;

    public SaveRequest(String targetFile, VersionedSerializable data) {
        this.targetFile = targetFile;
        this.data = data;
    }
}
