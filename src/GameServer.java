import Gamestate.Account;
import Gamestate.Card;
import Gamestate.CardStack;
import Gamestate.Gameobjects.GameObject;
import Gamestate.Gameobjects.GameObjectManager;
import Gamestate.Gameobjects.GameObjectTransferEvent;
import Gamestate.Gameobjects.GameObjectUpdateEvent;
import Globals.Config;
import Globals.GlobalEnvironment;
import Globals.Style;
import Schema.AccountManager;
import Schema.DiskUtil;
import Server.*;
import UI.*;
import core.AdvancedGraphics;
import network.NetClientHandshake;
import network.NetEvent;
import network.NetServerHandshake;
import network.NetworkClientHandler;
import network.event.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import static Server.ServerEnvironment.*;
import static Globals.GlobalEnvironment.*;
import static com.jogamp.newt.event.KeyEvent.VK_F6;
import static network.NetEvent.LOCAL_USER;
import static network.NetEvent.SERVER_USER;

public class GameServer extends GameBase {

    public void settings() {
        super.settings();
        size(1200, 900, "core.AdvancedGraphics");
//        fullScreen(P3D,-2);
        SERVER=true;
    }

    static {
        Globals.Debug.stateDebug = new ServerDebugPanel();
    }

    ServerSocket ss;
    Exception lastE;

    UITextBox chatBox;

    ServerGamePhase currentPhase;

    UILabel phaseLabel;

    UITabWell tabWell;

    UIPanel cardControlPanel;
    UIPanel accountControlPanel;

    UIPanel devPanel;

    CardStack testStack;

    @Override
    public void setup() {
        super.setup();

        serverlog = uiRoot.addChild(new UILogView(810, 10, -10, -40));

        accountManager = DiskUtil.tryToLoadFromFileTyped(AccountManager.class, AccountManager.FILE_NAME);
        if(accountManager==null){
            svErr("WARNING: NO ACCOUNT DB FOUND");
            accountManager = new AccountManager();
        }


        chatBox = uiRoot.addChild(new UITextBox(100, -25, 400, 25, true));
        chatBox.setFontFamily(Style.F_CODE);

        chatBox.textSubmitted = source -> {
            svLog("Sending message");
            broadcast(new ChatMessageNetEvent(source.getText()), false);
            source.clearText();
        };

//        uiRoot.addChild(new UIButton(10, 10, 100, 100, "add", new Action() {
//            @Override
//            public void action() {
//                svLog("big dick energy");
//            }
//        }));

        tabWell = uiRoot.addChild(new UITabWell(10,45,790,-40));
        phasePanel = tabWell.addTab("Phase Control");
        cardControlPanel = tabWell.addTab("Card Library");
        accountControlPanel = tabWell.addTab("Accounts");
        if(DEV_MODE) {
            devPanel = tabWell.addTab("Dev");
            devPanel.addChild(new UIButton(10,10,150,30, "add card",
                    () ->testStack.addCard( new Card(cardSourceManager.randomCardSource().definition).initializeCard())));
            devPanel.addChild(new UIButton(10,10+40,150,30, "transfer ownership to P1",
                    () -> testStack.giveOwnershipToPeer(1)));


        }

        //Style.font32.font.initInjection();
        ((AdvancedGraphics) g).initializeInjector();
        try {
            ss = new ServerSocket(5056);
            ss.setSoTimeout(16);

        } catch (IOException e) {
            lastE = e;
            ss = null;

        }

        GlobalEnvironment.netInterface = new ServerNetClient();

        svPlayers = new ArrayList<>();

        currentPhase = new PregamePhase();

        phaseLabel = uiRoot.addChild(new UILabel(10,10,200,30,currentPhase.getPhaseName()));
        phaseLabel.setEnabled(false);

//        getSurface().setLocation(10,30);

        // ==== SET UP CARD CONTROL ==== //
        cardSourceManager.setupControlPanel(cardControlPanel);
        cardSourceManager.loadCardLibraryFromDisk();
        accountManager.setupControlPanel(accountControlPanel);



        // TEST CARD
        testStack = new CardStack(LOCAL_USER);
        CardSource s = DiskUtil.tryToLoadFromFileTyped(CardSource.class, "C:\\devspace\\doxo\\data\\server\\cards/card_12.card");
        Card testCard = new Card(s.definition);
        testCard.initializeCard();
        testStack.addCard(testCard);

        super.finalizeSetup();
    }



    @Override
    public void _update(int dt) {
        while(currentPhase.shouldEnd()){
            currentPhase.cleanup();
            svLog("Advancing from phase "+currentPhase.getPhaseName());
            currentPhase = currentPhase.createNextPhase();
            phaseLabel.setText(currentPhase.getPhaseName());
        }

        fill(255, 0, 0);
        if (ss != null) {
            networkUpdateStep(dt);
        } else {
            Style.getFont(Style.F_STANDARD, Style.FONT_HUGE).apply(this);
            text("UNABLE TO START SOCKET RIP", 100, 100);

        }

        Style.getFont(Style.F_CODE, Style.FONT_MEDIUM).apply(this);
        if (lastE != null)
            text(lastE.toString(), 100, 200);

        currentPhase.updateStep(dt);

        cardSourceManager.cardSaveUpdate(dt);

    }

    protected boolean globalProcessNetEvent(SvPlayer player, NetEvent event){
        if(event instanceof RequestCardIDNetEvent){
            if(player.currentNewCardID!=-1){
                // player already has a new card allocated
                send(player, new GrantCardIDNetEvent(player.currentNewCardID));
            } else {
                CardSource newSource = cardSourceManager.allocateNextCardSource(player.player.account.accountUID);
                player.currentNewCardID = newSource.definition.uid;
                send(player, new GrantCardIDNetEvent(newSource.definition.uid));
            }
            return true;
        }
        if(event instanceof UpdateCardDefinitionNetEvent){
            UpdateCardDefinitionNetEvent typedEvent = (UpdateCardDefinitionNetEvent)event;
            // TODO validate that the correct player is the author of this card, etc
            cardSourceManager.applyCardDefinitionUpdate(typedEvent.cardDefinition);
            player.currentNewCardID=-1; // we used the new card, so clear it
            return true;
        }
        if(event instanceof GameObjectUpdateEvent updateEvent) {
            GameObjectManager.handleNetEvent(updateEvent);
            broadcast(updateEvent, false);
            return true;
        }
        if(event instanceof GameObjectTransferEvent transferEvent) {
            GameObjectManager.handleNetEvent(transferEvent);
            broadcast(transferEvent, false);
            return true;
        }
        return false;
    }

    protected void networkUpdateStep(int dt) {
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
                } else if(getPlayerIndexByAccountName(clientHandshake.accountName)==-1){
                    // account is not connected (or does not exist)
                    Account account = accountManager.getAccountByName(clientHandshake.accountName);
                    if(account==null)
                        account = accountManager.createAccount(clientHandshake.accountName);

                    SvPlayer player = playerConnect(account, clientHandshake.displayName, handler);
                    reply.success = true;
                    reply.clientID = player.player.playerIndex;
                    reply.clientPlayer = player.player;
                } else if(getPlayerByAccountName(clientHandshake.accountName).handler.connectionDropped){
                    // account previously connected but dropped
                    SvPlayer player = playerConnect(accountManager.getAccountByName(clientHandshake.accountName), clientHandshake.displayName, handler);
                    reply.success = true;
                    reply.clientID = player.player.playerIndex;
                    reply.clientPlayer = player.player;
                } else {
                    reply.message = "That account is already connected";
                }
                svLog("Handshake with client '"+handler.describeSocket()+"' "+(reply.success?"succeeded":"failed"));
                svLog("    display name='"+clientHandshake.displayName +"'");
                svLog("    account name='"+clientHandshake.accountName +"'");
                if(!reply.success)
                    svErr("    "+reply.message);
                handler.replyServerHandshake(reply);
            }
        }

        for (SvPlayer svPlayer : svPlayers) {
            NetworkClientHandler handler = svPlayer.handler;

            if(!handler.isSynced()){
                syncClient(svPlayer, handler);
            }

            if(handler.isReady()) {
                if (!svPlayer.wasReady) {
                    // on player finish connecting
                    svPlayer.active = true;
                    //todo broadcast playerConnected event
                    svLog("Player connected:"+svPlayer.player);
                }
                while (handler.hasReceivedEvents()) {
                    NetEvent event = handler.pollEvent();
                    svLog("rcvd:" + event.toString());
                    if (!currentPhase.processNetEvent(svPlayer, event) && !globalProcessNetEvent(svPlayer, event)) {
                        broadcast(event, event.shouldReflect()); // Echo server, essentially
                    }
                }

                handler.updateTimeouts(dt);

            } else {
                if(svPlayer.wasReady){
                    svPlayer.active=false;
                    svLog("Player disconnected:"+svPlayer.player);
                    runDropHandling(svPlayer);
                }
            }
            svPlayer.wasReady = handler.isReady();
        }
    }

    protected void syncClient(SvPlayer svPlayer, NetworkClientHandler handler) {
        // send the current game state to client
        svLog("Syncing...");
        cardSourceManager.defineAllCards(handler);
        handler.sendSyncingEvent(new GiveTestCardStackEvent(testStack));
        // TODO finish sync code
        svLog("Synced");
        handler.setSynced(true);
        handler.sendSyncingEvent(new SyncCompleteNetEvent());
    }

    protected void runDropHandling(SvPlayer svPlayer) {
        GameObjectManager.runDropHandling(svPlayer.getPeerID());
        //todo broadcast playerDisconnected
    }

    @Override
    public void keyPressed() {
        super.keyPressed();

        // TODO remove
        if (keyCode == VK_F6) {
            if(testStack!=null)
                openSchema(testStack,false);
            else
                System.err.println("No local player");
        }
    }

    @Override
    public void keyReleased() {
        super.keyReleased();
    }

    public static void main(String... args) {
        run(GameServer.class.getSimpleName(),args);
    }
}