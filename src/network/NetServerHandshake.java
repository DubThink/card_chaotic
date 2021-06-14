package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static network.NetEvent.LOCAL_USER;

public class NetServerHandshake extends NetSerializable {
    public boolean success;
    public String message;
    public int clientID;
    public int accountUID;


    public NetServerHandshake() {
        this.success = false;
        this.message = "";
        this.clientID = LOCAL_USER;
    }

    public NetServerHandshake(DataInputStream dis) throws IOException {
        deserialize(dis);
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeBoolean(success);
        dos.writeInt(clientID);
        dos.writeInt(accountUID);
        dos.writeUTF(message);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        success = dis.readBoolean();
        clientID = dis.readInt();
        accountUID = dis.readInt();
        message = dis.readUTF();
    }

    @Override
    public String toString() {
        return "NetServerHandshake{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", clientID=" + clientID +
                ", accountUID=" + accountUID +
                '}';
    }
}
