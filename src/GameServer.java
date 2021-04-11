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

public class GameServer extends GameBase {

    public void settings() {
        super.settings();
        size(1200, 900, "core.AdvancedGraphics");
//        fullScreen(P3D,-2);
    }

    ServerSocket ss;
    Exception lastE;

    UITextBox chatBox;

    ServerGamePhase currentPhase;

    UILogView serverlog;

    @Override
    public void setup() {
        super.setup();

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

        currentPhase = new PregamePhase();

//        getSurface().setLocation(10,30);

        super.finalizeSetup();
    }

    public void svLog(String s) {
        String ms = millis() / 1000 + "." + String.format("%03d", millis() % 1000) + " : " + s;
        println(ms);
        serverlog.addLine(ms);
    }

    @Override
    public void _draw(int dt) {
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
    }

    @Override
    public void keyReleased() {
        super.keyReleased();
    }

    public static void main(String... args) {
        run(GameServer.class.getSimpleName(),args);
    }
}