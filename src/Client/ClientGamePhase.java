package Client;

import UI.UIBase;
import network.NetEvent;

public interface ClientGamePhase {
    /**
     * Called once per update step
     * @param dt delta time in milliseconds (yes it's an int it's dumb but that's how processing does it)
     */
    void updateStep(int dt);

    /**
     * While shouldEnd() returns false, will be called each update step once per net event received
     * @param event the event to process
     * @return true if the event was handled
     */
    boolean processNetEvent(NetEvent event);

    void cleanup();
}
