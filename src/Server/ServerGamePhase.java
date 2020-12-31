package Server;

import UI.UIBase;
import network.NetEvent;

public interface ServerGamePhase {
    /**
     * Called once per update step while shouldEnd() returns false
     * @param dt delta time in milliseconds (yes it's an int it's dumb but that's how processing does it)
     */
    void updateStep(int dt);

    /**
     * While shouldEnd() returns false, will be called each update step once per net event received
     * @param event the event to process
     * @return true if the event was handled
     */
    boolean processNetEvent(NetEvent event);

    /**
     * Is tested once at the beginning of each update step. If return value is false, advances phase
     * @return false if the phase should end
     */
    boolean shouldEnd();

    /**
     * @return the next phase, initialized and ready for startup() to be called
     */
    ServerGamePhase createNextPhase();

    void cleanup();

    String getPhaseName();
}
