package Client;

import Gamestate.CardDefinition;
import UI.UIBase;
import UI.UILabel;
import UI.UILogView;
import network.NetEvent;
import network.event.AddFlightNetEvent;

import java.util.ArrayList;

import static Client.ClientEnvironment.*;
import static Globals.GlobalEnvironment.*;

public class ClBiddingPhase implements ClientGamePhase {
    ArrayList<CardDefinition> flight;
    UILabel test;
    int t;

    public ClBiddingPhase() {
        flight = new ArrayList<>();
        test = uiRoot.addChild(new UILabel(10, 10, 200, 200,"In Bidding phase"));
        t=0;
    }



    @Override
    public void updateStep(int dt) {
        t+=dt;
        test.setText("Bidding "+t+"ms");

    }

    @Override
    public boolean processNetEvent(NetEvent event) {
        if(event instanceof AddFlightNetEvent){
            AddFlightNetEvent tevent = (AddFlightNetEvent)event;
            flight.add(tevent.cardDefinition);
            //sysMessage("Added card "+tevent.cardDefinition+" to flight.");
        }
        return false;
    }

    @Override
    public void cleanup() {
        uiRoot.removeChild(test);
    }
}
