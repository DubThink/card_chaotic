package network.event;

import network.NetEvent;
import network.NetSerializerUtils;
import processing.core.PImage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static network.NetEventTypeID.CACHE_IMAGE;

public class CacheImageNetEvent extends NetEvent {
    public PImage image;
    public String name;

    public CacheImageNetEvent(PImage image, String name) {
        this.image = image;
        this.name = name;
    }

    public CacheImageNetEvent(DataInputStream dis) throws IOException {
        super(dis);
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        NetSerializerUtils.serializeImage(image,dos);
        dos.writeUTF(name);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        super.deserialize(dis);
        image = NetSerializerUtils.deserializeImage(dis);
        name = dis.readUTF();
    }

    @Override
    public int eventTypeIdentifier() {
        return CACHE_IMAGE;
    }

    @Override
    public String toString() {
        return "CacheImageNetEvent(#"+serial+", @"+authorID+"){" +
                "image=" + image +
                ", cacheName='" + name + '\'' +
                '}';
    }
}
