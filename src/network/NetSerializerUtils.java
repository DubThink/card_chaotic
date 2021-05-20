package network;

import Globals.Debug;
import processing.core.PImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class NetSerializerUtils {
    public static <T extends NetSerializable> void serializeArrayList(ArrayList<T> list, DataOutputStream dos) throws IOException {
        dos.writeInt(list.size());
        for(int i=0;i<list.size();i++){
            list.get(i).serialize(dos);
        }
    }

    public static <T extends NetSerializable> void deserializeArrayList(ArrayList<T> list, DataInputStream dis, Deserializer<T> deserializer) throws IOException {
        int size = dis.readInt();
        list.clear();
        list.ensureCapacity(size);
        for(int i=0;i<size;i++){
            list.add(deserializer.deserialize(dis));
        }
    }

    public static void serializeImage(PImage image, DataOutputStream dos) throws IOException {
        float start = Debug.perfTimeMS();
        BufferedImage bimg = (BufferedImage) image.getImage();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bimg, "jpg", byteArrayOutputStream);

        System.out.println("Image size "+byteArrayOutputStream.size());
        dos.writeInt(byteArrayOutputStream.size());
        dos.write(byteArrayOutputStream.toByteArray());
        Debug.perfView.imageTXRXGraph.addVal(Debug.perfTimeMS() - start);

    }

    public static PImage deserializeImage(DataInputStream dis) throws IOException {
        float start = Debug.perfTimeMS();
        int size = dis.readInt();

        if(size>1024*1024*16)
            throw new RuntimeException("Image too big (>16mb)");

        byte[] imageAr = dis.readNBytes(size);

        BufferedImage bimg = ImageIO.read(new ByteArrayInputStream(imageAr));
        Debug.perfView.imageTXRXGraph.addVal(Debug.perfTimeMS() - start);
        return new PImage(bimg);
    }
}
