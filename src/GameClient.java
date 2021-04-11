import Client.ClBiddingPhase;
import Client.ClientGameStateManager;
import Gamestate.CardDefinition;
import Gamestate.ClientGamestate;
import Globals.Debug;
import Globals.Style;
import UI.*;
import core.*;
import network.event.ChatMessageNetEvent;
import network.NetEvent;
import network.NetworkClient;
import network.event.DefineCardNetEvent;
import processing.core.PApplet;
import processing.core.PImage;

import static Client.ClientEnvironment.*;
import static Globals.GlobalEnvironment.*;
import static com.jogamp.newt.event.KeyEvent.*; // use this for p2d-based graphics (not KeyEvent)


public class GameClient extends GameBase {

    public void settings() {
        super.settings();
//        size(1600, 900, "core.AdvancedGraphics");
        fullScreen("core.AdvancedGraphics");
    }

    UIBase netPanel;
    UIButton netMenuButton;
    UITextBox chatBox;
    UICardEditor cardEditor;


    PImage testimg;

    @Override
    public void setup() {
        super.setup();

        UIBase testButtons = uiRoot.addChild(new UIBase(0, 0, 0, 0));
        Action test = new Action() {
            @Override
            public void action() {
                testButtons.setEnabled(false);
            }
        };
        testButtons.addChild(new UIButton(10, 10         , -10, 50, hyperText("Hello World. The 4 elements are /E/A/F/W. We also have /Ccool, /Llove, /Ppower, and /Ddeath. /Y and Cayde."), test));
        testButtons.addChild(new UIButton(10, 10 + 60 * 1, -10, 50, hyperText("Earth time /E/E/E"), test)).setFontFamily(Style.F_IMPACT);
        testButtons.addChild(new UIButton(10, 10 + 60 * 2, -10, 32, hyperText("/X //E \\/E"), test, null, true)).setFontFamily(Style.F_SCRIPT);

        testButtons.addChild(new UIButton(10, 10 + 60 * 4, -10, 50, hyperText("Testi^^ng. /E/E/F: cast ^cool ability^ to ^gen^erate /P/P.\n The sunset ^looks\\^ nice."), test)).setFontFamily(Style.F_STANDARD);
        testButtons.addChild(new UIButton(10, 10 + 60 * 5, -10, 50, hyperText("Testing. /E/E/F: cast cool ability to generate /P/P.\n The sunset looks nice."), test)).setFontFamily(Style.F_FLAVOR);
        testButtons.addChild(new UIButton(10, 10 + 60 * 6, -10, 50, hyperText("Testing. /E/E/F: cast cool ability to generate /P/P.\n The sunset looks nice."), test)).setFontFamily(Style.F_CODE);
        testButtons.addChild(new UIButton(10, 10 + 60 * 7, -10, 50, hyperText("Testing. /E/E/F: cast cool ability to generate /P/P.\n The sunset looks nice."), test)).setFontFamily(Style.F_SCRIPT);
        testButtons.addChild(new UIButton(10, 10 + 60 * 8, -10, 50, hyperText("Testing. /E/E/F: cast cool ability to generate /P/P.\n The sunset looks nice."), test)).setFontFamily(Style.F_IMPACT);

        testButtons.addChild(new UIButton(10, 550 + 40 * 4, -10, 30, hyperText("Testing. /E/E/F: cast cool ability to ge^nerate /P/P. The s^unset looks nice."), test)).setFontFamily(Style.F_STANDARD);
        testButtons.addChild(new UIButton(10, 550 + 40 * 5, -10, 30, hyperText("Testing. /E/E/F: cast cool ability to generate /P/P. The sunset looks nice."), test)).setFontFamily(Style.F_FLAVOR);
        testButtons.addChild(new UIButton(10, 550 + 40 * 6, -10, 30, hyperText("Testing. /E/E/F: cast cool ability to generate /P/P. The sunset looks nice."), test)).setFontFamily(Style.F_CODE);
        testButtons.addChild(new UIButton(10, 550 + 40 * 7, -10, 30, hyperText("Testing. /E/E/F: cast cool ability to generate /P/P. The sunset looks nice."), test)).setFontFamily(Style.F_SCRIPT);
        testButtons.addChild(new UIButton(10, 550 + 40 * 8, -10, 30, hyperText("Testing. /E/E/F: cast cool ability to generate /P/P. The sunset looks nice."), test)).setFontFamily(Style.F_IMPACT);



        //root.addChild(new UIImage(10, 10, -10, -10, loadImage("data/user/inner-well-being.jpg")));

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

        UITextBox username = netPanel.addChild(new UITextBox(10, 45, -10, 25, true));
        netPanel.addChild(new UIButton(10, 80, -10, 25, "Connect", new Action() {
            @Override
            public void action() {
                if(!netClient.isAlive()){
                    ClientGamestate.username = username.getText();
                    netClient.start();
                }
            }
        }));

        netMenuButton = uiRoot.addChild(new UIButton(0, -25, 100, 25, "Disconnected"));
        netMenuButton.onAction = new Action() {
            @Override
            public void action() {
                netPanel.setEnabled(!netPanel.isEnabled());
            }
        };

        chatBox = uiRoot.addChild(new UITextBox(100, -25, 400, 25, true));
        chatView = uiRoot.addChild(new UILogView(100,-250,400,-25));


        chatBox.textSubmitted = new UIUpdateNotify<UITextBox>() {
            @Override
            public void notify(UITextBox source) {
                System.out.println("Sending message");
                if(netClient.isReady())
                    netClient.sendEvent(new ChatMessageNetEvent(source.getText()));
                source.clearText();
            }
        };


        cardPreview = uiRoot.addChild(new UICardView(50,10,.5f,UILayer.INTERFACE));
        cardPreview.setCardDefinitionView(new CardDefinition(-1, "The Golden Judgement", "Exotic Warrior Behemoth", "At the beginning of your turn:\n" +
                "If your /P equals your /D, gain a VP for\neach /P.\n" +
                "Otherwise, loose X VP for the difference\nbetween your /P and /D.", "Power always comes with a cost","gato.jpg"));
        uiRoot.addChild(new UICardView(50,-700,1f, UILayer.INTERFACE)).setCardDefinitionView(cardPreview.card.definition);
        cardPreview.card.definition.setBeingValues(3,10);
        //uiRoot.addChild(new UICardView(1070,-710,1,UILayer.INTERFACE)).setCardDefinitionView(cardPreview.card.definition);

//        uiRoot.addChild(new UICardView(410,-710,.5f,UILayer.INTERFACE)).setCardDefinitionView(cardPreview.card.definition);
//        uiRoot.addChild(new UICardView(410,-710,.5f,UILayer.INTERFACE)).setCardDefinitionView(cardPreview.card.definition);
//        uiRoot.addChild(new UICardView(410,-710,.5f,UILayer.INTERFACE)).setCardDefinitionView(cardPreview.card.definition);
//        uiRoot.addChild(new UICardView(410,-710,.5f,UILayer.INTERFACE)).setCardDefinitionView(cardPreview.card.definition);
//        uiRoot.addChild(new UICardView(410,-710,.5f,UILayer.INTERFACE)).setCardDefinitionView(cardPreview.card.definition);

        //Style.font32.font.initInjection();
        ((AdvancedGraphics) g).initializeInjector();

        netClient = new NetworkClient();

        gameStateManager = new ClientGameStateManager();
        gameStateManager.gotoPhase(new ClBiddingPhase());

        testimg = imageLoader.getUserImage("test_card12b_half.png");
        PImage testimg2 = imageLoader.getUserImage("test_card12_half.png");
        PImage testimg3 = imageLoader.getUserImage("test_card12.png");

        uiRoot.addChild(new UIImage(300, 10, testimg2));
        uiRoot.addChild(new UIImage(550, -700, testimg3));

        cardEditor = uiRoot.addChild(new UICardEditor(uiRoot));
        //uiRoot.addChild(new UIImage(900, -710, 1000,1000, testimg2)).setScaling(false);


        //uiRoot.addChild(new UIImage(10,10,1,1,imageLoader.getUserImage("test16.png"))).setScaling(false);


        super.finalizeSetup();
    }

    @Override
    public void _draw(int dt) {
        handleReceivedNetEvents();

        gameStateManager.updateStep(dt);

        if(Debug.renderUIDebug){
            stroke(127);
            line(0,mouseY,width,mouseY);
            line(mouseX,0,mouseX,height);
        }
    }

    public void handleReceivedNetEvents(){
        if(!netClient.isAlive())return;
        while (netClient.hasReceivedEvents()){
            NetEvent event = netClient.pollEvent();
            if(event instanceof ChatMessageNetEvent){
                chatView.addLine(event.authorID+": "+ hyperText(((ChatMessageNetEvent) event).message));
            } else if (event instanceof DefineCardNetEvent)
                cardDefinitionManager.handleNetEvent((DefineCardNetEvent) event);
            else if(gameStateManager.handleNetEvent(event)){
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