package Server;

import network.NetClientInterface;
import network.NetEvent;

import static network.NetEvent.LOCAL_USER;
import static network.NetEvent.SERVER_USER;

public class ServerNetClient implements NetClientInterface {
    @Override
    public void sendEvent(NetEvent e) {
        ServerEnvironment.broadcast(e,false);
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public int translateUserIDToNet(int localID) {
        return localID == LOCAL_USER ? SERVER_USER : localID;
    }

    @Override
    public int translateUserIDToLocal(int netID) {
        return netID == SERVER_USER ? LOCAL_USER : netID;
    }
}
