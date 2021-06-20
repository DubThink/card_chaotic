import Client.*;
import Gamestate.Card;
import Gamestate.CardDefinition;
import Globals.Style;
import Schema.DiskUtil;
import Server.CardSource;
import UI.*;
import core.*;
import network.event.*;
import network.NetEvent;

import java.util.Arrays;

import static Client.ClientEnvironment.*;
import static Globals.GlobalEnvironment.*;
import static com.jogamp.newt.event.KeyEvent.*; // use this for p2d-based graphics (not KeyEvent)


public class GameClient extends GameBase {

    public void settings() {
        fullScreen("core.AdvancedGraphics");
        super.settings();
//        size(1600, 900, "core.AdvancedGraphics");
    }

    UICardEditor cardEditor;

    ConnectScreen connectScreen;

    ImageNetEvent testIMG;

    static {
        Globals.Debug.stateDebug = new ClientDebugPanel();
    }

    @Override
    public void setup() {
        super.setup();

        // ---- LOAD PLAYER PREFS ---- //
        String prefsFilename = getArgParameter("prefs","localplayer");

        localPlayerPrefs = DiskUtil.tryToLoadFromFileTyped(LocalPlayerPrefs.class, "data/client/"+prefsFilename+".prefs");
        if(localPlayerPrefs == null)
            localPlayerPrefs = new LocalPlayerPrefs();
        localPlayerPrefs.fname = prefsFilename;

        // ---- Title/Connect Screen ---- //
        connectScreen = new ConnectScreen(uiRoot.addChild(new UIPanel(0,0,0,0), UILayer.OVERLAY),width,height);
        if(checkArg("nonet"))
            connectScreen.toggle();

        UIPanel testEditor = uiRoot.addChild(new UIPanel(700,10,-10,-10),UILayer.OVERLAY);
        testEditor.setEnabled(false);
        UITextBox editBox = testEditor.addChild(new UITextBox(10, 10, -10, -600, false));
        editBox.setText("*testing*");
        editBox.setFontFamily(Style.F_CODE);
        UILabel editOutput = testEditor.addChild(new UILabel(10,-590,-10,-10,""));
        UILabel editOutput2 = testEditor.addChild(new UILabel(10,-340,-10,-10,""));
        editOutput2.setFontFamily(Style.F_FLAVOR);
        editBox.textUpdated = new UIUpdateNotify<UITextBox>() {
            @Override
            public void notify(UITextBox source) {
                editOutput.setText(hyperText(editBox.getText()));
                editOutput2.setText(editBox.getText());
            }
        };

        ((AdvancedGraphics) g).initializeInjector();

        gameStateManager = new ClientGameStateManager();
        gameStateManager.gotoPhase(new ClBiddingPhase());


        cardEditor = uiRoot.addChild(new UICardEditor(uiRoot));

        cardEditor.setEnabled(false);

        CardSource s = DiskUtil.tryToLoadFromFileTyped(CardSource.class, "C:\\devspace\\doxo\\data\\server\\cards/card_12.card");
        UICardView backView = uiRoot.addChild(new UICardView(100,10,1,UILayer.FIELD));
        UICardView definitionView = uiRoot.addChild(new UICardView(100+20+ CardDefinition.CARD_WIDTH,10,1,UILayer.FIELD));
        UICardView instanceView = uiRoot.addChild(new UICardView(100+40+ CardDefinition.CARD_WIDTH*2,10,1,UILayer.FIELD));

        UICardView backView2 = uiRoot.addChild(new UICardView(100,692,.5f,UILayer.FIELD));
        UICardView definitionView2 = uiRoot.addChild(new UICardView(100+20+ CardDefinition.CARD_WIDTH,692,.5f,UILayer.FIELD));
        UICardView instanceView2 = uiRoot.addChild(new UICardView(100+40+ CardDefinition.CARD_WIDTH*2,692,.5f,UILayer.FIELD));


        backView.setCardBackView();
        backView2.setCardBackView();
        definitionView.setCardDefinitionView(s.definition);
        definitionView2.setCardDefinitionView(s.definition);
        Card testCard = new Card(s.definition);
        testCard.initializeCard();
        instanceView.setCardView(testCard,false);
        instanceView2.setCardView(testCard,false);

        openSchema(testCard, false);

        //AccountManager s = DiskUtil.tryToLoadFromFileTyped(AccountManager.class, "C:\\devspace\\doxo\\data\\server/accountdb.bs");

        super.finalizeSetup();
    }

    @Override
    public void _draw(int dt) {
        if(netClient!=null && netClient.isAlive())
            netClient.updateTimeouts(dt);
        handleReceivedNetEvents(dt);

        gameStateManager.updateStep(dt);

        if(testIMG!=null)image(testIMG.image,1200,0);
    }

    public void handleReceivedNetEvents(int dt){
        if(netClient == null || !netClient.isAlive())return;
        while (netClient.hasReceivedEvents()) {
            NetEvent event = netClient.pollEvent();
            if (event instanceof ChatMessageNetEvent) {
                chatView.addLine(event.authorID + ": " + hyperText(((ChatMessageNetEvent) event).message));
            } else if (event instanceof DefineCardNetEvent){
                cardDefinitionManager.handleNetEvent((DefineCardNetEvent) event);
            } else if(event instanceof ImageNetEvent){
                testIMG = (ImageNetEvent) event;
            } else if(event instanceof GrantCardIDNetEvent){
                cardEditor.handleNetEvent((GrantCardIDNetEvent) event);
            } else if (event instanceof SyncCompleteNetEvent){
                if(syncModal!=null) {
                    syncModal.closePositive();
                    syncModal = null;
                }
            } else if(gameStateManager.handleNetEvent(event)){
                // pass
            } else {
                System.out.println("Unhandled event "+event);
            }
        }
    }

    @Override
    public void keyPressed() {
        super.keyPressed();

        if(DEV_MODE) {
            if (keyCode == VK_F2) {
                cardEditor.setEnabled(!cardEditor.isEnabled());
            }
            if (keyCode == VK_F4) {
                connectScreen.toggle();
            }
        }
    }

    @Override
    public void keyReleased() {
        super.keyReleased();
    }

    public static void main(String... args) {
        System.out.println("Args:"+ Arrays.toString(args));
        run(GameClient.class.getSimpleName(),args);
    }
}