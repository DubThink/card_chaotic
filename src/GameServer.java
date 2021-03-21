import Globals.Config;
import Globals.Debug;
import Globals.Style;
import Server.PregamePhase;
import Server.ServerGamePhase;
import Server.SvPlayer;
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

import static Server.ServerEnvironment.*;
import static Globals.GlobalEnvironment.*;

public class GameServer extends AdvancedApplet {

    public void settings() {
        size(1200, 900, "core.AdvancedGraphics");
        smooth(4);
//        fullScreen(P3D,-2);
    }

    float ax, ay, az;
    int lastMillis = 0;
    UITextBox chatBox;
    UIButton netMenuButton;
    ServerSocket ss;
    Exception lastE;

    ServerGamePhase currentPhase;

    UILogView serverlog;

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
        uiRoot.addChild(new UILabel(10, 10, -10, 60, "Server")).setBigLabel(true).setJustify(PConstants.CENTER);


        chatBox = uiRoot.addChild(new UITextBox(100, -25, 400, 25, true));
        chatBox.setFontFamily(Style.F_CODE);


        serverlog = uiRoot.addChild(new UILogView(-700, 10, 690, -10));
        uiRoot.addChild(new UIButton(10, 10, 100, 100, "add", new Action() {
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

        svPlayers = new ArrayList<>();

        lastMillis = millis();

        currentPhase = new PregamePhase();

//        getSurface().setLocation(10,30);
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

        while(currentPhase.shouldEnd()){
            currentPhase.cleanup();
            svLog("Advancing from phase "+currentPhase.getPhaseName());
            currentPhase = currentPhase.createNextPhase();
        }

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

        currentPhase.updateStep(dt);

        noFill();
        noStroke();
        strokeWeight(1);
        if (width != uiRoot.getWidth() || height != uiRoot.getHeight()) {
            uiRoot.setSize(width, height);
        }

        uiRoot.updateFocus(mouseX, mouseY);
        uiRoot.updateLogic(dt);
        uiRoot.render(this);
    }

    protected void networkUpdateStep() {
        try {
            Socket socket = ss.accept();
            svLog("Client connected: " + socket);
            NetworkClientHandler t = new NetworkClientHandler(socket);
            t.start();
            clientConnect(t);
        } catch (SocketTimeoutException e) {
            // pass
        } catch (Exception e) {
            lastE = e;
        }

        for(int i=jipHandlers.size()-1;i>=0;i--){
            NetworkClientHandler handler = jipHandlers.get(i);
            if(handler.needsHandshake()){
                NetClientHandshake clientHandshake = handler.getClientHandshake();
                NetServerHandshake reply = new NetServerHandshake();
                if(clientHandshake.clientNetVersion != Config.NET_VERSION){
                    reply.message = "Net versions do not match (Client = "+clientHandshake.clientNetVersion+", Server = "+Config.NET_VERSION+")";
                } else if(clientHandshake.clientVersion != Config.GAME_VERSION){
                    reply.message = "Game versions do not match (Client = "+clientHandshake.clientVersion+", Server = "+Config.GAME_VERSION+")";
                } else if(getPlayerUIDByUsername(clientHandshake.username)==-1){
                    SvPlayer player = playerConnect(clientHandshake.username, handler);
                    reply.success = true;
                    reply.clientID = player.player.uid;
                } else {
                    reply.message = "User already exists with that name";
                }
                handler.replyServerHandshake(reply);
            }
        }

        for (SvPlayer player : svPlayers) {
            NetworkClientHandler handler = player.handler;

            if(!handler.isSynced()){
                // send the current game state to client
                svLog("Syncing...");
                cardSourceManager.defineAllCards(handler);
                // TODO finish sync code
                svLog("Synced");
                handler.setSynced(true);
            }

            if(handler.isReady())
                if(!player.wasReady) {
                    // on player finish connecting
                    player.active = true;
                }
                while (handler.hasReceivedEvents()) {
                    NetEvent event = handler.pollEvent();
                    svLog("rcvd:"+event.toString());
                    if(!currentPhase.processNetEvent(event)) {
                        broadcast(event, true); // Echo server, essentially
                    }
                }
        }
    }

    @Override
    public void keyPressed() {
        super.keyPressed();

        if (keyCode == VK_F3)
            Debug.renderUIDebug = !Debug.renderUIDebug;
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
        final String SKETCH_NAME = "GameServer";
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