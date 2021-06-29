package network;

public interface NetClientInterface {
    void sendEvent(NetEvent e);
    boolean isReady();
    int translateUserIDToNet(int localID);
    int translateUserIDToLocal(int netID);
}
