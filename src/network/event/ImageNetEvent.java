package network.event;

import Gamestate.CardDefinition;
import Globals.Debug;
import Globals.GlobalEnvironment;
import com.jogamp.nativewindow.CapabilitiesImmutable;
import network.NetEvent;
import network.NetSerializerUtils;
import processing.core.PImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;

import static Client.ClientEnvironment.cardDefinitionManager;
import static network.NetEventTypeID.*;

public class ImageNetEvent extends NetEvent {
    public PImage image;

    public ImageNetEvent(PImage image) {
        this.image = image;
    }

    public ImageNetEvent(DataInputStream dis) throws IOException {
        super(dis);
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        NetSerializerUtils.serializeImage(image,dos);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        super.deserialize(dis);
        image = NetSerializerUtils.deserializeImage(dis);
    }

    @Override
    public int eventTypeIdentifier() {
        return DEFINE_IMAGE;
    }

    @Override
    public String toString() {
        return "ImageNetEvent(#"+serial+", @"+authorID+"){" +
                "image=" + image +
                '}';
    }
}
