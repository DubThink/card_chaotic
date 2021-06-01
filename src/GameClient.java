import Client.ClBiddingPhase;
import Client.ClientDebugPanel;
import Client.ClientEnvironment;
import Client.ClientGameStateManager;
import Gamestate.CardDefinition;
import Gamestate.ClientGamestate;
import Globals.Style;
import UI.*;
import core.*;
import network.event.ChatMessageNetEvent;
import network.NetEvent;
import network.NetworkClient;
import network.event.DefineCardNetEvent;
import network.event.GrantCardIDNetEvent;
import network.event.ImageNetEvent;
import processing.opengl.PGraphicsOpenGL;

import static Client.ClientEnvironment.*;
import static Globals.GlobalEnvironment.*;
import static com.jogamp.newt.event.KeyEvent.*; // use this for p2d-based graphics (not KeyEvent)


public class GameClient extends GameBase {

    public void settings() {
        fullScreen("core.AdvancedGraphics");
        super.settings();
//        size(1600, 900, "core.AdvancedGraphics");
    }

    UIBase netPanel;
    UIButton netMenuButton;
    UITextBox chatBox;
    UICardEditor cardEditor;

    ImageNetEvent testIMG;

    static {
        Globals.Debug.stateDebug = new ClientDebugPanel();
    }

    @Override
    public void setup() {
        super.setup();
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



        netPanel = uiRoot.addChild(new UIPanel(0,-400,400,-25),UILayer.POPUP);
        netPanel.setEnabled(false);
        netPanel.addChild(new UILabel(10, 10 ,-10, 25,hyperText("^Net status^: not implemented lol")));//.setFontFamily(Style.F_FLAVOR);

        netPanel.addChild(new UIButton(10, 80, -10, 25, "Connect", this::actionConnect));

        netMenuButton = uiRoot.addChild(new UIButton(0, -25, 100, 25, "Disconnected"));
        netMenuButton.onAction = new Action() {
            @Override
            public void action() {
                netPanel.setEnabled(!netPanel.isEnabled());
            }
        };

        chatBox = uiRoot.addChild(new UITextBox(100, -25, 400, 25, true));
        chatView = uiRoot.addChild(new UILogView(100,-250,400,-25));


        chatBox.textSubmitted = source -> {
//            System.out.println("Sending message");
            if(netClient.isReady())
                netClient.sendEvent(new ChatMessageNetEvent(source.getText()));
            source.clearText();
        };


        cardPreview = uiRoot.addChild(new UICardView(50,10,.5f,UILayer.INTERFACE));
        cardPreview.setCardDefinitionView(new CardDefinition(-1, "The Golden Judgement", "Exotic Warrior Behemoth", "At the beginning of your turn:\n" +
                "If your /P equals your /D, gain a VP for\neach /P.\n" +
                "Otherwise, loose X VP for the difference\nbetween your /P and /D.", "Power always comes with a cost","gato.jpg"));
        uiRoot.addChild(new UICardView(400,10,1f, UILayer.INTERFACE)).setCardDefinitionView(cardPreview.card.definition);
        cardPreview.card.definition.setBeingValues(3,10);
        //uiRoot.addChild(new UICardView(1070,-710,1,UILayer.INTERFACE)).setCardDefinitionView(cardPreview.card.definition);

//        uiRoot.addChild(new UICardView(410,-710,.5f,UILayer.INTERFACE)).setCardDefinitionView(cardPreview.card.definition);
//        uiRoot.addChild(new UICardView(410,-710,.5f,UILayer.INTERFACE)).setCardDefinitionView(cardPreview.card.definition);
//        uiRoot.addChild(new UICardView(410,-710,.5f,UILayer.INTERFACE)).setCardDefinitionView(cardPreview.card.definition);
//        uiRoot.addChild(new UICardView(410,-710,.5f,UILayer.INTERFACE)).setCardDefinitionView(cardPreview.card.definition);
//        uiRoot.addChild(new UICardView(410,-710,.5f,UILayer.INTERFACE)).setCardDefinitionView(cardPreview.card.definition);

        //Style.font32.font.initInjection();
        ((AdvancedGraphics) g).initializeInjector();


        gameStateManager = new ClientGameStateManager();
        gameStateManager.gotoPhase(new ClBiddingPhase());


        cardEditor = uiRoot.addChild(new UICardEditor(uiRoot));

        cardEditor.setEnabled(false);


        //uiRoot.addChild(new UIImage(10,10,1,1,imageLoader.getUserImage("test16.png"))).setScaling(false);

        netClient = new NetworkClient();


        super.finalizeSetup();
    }


    void actionConnect(){
        if(!netClient.isAlive()){
            if(netClient.connectionDropped){
                netClient = new NetworkClient();
            }
            ClientGamestate.username = "test";
            netClient.start();
        }
    }

    @Override
    public void _draw(int dt) {
        handleReceivedNetEvents();

        gameStateManager.updateStep(dt);

        if(testIMG!=null)image(testIMG.image,1200,0);
    }

    public void handleReceivedNetEvents(){
        if(!netClient.isAlive())return;
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

        if (keyCode == VK_F2) {
            cardEditor.setEnabled(!cardEditor.isEnabled());
            //cardPreview.card.definition.setCropCenteredSmall().refreshDisplay(this);
        }

    }

    @Override
    public void keyReleased() {
        super.keyReleased();
    }

    public static void main(String... args) {
        run(GameClient.class.getSimpleName(),args);
    }
}