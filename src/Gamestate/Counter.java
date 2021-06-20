package Gamestate;

import Schema.SchemaEditable;

@SchemaEditable
public class Counter {
    public int value;

    public Counter(int value) {
        this.value = value;
    }
}
