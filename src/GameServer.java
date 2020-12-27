import Gamestate.PlayerIdentity;
import Gamestate.ServerGamestate;
import Globals.Config;
import Globals.DebugConstants;
import Globals.Style;
import UI.*;
import core.AdvancedApplet;
import core.AdvancedGraphics;
import core.Symbol;
import core.SymbolInjector;
import network.NetClientHandshake;
import network.NetEvent;
import network.NetServerHandshake;
import network.NetworkClientHandler;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import static com.jogamp.newt.event.KeyEvent.VK_F12;
import static com.jogamp.newt.event.KeyEvent.VK_F3;


public class GameServer extends AdvancedApplet {

    public void settings() {
        size(1600, 900, "core.AdvancedGraphics");
        smooth(4);
//        fullScreen(P3D,-2);
    }

    float ax, ay, az;
    int lastMillis = 0;
    UIBase root;
    UITextBox chatBox;
    UIButton netMenuButton;
    ServerSocket ss;
    Exception lastE;
    ArrayList<NetworkClientHandler> handlers;

    UILogView serverlog;

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
        root.addChild(new UILabel(10, 10, -10, 60, "Server")).setBigLabel(true).setJustify(PConstants.CENTER);


        chatBox = root.addChild(new UITextBox(100, -25, 400, 25, true));
        chatBox.setFontFamily(Style.F_CODE);


        serverlog = root.addChild(new UILogView(-700, 10, 690, -10));
        root.addChild(new UIButton(10, 10, 100, 100, "add", new Action() {
            @Override
            public void action() {
                svLog("big dick energy");
            }
        }));


        //Style.font32.font.initInjection();
        ((AdvancedGraphics) g).initializeInjector();
        try {
            ss = new ServerSocket(5056);
            ss.setSoTimeout(33);

        } catch (IOException e) {
            lastE = e;
            ss = null;

        }

        handlers = new ArrayList<>();

        lastMillis = millis();
    }

    public void svLog(String s) {
        String ms = millis() / 1000 + "." + String.format("%03d", millis() % 1000) + " : " + s;
        println(ms);
        serverlog.addLine(ms);
    }

    public void draw() {
        background(240, 225, 200);
        int dt = millis() - lastMillis;
        lastMillis = millis();

        fill(255, 0, 0);
        if (ss != null) {
            networkUpdateStep();
        } else {
            Style.getFont(Style.F_STANDARD, Style.FONT_HUGE).apply(this);
            text("UNABLE TO START SOCKET RIP", 100, 100);

        }
        Style.getFont(Style.F_CODE, Style.FONT_MEDIUM).apply(this);
        if (lastE != null)
            text(lastE.toString(), 100, 200);


        noFill();
        noStroke();
        strokeWeight(1);
        if (width != root.getWidth() || height != root.getHeight()) {
            root.setSize(width, height);
        }

        root.updateFocus(mouseX, mouseY);
        root.updateLogic(dt);
        root.render(this);
    }

    protected void networkUpdateStep() {
        try {
            Socket socket = ss.accept();
            svLog("Client connected: " + socket);
            NetworkClientHandler t = new NetworkClientHandler(socket);
            t.start();
            handlers.add(t);
        } catch (SocketTimeoutException e) {
            // pass
        } catch (Exception e) {
            lastE = e;
        }

        for (NetworkClientHandler handler : handlers) {

            if(handler.needsHandshake()){
                NetClientHandshake clientHandshake = handler.getClientHandshake();
                NetServerHandshake reply = new NetServerHandshake();
                if(clientHandshake.clientNetVersion != Config.NET_VERSION ){
                    reply.message = "Net versions do not match (Client = "+clientHandshake.clientNetVersion+", Server = "+Config.NET_VERSION+")";
                } else if(clientHandshake.clientVersion != Config.GAME_VERSION){
                    reply.message = "Game versions do not match (Client = "+clientHandshake.clientVersion+", Server = "+Config.GAME_VERSION+")";
                } else if(ServerGamestate.getPlayerUIDByUsername(clientHandshake.username)==-1){
                    PlayerIdentity id = ServerGamestate.addPlayer(clientHandshake.username);
                    reply.success = true;
                    reply.clientID = id.uid;
                } else {
                    reply.message = "User already exists with that name";
                }
                handler.replyServerHandshake(reply);
            }
            if(!handler.isSynced()){
                // send the current game state to client
                // TODO finish sync code
                handler.setSynced(true);
            }

            if(handler.isReady())
                while (handler.hasReceivedEvents()) {
                    NetEvent event = handler.pollEvent();
                    broadcast(event, true); // Echo server, essentially
                    svLog(event.toString());
                }
        }
    }

    void broadcast(NetEvent event, boolean reflect){
        for (NetworkClientHandler handler: handlers){
            if(!handler.isReady())
                continue;
            if(reflect||handler.getClientUID()!=event.authorID)
                handler.sendEvent(event);
        }
    }

    @Override
    public void keyPressed() {
        super.keyPressed();

        if (keyCode == VK_F3)
            DebugConstants.renderUIDebug = !DebugConstants.renderUIDebug;
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
        PApplet.main("GameServer");
    }
}