package Gamestate.Gameobjects;

import Globals.Assert;
import Globals.DBG;

import java.util.ArrayList;

import static Globals.GlobalEnvironment.netInterface;
import static Server.ServerEnvironment.SERVER_ONLY;

public class GameObjectManager {
    private static final ArrayList<GameObject> objects;

    static {
        objects = new ArrayList<>();
    }
    public static void sendObjectUpdate(GameObjectUpdate update, GameObject object) {
        netInterface.sendEvent(new GameObjectUpdateEvent(update,object.getGameObjectID()));
    }

    public static void handleNetEvent(GameObjectUpdateEvent updateEvent) {
        GameObject object = getObjectByID(updateEvent.objectID);
        if(object==null)
            DBG.Warning("Received update for object #"+updateEvent.objectID+" but do not have it registered");
        else
            object.netApplyUpdate(updateEvent.update);
    }

    public static void handleNetEvent(GameObjectTransferEvent transferEvent) {
        GameObject object = getObjectByID(transferEvent.objectID);
        if(object==null)
            DBG.Warning("Received transfer for object #"+transferEvent.objectID+" but do not have it registered");
        else {
            object.setOwner(transferEvent.newOwner);
        }
    }

    // intentionally no modifier to be package-private
    static int nextObjectID() {
        return objects.size();
    }

    // intentionally no modifier to be package-private
    static void register(GameObject object){
        SERVER_ONLY();
        Assert.bool(object.getGameObjectID() == objects.size());
        objects.add(object);
    }

    // intentionally no modifier to be package-private
    static void netRegister(GameObject object){
        // make sure we aren't registering an object that's already registered
        // actually I don't think we need this but leaving it for now
        //Assert.bool(getObjectByID(object.getGameObjectID()) == null);
        objects.ensureCapacity(object.getGameObjectID());
        int csize = objects.size()-1;
        while (csize++ < object.getGameObjectID())
            objects.add(null);
        objects.set(object.getGameObjectID(), object);
    }

    static void giveOwnershipToPeer(GameObject object, int newPeer){
        object.assertOwned();
        object.setOwner(newPeer);
        netInterface.sendEvent(new GameObjectTransferEvent(object.gameObjectID, newPeer));

    }

    private static GameObject getObjectByID(int id) {
        if(id<objects.size())
            return objects.get(id);
        return null;
    }

}
