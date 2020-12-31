package Server;

import Globals.Config;
import UI.UIBase;
import network.NetEvent;
import network.event.DefineCardNetEvent;

import java.util.ArrayList;

import static Server.ServerEnvironment.*;

public class SvBiddingIntroPhase implements ServerGamePhase {
    ArrayList<CardSource> flight;
    int maxFlightSize;
    int showTimeLeft;
    int voteTimeLeft;
    boolean voting=false;
    int votesBan;
    int votesKeep;
    CardSource currentCard;

    public SvBiddingIntroPhase() {
        this.maxFlightSize = getPlayerCount()*Config.FLIGHT_SIZE_MULTIPLIER;
        flight = new ArrayList<>();
        voteTimeLeft = Config.VOTING_TIME_MS;
    }

    @Override
    public void updateStep(int dt) {
        voteTimeLeft-=dt;
        showTimeLeft-=dt;
        if(voteTimeLeft<0 && showTimeLeft<0){
            // deal with current card
            if(voting){
                if(votesBan >= getPlayerCount()*0.667){
                    cardSourceManager.banCard(currentCard);
                } else {
                    cardSourceManager.allowCard(currentCard);
                    // TODO send AddFlight event
                    flight.add(currentCard);
                    showTimeLeft = Config.SHOW_ADD_TO_FLIGHT_TIME_MS;
                    return;
                }
            }

            // If last card was banned or card has finished showing

            // pick card
            currentCard = cardSourceManager.randomCardSource();

            // update clients
            if(currentCard.hasBeenIntroed){
                // step
                // TODO send AddFlight event
                showTimeLeft = Config.SHOW_ADD_TO_FLIGHT_TIME_MS;
            } else {
                broadcast(new DefineCardNetEvent(currentCard.definition),true);
                // TODO send IntroCard event
                voteTimeLeft = Config.VOTING_TIME_MS;
            }
        }
    }

    @Override
    public boolean processNetEvent(NetEvent event) {
        return false;
    }

    @Override
    public boolean shouldEnd() {
        return flight.size() == maxFlightSize;
    }

    @Override
    public ServerGamePhase createNextPhase() {
        return null;
    }

    @Override
    public void cleanup() {

    }

    @Override
    public String getPhaseName() {
        return "ServerBiddingIntro";
    }
}
