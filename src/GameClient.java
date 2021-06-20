import Client.*;
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

        cardPreview = uiRoot.addChild(new UICardView(50,10,.5f,UILayer.INTERFACE));
        cardPreview.setCardBackView();
        uiRoot.addChild(new UICardView(400,10,1f, UILayer.INTERFACE)).setCardBackView();//.setCardDefinitionView(cardPreview.card.definition);
        cardPreview.card.definition.setBeingValues(3,10);

        ((AdvancedGraphics) g).initializeInjector();

        gameStateManager = new ClientGameStateManager();
        gameStateManager.gotoPhase(new ClBiddingPhase());


        cardEditor = uiRoot.addChild(new UICardEditor(uiRoot));

        cardEditor.setEnabled(false);

//        CardSource s = DiskUtil.tryToLoadFromFileTyped(CardSource.class, "C:\\devspace\\doxo\\data\\server\\cards/card_0.card");
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