package Gamestate;

public class PlayerIdentity {
    public final int uid;
    String username;

    public PlayerIdentity(int uid, String username) {
        this.uid = uid;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
