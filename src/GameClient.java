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


public class GameClient extends AdvancedApplet {

    public void settings() {
//        size(1600, 900, "core.AdvancedGraphics");
        smooth(4);
        fullScreen("core.AdvancedGraphics");
    }

    float ax, ay, az;
    int lastMillis = 0;
    UITextBox chatBox;
    UIBase netPanel;
    UIButton netMenuButton;

    PImage testimg;



    void loadAndCreateSymbol(String file, String bind) {
        PImage ti = loadImage(file);
        Symbol cayde = SymbolInjector.createSymbol(ti);
        cayde.setMSize(28);
        SymbolInjector.addKey(bind, cayde);
        System.out.println("Binding '" + file + String.format("' to char 0x%02x", (int) cayde.c));
    }

    @Override
    public void setup() {
        UIBase.app = this;
        imageLoader = new ImageLoader(this);
        Style.loadFonts(this);

        loadAndCreateSymbol("cayde32.png", "Y");
        loadAndCreateSymbol("earth.png", "E");
        loadAndCreateSymbol("air.png", "A");
        loadAndCreateSymbol("fire.png", "F");
        loadAndCreateSymbol("water.png", "W");
        loadAndCreateSymbol("cool.png", "C");
        loadAndCreateSymbol("love.png", "L");
        loadAndCreateSymbol("power.png", "P");
        loadAndCreateSymbol("death.png", "D");

        uiRoot = new UIBase(0, 0, width, height);

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
        netMenuButton = uiRoot.addChild(new UIButton(0, -25, 100, 25, "Disconnected"));
        netMenuButton.onAction = new Action() {
            @Override
            public void action() {
                netPanel.setEnabled(!netPanel.isEnabled());
            }
        };

        cardPreview = uiRoot.addChild(new UICardView(50,10,.5f,UILayer.INTERFACE));
        cardPreview.setCardDefinitionView(new CardDefinition(-1, "The Golden Judgement", "Exotic Warrior Behemoth", "At the beginning of your turn:\n" +
                "If your /P equals your /D, gain a VP for\neach /P.\n" +
                "Otherwise, loose X VP for the difference\nbetween your /P and /D.", "Power always comes with a cost","b02.jpg"));
        uiRoot.addChild(new UICardView(50,-700,1f, UILayer.INTERFACE)).setCardDefinitionView(cardPreview.card.definition);
        cardPreview.card.definition.setBeingValues(true,3,10);
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
        //uiRoot.addChild(new UIImage(900, -710, 1000,1000, testimg2)).setScaling(false);


        //uiRoot.addChild(new UIImage(10,10,1,1,imageLoader.getUserImage("test16.png"))).setScaling(false);


        lastMillis = millis();
    }

    public void draw() {
        background(240, 225, 200);
        int dt = millis() - lastMillis;
        lastMillis = millis();
        float drawStartTime = Debug.perfTimeMS();

        handleReceivedNetEvents();


        noFill();
        noStroke();
        strokeWeight(1);
        if(width != uiRoot.getWidth() || height != uiRoot.getHeight()){
            uiRoot.setSize(width, height);
        }

        gameStateManager.updateStep(dt);

        uiRoot.updateFocus(mouseX, mouseY);
        uiRoot.updateLogic(dt);
        textAlign(LEFT);
        uiRoot.render(this);

        //image(imageLoader.getCardImage("b02.jpg"),10,10);
        cardPreview.card.definition.drawPreview(this,1050,380,1);
        cardPreview.card.definition.drawPreview(this,550,10,.5f);
        if(Debug.renderUIDebug){
            stroke(127);
            line(0,mouseY,width,mouseY);
            line(mouseX,0,mouseX,height);
        }

//        fill(255);
//        Style.getFont(Style.F_STANDARD,Style.FONT_12).apply(this);
//        textAlign(CENTER,CENTER);
//        text(CC_BOLD+"Testing @16 pt", 110,25);
//        Style.getFont(Style.F_SCRIPT,Style.FONT_12).apply(this);
        //text("Testing @16 pt", 320,40);
        Debug.perfView.frameGraph.addVal(Debug.perfTimeMS()-drawStartTime);
        Debug.perfView.nextFrame(dt);
        if(Debug.renderPerfView)
            Debug.perfView.render(this.getAdvGraphics());
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
        //println(key, (int) key, keyCode);

        if (keyCode == VK_F3)
            Debug.renderUIDebug = !Debug.renderUIDebug;
        if (keyCode == VK_F7)
            Debug.renderPerfView = !Debug.renderPerfView;
//        else if (keyCode == VK_F4)
//            DebugConstants.printUIDebug = !DebugConstants.renderUIDebug;
        else if (keyCode == VK_F12)
            Debug.breakpoint = !Debug.breakpoint;
        else uiRoot.handleKeyPress(true, key, keyCode);
    }

    @Override
    public void keyReleased() {
        super.keyReleased();
        uiRoot.handleKeyPress(false, key, keyCode);
    }

    @Override
    public void mousePressed() {
        uiRoot.handleMouseInput(true, mouseButton, mouseX, mouseY);
    }

    @Override
    public void mouseReleased() {
        uiRoot.handleMouseInput(false, mouseButton, mouseX, mouseY);
    }

    public static void main(String... args) {
        // dumb hack
        // for some reason, PApplet.main concats the args after the sketch class name
        // even though any args after the sketch name are ignored
        // ...
        final String SKETCH_NAME = "GameClient";
        if(args.length>0) {
            String name_tm = args[0];
            for (int i = 0;i<args.length-1;i++){
                args[i]=args[i+1];
            }
            args[args.length-1]=SKETCH_NAME;
            PApplet.main(name_tm,args);
        } else {
            PApplet.main(SKETCH_NAME);
        }
    }
}