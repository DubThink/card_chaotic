package Client;

import UI.UIBase;
import UI.UICardView;
import UI.UILogView;
import core.ImageLoader;
import network.NetworkClient;

import static core.AdvancedApplet.CC_ITALIC;

public class ClientEnvironment {
    public static final CardDefinitionManager cardDefinitionManager;
    public static NetworkClient netClient;
    public static ClientGameStateManager gameStateManager;

    public static UILogView chatView;
    public static UICardView cardPreview;

    static {
        cardDefinitionManager = new CardDefinitionManager();
    }

    public static void sysMessage(String s){
        chatView.addLine(CC_ITALIC+s);
    }
}
