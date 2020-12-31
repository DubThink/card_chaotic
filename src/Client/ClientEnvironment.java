package Client;

import Gamestate.CardDefinitionManager;
import UI.UIBase;
import UI.UILogView;
import network.NetworkClient;
import network.NetworkEventTransceiver;

import static core.AdvancedApplet.CC_ITALIC;
import static core.AdvancedApplet.hyperText;

public class ClientEnvironment {
    public static final CardDefinitionManager cardDefinitionManager;
    public static NetworkClient netClient;
    public static ClientGameStateManager gameStateManager;

    public static UIBase uiRoot;
    public static UILogView chatView;

    static {
        cardDefinitionManager = new CardDefinitionManager();
    }

    public static void sysMessage(String s){
        chatView.addLine(CC_ITALIC+s);
    }
}
