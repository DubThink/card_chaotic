package Client;

import UI.UIBase;
import UI.UICardView;
import UI.UILogView;
import UI.UIModal;
import core.AdvancedApplet;
import core.ImageLoader;
import network.NetworkClient;

import static Globals.GlobalEnvironment.asyncIOHandler;
import static core.AdvancedApplet.CC_ITALIC;

public class ClientEnvironment {
    public static final CardDefinitionManager cardDefinitionManager;
    public static NetworkClient netClient;
    public static ClientGameStateManager gameStateManager;

    public static LocalPlayerPrefs localPlayerPrefs;

    public static UILogView chatView;
    public static UIModal syncModal;

    static {
        cardDefinitionManager = new CardDefinitionManager();
    }

    public static void savePlayerPrefs(){
        System.out.println("Saving player prefs");
        if(asyncIOHandler!=null && localPlayerPrefs!=null)
            asyncIOHandler.requestSave(localPlayerPrefs, localPlayerPrefs.fname+".prefs");
    }

    public static void sysMessage(String s){
        chatView.addLine(CC_ITALIC+s);
    }

    public static void sysError(String s){
        chatView.addLine(AdvancedApplet.hyperText("/]")+CC_ITALIC+s);
    }

}
