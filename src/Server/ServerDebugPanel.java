package Server;

import Debug.DebugPanel;
import core.AdvancedGraphics;

public class ServerDebugPanel extends DebugPanel {
    @Override
    protected void _render(AdvancedGraphics p) {
        print(p,0,"SERVER DEBUG");
    }
}
