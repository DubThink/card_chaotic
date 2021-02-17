package Gamestate;

import Client.ClientEnvironment;
import Globals.GlobalEnvironment;
import Globals.Style;
import bpw.PUtil;
import core.AdvancedApplet;
import core.AdvancedGraphics;
import network.NetSerializable;
import processing.core.PImage;
import processing.opengl.PGL;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static core.AdvancedApplet.CC_BOLD;
import static processing.core.PConstants.*;

/**
 * Defines the invariable portions of a card (used to generate cards)
 */
public class CardDefinition extends NetSerializable {
    public final int uid;

    public int creatorid;

    public String name;
    public String type;
    public String desc;
    public String flavor;

    boolean isBeing;
    int attackDefaultValue;
    int healthDefaultValue;

    boolean hasCounter;
    int counterDefaultValue;

    public String imageFileName;
    float u1,v1,u2,v2;

    // LOCAL ONLY
    public PImage renderedImage;

    // STATIC GEN
    public static AdvancedGraphics renderTarget;

    public static final int CARD_SCALE = 24;
    public static final int CARD_WIDTH = CARD_SCALE *20;
    public static final int CARD_HEIGHT = CARD_SCALE *28;

    public CardDefinition(int uid, String name, String type, String desc, String flavor, String imageFileName) {
        this.uid = uid;
        this.name = name;
        this.type = type;
        this.desc = desc;
        this.flavor = flavor;
        this.imageFileName = imageFileName;

        u1=0;
        v1=0;
        u2=0;
        v2=0;
    }

    public CardDefinition(DataInputStream dis) throws IOException {
        super(dis);
        uid = dis.readInt();
    }

    private static float colorCurve(float c){
        return c*1.5f;//sin(c*90);
    }

    private static void textEnhanceFilter(AdvancedApplet a,PImage p){
        p.loadPixels();
        for(int i=0;i<p.pixels.length;i++){
            int px=p.pixels[i];
            float r= colorCurve(a.red(px)/255f)*255.f;
            float g= colorCurve(a.green(px)/255f)*255.f;
            float b= colorCurve(a.blue(px)/255f)*255.f;
            float alpha= a.alpha(px);
            p.pixels[i]=a.color(r,g,b,alpha);
        }
        p.updatePixels();
    }
//
//    private static void blit(AdvancedGraphics a, PImage source,PImage dest){
//        source.loadPixels();
//        dest.loadPixels();
//        assert source.width==dest.width && source.height==dest.height && source.format==dest.format;
//        for(int i=0;i<source.pixels.length;i++){
//            dest.pixels[i] = PUtil.mixColor(a, source.pixels[i], dest.pixels[i], a.alpha(dest.pixels[i])/255.0f);
//        }
//        dest.updatePixels();
//    }

    private static void prepareRenderTarget(AdvancedApplet a){
        if(renderTarget == null){
            renderTarget = (AdvancedGraphics) a.createGraphics(CARD_WIDTH, CARD_HEIGHT, "core.AdvancedGraphics");
            renderTarget.initializeInjector();
            //renderTarget.noSmooth();
        }
    }

    public PImage getRenderedImage(AdvancedApplet a){
        if(renderedImage==null) {
            prepareRenderTarget(a);
            if(renderedImage==null || renderedImage.width != CARD_WIDTH || renderedImage.height != CARD_HEIGHT)
                renderedImage = a.createImage(CARD_WIDTH, CARD_HEIGHT, ARGB);

            //render text layer, and store in renderedImage
            renderTarget.beginDraw();
            renderText(renderTarget);
            textEnhanceFilter(a, renderTarget);
            renderTarget.loadPixels();
            renderedImage.set(0,0,renderTarget);
            renderTarget.endDraw();

            // render full boi
            renderTarget.beginDraw();
            renderBase(renderTarget);
            renderTarget.image(renderedImage,0,0);
            renderTarget.loadPixels();
            renderedImage.set(0,0,renderTarget);
            renderTarget.endDraw();



        }

        return renderedImage;
    }

    static private PImage _cardMask;
    static private PImage getCardMask(AdvancedApplet a){
        if(_cardMask==null) {
            prepareRenderTarget(a);
            renderTarget.beginDraw();
            renderTarget.strokeWeight(2);
            renderTarget.noStroke();
            renderTarget.fill(0);

            renderTarget.rect(0, 0, CARD_WIDTH, CARD_HEIGHT, CARD_SCALE / 2f);
            renderTarget.loadPixels();
            _cardMask.set(0,0,renderTarget);
            renderTarget.endDraw();
        }
        return _cardMask;
    }

    public void renderBase(AdvancedGraphics p){
        p.strokeWeight(2);
        p.noStroke();
        p.fill(0);

        p.rect(0, 0, CARD_WIDTH, CARD_HEIGHT, CARD_SCALE /2f);

        PImage cardImage = GlobalEnvironment.imageLoader.getCardImage(imageFileName);
        p.texture(cardImage);
        p.image(cardImage,0,0);

    }

    // renders card to target
    public void renderText(AdvancedGraphics p){
//        p.strokeWeight(2);
//        p.noStroke();
//        p.fill(0);
//
//        p.rect(0, 0, CARD_WIDTH, CARD_HEIGHT, CARD_SCALE /2f);
        p.fill(255);

        p.textAlign(LEFT,CENTER);
        Style.getFont(Style.F_STANDARD,Style.FONT_33).apply(p);
        p.text(CC_BOLD+name,m(1),m(1.5f));
        Style.getFont(Style.F_STANDARD,Style.FONT_27).apply(p);
        p.text(CC_BOLD+type,m(1),m(17));
        Style.getFont(Style.F_STANDARD,Style.FONT_24).apply(p);
        p.text(desc,m(1),m(22));
        Style.getFont(Style.F_FLAVOR,Style.FONT_24).apply(p);
        p.textAlign(LEFT);
        p.text(flavor, m(1), m(27));
    }

    private float m(float v){
        return v * CARD_SCALE;
    }


    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeUTF(name);
        dos.writeUTF(imageFileName);

        // FINAL VALUES BELOW
        dos.writeInt(uid);
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        name = dis.readUTF();
        imageFileName = dis.readUTF();
    }

    @Override
    public String toString() {
        return "CardDefinition{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                '}';
    }
}
