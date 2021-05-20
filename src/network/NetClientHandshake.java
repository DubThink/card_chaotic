package network;

import Globals.Config;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NetClientHandshake extends NetSerializable {
    public int clientNetVersion;
    public int clientVersion;
    public String username;

    public NetClientHandshake(String username) {
        this.username = username;
        clientNetVersion = Config.NET_VERSION;
        clientVersion = Config.GAME_VERSION;
    }

    public NetClientHandshake(DataInputStream dis) throws IOException {
        super(dis);
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeInt(clientNetVersion);
        dos.writeInt(clientVersion);
        dos.writeUTF(username);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        clientNetVersion = dis.readInt();
        clientVersion = dis.readInt();
        username = dis.readUTF();
    }

    @Override
    public String toString() {
        return "NetClientHandshake{" +
                "clientNetVersion=" + clientNetVersion +
                ", clientVersion=" + clientVersion +
                ", username='" + username + '\'' +
                '}';
    }
}
