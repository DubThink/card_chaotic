package Schema;

import Gamestate.CardDefinition;
import Server.CardSource;

import java.io.*;

public class DiskUtil {

    public void startup(){

    }

    public void saveToFile(VersionedSerializable serializable, String fname){
        try {
            FileOutputStream fout = new FileOutputStream(fname);
            DataOutputStream dos = new DataOutputStream(fout);
            dos.writeInt(serializable.getSchemaType());
            serializable.serialize(dos);
            dos.close();
            fout.close();
        } catch (FileNotFoundException e){
            throw new RuntimeException(e);
        }catch (IOException f){
            throw new RuntimeException(f);
        }
    }

    public <T extends VersionedSerializable> T loadFromFileTyped(Class<T> token, String fname){
        VersionedSerializable vs = loadFromFile(fname);
        return token.cast(vs);
    }

    public VersionedSerializable loadFromFile(String fname){
        try {
            FileInputStream fin = new FileInputStream(fname);
            DataInputStream dis = new DataInputStream(fin);
            int schemaType = dis.readInt();
            VersionedSerializable vs=switch (schemaType){
                case SchemaTypeID.CARD_DEFINITION -> new CardDefinition(dis);
                case SchemaTypeID.CARD_SOURCE -> new CardSource(dis);
                default -> throw new RuntimeException("Invalid schema type:"+schemaType);
            };
            dis.close();
            fin.close();
            return vs;
        } catch (FileNotFoundException e){
            throw new RuntimeException(e);
        }catch (IOException f){
            throw new RuntimeException(f);
        }
    }

    public static void main(String[] args) {
        DiskUtil db = new DiskUtil();
        db.startup();
        CardSource event = new CardSource(new CardDefinition(-1, "The Golden Judgement", "Exotic Warrior Behemoth", "At the beginning of your turn:\n" +
                "If your /P equals your /D, gain a VP for\neach /P.\n" +
                "Otherwise, loose X VP for the difference\nbetween your /P and /D.", "Power always comes with a cost","gato.jpg"));
        event.submitWinningBid(10);
        event.hasBeenIntroed=true;
        db.saveToFile(event,"test.txt");

        CardSource out= db.loadFromFileTyped(CardSource.class,"test.txt");
        System.out.println("done");
    }
}
