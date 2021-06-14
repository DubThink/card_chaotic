package network;

import Globals.Config;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NetClientHandshake extends NetSerializable {
    public int clientNetVersion;
    public int clientVersion;
    public String accountName;
    public String displayName;

    public NetClientHandshake(String accountName, String displayName) {
        this.accountName = accountName;
        this.displayName = displayName;
        clientNetVersion = Config.NET_VERSION;
        clientVersion = Config.GAME_VERSION;
    }

    public NetClientHandshake(DataInputStream dis) throws IOException {
        deserialize(dis);
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeInt(clientNetVersion);
        dos.writeInt(clientVersion);
        dos.writeUTF(accountName);
        dos.writeUTF(displayName);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        clientNetVersion = dis.readInt();
        clientVersion = dis.readInt();
        accountName = dis.readUTF();
        displayName = dis.readUTF();
    }

    @Override
    public String toString() {
        return "NetClientHandshake{" +
                "clientNetVersion=" + clientNetVersion +
                ", clientVersion=" + clientVersion +
                ", accountName='" + accountName + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
