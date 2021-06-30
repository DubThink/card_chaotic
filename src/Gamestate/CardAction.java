package Gamestate;

import network.NetSerializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class CardAction extends NetSerializable {

    public CardAction(){}

    public CardAction(DataInputStream dis) throws IOException {}

    public abstract int actionTypeIdentifier();

    @Override
    public abstract String toString();

    @Override
    public void serialize(DataOutputStream dos) throws IOException {

    }
}
