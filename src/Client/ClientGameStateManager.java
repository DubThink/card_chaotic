package Client;

import network.NetEvent;
import network.event.NotifyPhaseChangeNetEvent;

public class ClientGameStateManager {
    public static final int CLIENT_PHASE_BIDDING=0;
    ClientGamePhase currentPhase;

    public void updateStep(int dt){
        currentPhase.updateStep(dt);
    }

    public boolean handleNetEvent(NetEvent event){
        if(event instanceof NotifyPhaseChangeNetEvent) {
            NotifyPhaseChangeNetEvent tevent = (NotifyPhaseChangeNetEvent) event;
            switch (tevent.newPhase){
                case CLIENT_PHASE_BIDDING:
                    currentPhase = new ClBiddingPhase();
                    break;
                default:
                    throw new RuntimeException("invalid new phase "+tevent.newPhase);
            }
            return true;
        } else {
            return currentPhase.processNetEvent(event);
        }

    }

    public void gotoPhase(ClientGamePhase phase) {
        if(currentPhase != null)
            currentPhase.cleanup();
        currentPhase = phase;
    }
}
