package Gamestate;

public enum CounterTypes {
    Earth(0, "/E"),
    Air(1, "/A"),
    Fire(2, "/F"),
    Water(3, "/W"),
    Cool(4, "/C"),
    Love(5, "/L"),
    Power(6, "/P"),
    Death(7, "/D"),
    ;

    public static final int COUNTER_TYPE_COUNT = 8;
    public final int idx;
    public final String displayName;

    CounterTypes(int idx, String displayName) {
        this.idx = idx;
        this.displayName = displayName;
    }
}
