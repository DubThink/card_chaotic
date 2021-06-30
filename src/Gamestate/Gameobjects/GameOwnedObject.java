package Gamestate.Gameobjects;

import java.io.DataInputStream;
import java.io.IOException;

public class GameOwnedObject extends GameObject {
    protected GameObject owningObject;
    public GameOwnedObject(GameObject owningObject) {
        super(owningObject.getOwner());
        this.owningObject = owningObject;
    }

    @Override
    public int getOwner() {
        return owningObject.getOwner();
    }

    @Override
    void setOwner(int owner) {
        throw new RuntimeException("don't set the owner on an owned object");
    }

    void setOwningObject(GameObject owningObject) {

    }


    public GameOwnedObject(DataInputStream dis) throws IOException {
        super(dis);
    }

    @Override
    public void netApplyUpdate(GameObjectUpdate update) {

    }
}
