package Schema;

import Client.LocalPlayerPrefs;
import Gamestate.CardDefinition;
import Gamestate.PersistentPlayer;
import Globals.Debug;
import Server.CardLibraryMetadata;
import Server.CardSource;

import java.io.*;

import static Globals.Debug.perfTimeMS;

public class DiskUtil {

    public static void saveToFile(VersionedSerializable serializable, String fname){
        try {
            FileOutputStream fout = new FileOutputStream(fname);
            DataOutputStream dos = new DataOutputStream(fout);
            dos.writeInt(serializable.getSchemaType());
            serializable.serialize(dos);
            dos.close();
            fout.close();
        } catch (FileNotFoundException e){
            throw new RuntimeException(e);
        } catch (IOException f){
            throw new RuntimeException(f);
        }
    }

    public static <T extends VersionedSerializable> T tryToLoadFromFileTyped(Class<T> token, String fname){
        try {
            return loadFromFileTyped(token,fname);
        } catch (VersionMismatchException | FileNotFoundException vme){
            System.err.println("Error while loading '"+fname+"':");
            System.err.println(vme);
            return null;
        }
    }

    public static <T extends VersionedSerializable> T loadFromFileTyped(Class<T> token, String fname) throws FileNotFoundException, VersionMismatchException {
        VersionedSerializable vs = loadFromFile(fname);
        return token.cast(vs);
    }

    public static VersionedSerializable tryToLoadFromFile(String fname){
        try {
            return loadFromFile(fname);
        } catch (VersionMismatchException | FileNotFoundException vme){
            System.err.println("Error while loading '"+fname+"':");
            System.err.println(vme);
            return null;
        }
    }

    public static VersionedSerializable loadFromFile(String fname) throws FileNotFoundException, VersionMismatchException {
        try {
            FileInputStream fin = new FileInputStream(fname);
            DataInputStream dis = new DataInputStream(fin);
            int schemaType = dis.readInt();
            VersionedSerializable vs=switch (schemaType){
                case SchemaTypeID.CARD_DEFINITION -> new CardDefinition(dis);
                case SchemaTypeID.CARD_SOURCE -> new CardSource(dis);
                case SchemaTypeID.PERSISTENT_PLAYER -> new PersistentPlayer(dis);
                case SchemaTypeID.CARD_LIBRARY_METADATA -> new CardLibraryMetadata(dis);
                case SchemaTypeID.LOCAL_PLAYER_PREFS -> new LocalPlayerPrefs(dis);
                default -> throw new RuntimeException("Invalid schema type:"+schemaType);
            };
            dis.close();
            fin.close();
            return vs;
        } catch (FileNotFoundException e){
            throw e;
        } catch (VersionMismatchException vme){
            throw vme;
        }catch (IOException f){
            throw new RuntimeException(f);
        }
    }

    public static void main(String[] args) {
        CardSource event = new CardSource(new CardDefinition(-1, "The Golden Judgement", "Exotic Warrior Behemoth", "At the beginning of your turn:\n" +
                "If your /P equals your /D, gain a VP for\neach /P.\n" +
                "Otherwise, loose X VP for the difference\nbetween your /P and /D.", "Power always comes with a cost","gato.jpg"));
        event.submitWinningBid(10);
        event.hasBeenIntroed=true;
        //saveToFile(event,"test.txt");
        float startTime = perfTimeMS();
        LocalPlayerPrefs prefs = new LocalPlayerPrefs();
        saveToFile(prefs,"test.txt");
        System.out.println(Debug.perfTimeMS() - startTime);

        CardSource out= tryToLoadFromFileTyped(CardSource.class,"test.txt");
        System.out.println("done");
    }
}
