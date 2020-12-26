package UI;

public interface UIUpdateNotify<E extends UIBase> {
    void notify(E source);
}
