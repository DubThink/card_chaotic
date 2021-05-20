package Server;

import Debug.DebugPanel;
import core.AdvancedGraphics;

import static Client.ClientEnvironment.netClient;

public class ServerDebugPanel extends DebugPanel {
    @Override
    protected void _render(AdvancedGraphics p) {
        print(p,0,"SERVER DEBUG");
    }
}
