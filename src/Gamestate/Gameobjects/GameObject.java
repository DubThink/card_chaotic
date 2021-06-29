package Gamestate.Gameobjects;

import Globals.Assert;
import Schema.SchemaEditViewOnly;
import network.NetSerializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.PublicKey;

import static Globals.DBG.*;
import static Globals.GlobalEnvironment.netInterface;
import static network.NetEvent.LOCAL_USER;

public abstract class GameObject extends NetSerializable {
    public static final int OFFLINE_OBJECT = -1;
    @SchemaEditViewOnly
    public final int gameObjectID;

    @SchemaEditViewOnly
    private int owner;

    public GameObject(int owner) {
        this.gameObjectID = GameObjectManager.nextObjectID();
        this.owner = owner;
        GameObjectManager.register(this);
    }

    public GameObject(DataInputStream dis) throws IOException {
        gameObjectID = dis.readInt();
        int own = dis.readInt();
        this.owner = netInterface.translateUserIDToLocal(own);
        System.out.println("recv id="+gameObjectID+" owner="+own+" t="+owner);

        GameObjectManager.netRegister(this);
    }

    /** The method through which all updates to networked data is modified. Called on every box. */
    public abstract void netApplyUpdate(GameObjectUpdate update);

    /** processes an action on the owning machine in a networked fashion. */
    protected void localApplyUpdate(GameObjectUpdate update){
        if(owner==LOCAL_USER) {
            netApplyUpdate(update);
            GameObjectManager.sendObjectUpdate(update, this);
        } else {
            Warning("Attempting to apply update to object we do not own");
        }
    }

    public void giveOwnershipToPeer(int peerID) {
        assertOwned();
        GameObjectManager.giveOwnershipToPeer(this, peerID);
    }

    protected void onReceiveOwnership(){};
    protected void onLoseOwnership(){};

    public int getGameObjectID() {
        return gameObjectID;
    }

    public int getOwner() {
        return owner;
    }

    public boolean isOwner() {
        return owner == LOCAL_USER;
    }

    void setOwner(int owner) {
        System.out.println("setting owner to "+owner);
        boolean wasOwner = isOwner();
        this.owner = owner;
        if(!wasOwner && isOwner())
            onReceiveOwnership();
        if(wasOwner && !isOwner())
            onLoseOwnership();
    }

    protected void assertOwned(){
        Assert.bool(isOwner());
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeInt(gameObjectID);
        dos.writeInt(netInterface.translateUserIDToNet(owner));
        System.out.println("sending id="+gameObjectID+" owner="+owner+" t="+netInterface.translateUserIDToNet(owner));
    }
}
