import Gamestate.ClientGamestate;
import Globals.DebugConstants;
import Globals.Style;
import UI.*;
import core.AdvancedApplet;
import core.AdvancedGraphics;
import core.Symbol;
import core.SymbolInjector;
import network.ChatMessageNetEvent;
import network.NetEvent;
import network.NetworkClient;
import processing.core.PApplet;
import processing.core.PImage;

import static com.jogamp.newt.event.KeyEvent.*; // use this for p2d-based graphics (not KeyEvent)


public class GameClient extends AdvancedApplet {

    public void settings() {
        size(1600, 900, "core.AdvancedGraphics");
        smooth(4);
//        fullScreen(P3D,-2);
    }

    float ax, ay, az;
    int lastMillis = 0;
    UIBase root;
    UITextBox chatBox;
    UILogView chatView;
    UIBase netPanel;
    UIButton netMenuButton;

    NetworkClient netClient;


    void loadAndCreateSymbol(String file, String bind) {
        PImage ti = loadImage(file);
        Symbol cayde = SymbolInjector.createSymbol(ti);
        cayde.setMSize(28);
        SymbolInjector.addKey(bind, cayde);
        System.out.println("Binding '" + file + "' to char 0x%02x".formatted((int) cayde.c));
    }

    @Override
    public void setup() {
        UIBase.app = this;
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

        root = new UIBase(0, 0, width, height);

        UIBase testButtons = root.addChild(new UIBase(0, 0, 0, 0));
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

        UIPanel testEditor = root.addChild(new UIPanel(500,10,-10,-10));

        UITextBox editBox = testEditor.addChild(new UITextBox(10, 10, -10, -600, false));
        editBox.setFontFamily(Style.F_CODE);
        UILabel editOutput = testEditor.addChild(new UILabel(10,-590,-10,-10,""));
        editBox.textUpdated = new UIUpdateNotify<UITextBox>() {
            @Override
            public void notify(UITextBox source) {
                editOutput.setText(hyperText(editBox.getText()));
            }
        };



        netPanel = root.addChild(new UIPanel(0,-400,400,-25),UILayer.POPUP);
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

        chatBox = root.addChild(new UITextBox(100, -25, 400, 25, true));
        chatView = root.addChild(new UILogView(100,-500,400,-25));


        chatBox.textSubmitted = new UIUpdateNotify<UITextBox>() {
            @Override
            public void notify(UITextBox source) {
                System.out.println("Sending message");
                if(netClient.isReady())
                    netClient.sendEvent(new ChatMessageNetEvent(source.getText()));
                source.clearText();
            }
        };
        netMenuButton = root.addChild(new UIButton(0, -25, 100, 25, "Disconnected"));
        netMenuButton.onAction = new Action() {
            @Override
            public void action() {
                netPanel.setEnabled(!netPanel.isEnabled());
            }
        };



        //Style.font32.font.initInjection();
        ((AdvancedGraphics) g).initializeInjector();

        netClient = new NetworkClient();

        lastMillis = millis();
    }

    public void draw() {
        background(240, 225, 200);
        int dt = millis() - lastMillis;
        lastMillis = millis();

        handleReceivedNetEvents();


        noFill();
        noStroke();
        strokeWeight(1);
        if(width != root.getWidth() || height != root.getHeight()){
            root.setSize(width, height);
        }

        root.updateFocus(mouseX, mouseY);
        root.updateLogic(dt);
        root.render(this);
    }

    public void handleReceivedNetEvents(){
        if(!netClient.isAlive())return;
        while (netClient.hasReceivedEvents()){
            NetEvent event = netClient.pollEvent();
            if(event instanceof ChatMessageNetEvent){
                chatView.addLine(event.authorID+": "+ hyperText(((ChatMessageNetEvent) event).message));
            }
        }
    }

    @Override
    public void keyPressed() {
        super.keyPressed();
        //println(key, (int) key, keyCode);

        if (keyCode == VK_F3)
            DebugConstants.renderUIDebug = !DebugConstants.renderUIDebug;
//        else if (keyCode == VK_F4)
//            DebugConstants.printUIDebug = !DebugConstants.renderUIDebug;
        else if (keyCode == VK_F12)
            DebugConstants.breakpoint = !DebugConstants.breakpoint;
        else root.handleKeyPress(true, key, keyCode);
    }

    @Override
    public void keyReleased() {
        super.keyReleased();
        root.handleKeyPress(false, key, keyCode);
    }

    @Override
    public void mousePressed() {
        root.handleMouseInput(true, mouseButton, mouseX, mouseY);
    }

    @Override
    public void mouseReleased() {
        root.handleMouseInput(false, mouseButton, mouseX, mouseY);
    }

    public static void main(String... args) {
        PApplet.main("GameClient");
    }
}