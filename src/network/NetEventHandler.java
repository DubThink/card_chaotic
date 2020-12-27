package network;

public interface NetEventHandler<E extends NetEvent> {
    void handleEvent(E event);
}
