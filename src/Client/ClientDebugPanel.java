package Client;

import Debug.DebugPanel;
import core.AdvancedGraphics;
import static Client.ClientEnvironment.*;

public class ClientDebugPanel extends DebugPanel {
    @Override
    protected void _render(AdvancedGraphics p) {
        print(p,0,"CLIENT DEBUG");
        if(!netClient.isAlive()){
            print(p,2,"Client Disconnected");
        } else {
            print(p, 2, "player uid=" + netClient.getClientUID());
        }

    }
}
