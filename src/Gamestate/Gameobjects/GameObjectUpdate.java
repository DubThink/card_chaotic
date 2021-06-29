package Gamestate.Gameobjects;

import network.NetEvent;
import network.NetSerializable;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class GameObjectUpdate extends NetSerializable {

    public GameObjectUpdate(){}

    public GameObjectUpdate(DataInputStream dis) throws IOException {}

    public abstract int eventTypeIdentifier();

    @Override
    public abstract String toString();
}
