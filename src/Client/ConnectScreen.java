package Client;

import Gamestate.ClientGamestate;
import UI.*;
import network.NetworkClient;
import processing.core.PConstants;
import static Client.ClientEnvironment.*;
import static Globals.GlobalEnvironment.uiRoot;

public class ConnectScreen {
    UIPanel root;

    UITextBox ipBox;
    UITextBox accountNameBox;
    UITextBox displayNameBox;

    public ConnectScreen(UIPanel root, int width, int height) {
        this.root = root;
        UIPanel leftPanel = root.addChild(new UIPanel(0,0,width/2,0));
        UIPanel rightPanel = root.addChild(new UIPanel(width/2,0,0,0));

        leftPanel.addChild(new UILabel(10,m(0),-10,30,"IP")).setJustify(PConstants.RIGHT).setBigLabel(true);
        ipBox = rightPanel.addChild(new UITextBox(10,m(0),150,30,true)).setText(localPlayerPrefs.lastSuccessfulIP);

        leftPanel.addChild(new UILabel(10,m(1),-10, 30,"Account Name")).setJustify(PConstants.RIGHT).setBigLabel(true);
        accountNameBox = rightPanel.addChild(new UITextBox(10,m(1),180,30,true))
                .setText(localPlayerPrefs.accountName)
                .setEditable(localPlayerPrefs.accountName.length()==0);

        leftPanel.addChild(new UILabel(10,m(2),-10, 30, "Display Name")).setJustify(PConstants.RIGHT).setBigLabel(true);
        displayNameBox = rightPanel.addChild(new UITextBox(10,m(2),180,30,true))
                .setText(localPlayerPrefs.lastDisplayName);

        rightPanel.addChild(new UIButton(10, m(3),90,30,"Connect",this::actionConnectButton));
    }

    private UIModal connectingStatusModal;

    private void actionConnectButton(){
        if(netClient != null && netClient.isAlive()) {
                throw new RuntimeException("how");
        } else {
            netClient = new NetworkClient(ipBox.getText());

            ClientGamestate.accountName = accountNameBox.getText();
            ClientGamestate.displayName = displayNameBox.getText();

            netClient.notifyConnectionFailed = this::notifyConnectionDropped;
            netClient.notifyConnected = this::notifyConnected;

            netClient.start();

            connectingStatusModal = root.addChild(new UIModal(UIModal.MODAL_INFO_ONLY, "Connecting..."));
        }
    }

    private void notifyConnectionDropped(Exception e){
        root.setEnabled(true);
        if(connectingStatusModal!=null) {
            connectingStatusModal.closeNegative();
            connectingStatusModal=null;
        }
        String etext = e.getLocalizedMessage().length()>60?e.getLocalizedMessage().substring(0,60)+"...":e.getLocalizedMessage();
        root.addChild(new UIModal(UIModal.MODAL_CONTINUE, "Connection failed:\n'"+etext+"'"));
    }

    private void notifyConnected(){
        if(connectingStatusModal!=null) {
            connectingStatusModal.closePositive();
            connectingStatusModal=null;
        }

        localPlayerPrefs.lastDisplayName = displayNameBox.getText();
        localPlayerPrefs.lastSuccessfulIP = ipBox.getText();
        localPlayerPrefs.accountName = accountNameBox.getText();
        savePlayerPrefs();

        root.setEnabled(false);
        if(syncModal!=null)
            syncModal.closePositive();
        syncModal = uiRoot.addChild(new UIModal(UIModal.MODAL_INFO_ONLY, "Syncing game state..."));
    }

    public void toggle(){
        root.setEnabled(!root.isEnabled());
    }

    private int m(int p){
        return 360+40*p;
    }
}
