package Gamestate;

import Globals.Debug;
import Globals.GlobalEnvironment;
import Globals.Style;
import bpw.Util;
import core.AdvancedApplet;
import core.AdvancedGraphics;
import network.NetSerializable;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static Globals.Debug.perfTimeMS;
import static core.AdvancedApplet.CC_BOLD;
import static processing.core.PApplet.sqrt;
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
    public PImage renderedBase;
    public PImage renderedImage;

    // STATIC GEN
    private static AdvancedGraphics renderTarget;

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
        return sqrt(c);//sin(c*90);
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

    private static void prepareRenderTargets(AdvancedApplet a){
        if(renderTarget == null){
            renderTarget = (AdvancedGraphics) a.createGraphics(CARD_WIDTH, CARD_HEIGHT, "core.AdvancedGraphics");
            renderTarget.initializeInjector();
            //renderTarget.noSmooth();
        }

    }

    PImage getRenderedBase(AdvancedApplet a){
        float startTime = perfTimeMS();
        if(renderedBase==null){
            refreshBase(a);
        }
        Debug.perfView.cardRendersGraph.addVal(Debug.perfTimeMS() - startTime);
        return renderedBase;
    }

    private void refreshBase(AdvancedApplet a){
        prepareRenderTargets(a);
        if(renderedBase==null || renderedBase.width != CARD_WIDTH || renderedBase.height != CARD_HEIGHT)
            renderedBase = a.createImage(CARD_WIDTH, CARD_HEIGHT, ARGB);

        renderTarget.beginDraw();
        renderBase(renderTarget);
        renderTarget.loadPixels();
        renderedBase.set(0,0,renderTarget);
        renderedBase.mask(getCardMask(a)); // this has to be here and I'm very unclear why
        renderTarget.endDraw();
    }

    public PImage getRenderedImage(AdvancedApplet a) {
        float startTime = perfTimeMS();
        if(renderedImage==null) {
            refreshImage(a);
        }
        Debug.perfView.cardRendersGraph.addVal(Debug.perfTimeMS() - startTime);
        return renderedImage;
    }

    private void refreshImage(AdvancedApplet a){
        float startTime = perfTimeMS();
        prepareRenderTargets(a);
        if (renderedImage == null || renderedImage.width != CARD_WIDTH || renderedImage.height != CARD_HEIGHT)
            renderedImage = a.createImage(CARD_WIDTH, CARD_HEIGHT, ARGB);

        // make sure the base is rendered
        getRenderedBase(a);

        // render text layer, and store in renderedImage
        renderTarget.beginDraw();
        renderText(renderTarget);
        textEnhanceFilter(a, renderTarget);
        renderTarget.loadPixels();
        renderedImage.set(0, 0, renderTarget);
        renderTarget.endDraw();

        // render full boi, drawing renderedImage over top
        renderTarget.beginDraw();
        renderTarget.image(renderedBase, 0, 0);
        renderTarget.image(renderedImage, 0, 0);
        renderTarget.loadPixels();
        renderedImage.set(0, 0, renderTarget);
        renderTarget.endDraw();
        Debug.perfView.lastCardRenderMS = Debug.perfTimeMS() - startTime;
    }

    public void drawPreview(AdvancedApplet a, float x, float y, float scale) {
        float startTime = perfTimeMS();
        a.pushMatrix();
        a.translate(x,y);
        a.scale(scale);
        a.image(renderedBase,0,0);
        renderText(a.getAdvGraphics());
        a.popMatrix();
        Debug.perfView.cardRendersGraph.addVal(Debug.perfTimeMS() - startTime);
    }

    static private PImage _cardMask;
    static private PImage getCardMask(AdvancedApplet a){
        if(_cardMask==null) {
            _cardMask = a.createImage(CARD_WIDTH, CARD_HEIGHT, ARGB);

            prepareRenderTargets(a);
            renderTarget.beginDraw();
            renderTarget.background(0);
            renderTarget.noStroke();
            renderTarget.fill(255);

            renderTarget.rect(0, 0, CARD_WIDTH, CARD_HEIGHT, CARD_SCALE / 2f);
            renderTarget.loadPixels();
            _cardMask.set(0,0,renderTarget);
            renderTarget.endDraw();
        }
        return _cardMask;
    }

    private void renderBase(AdvancedGraphics p){
        p.strokeWeight(2);
        p.noStroke();
        p.fill(0);

        // background
        p.rect(0, 0, CARD_WIDTH, CARD_HEIGHT, CARD_SCALE /2f);

        // image
        PImage cardImage = GlobalEnvironment.imageLoader.getCardImage(imageFileName);
        p.texture(cardImage);
        p.image(cardImage,0,0);

        // panels
        p.fill(0,150);
        p.noStroke();
        p.rect(0,0,CARD_WIDTH,m(3));
        p.rect(0,m(16),CARD_WIDTH,m(12));


        // details
        p.noFill();
        p.stroke(255,150);
        fadeLine(p,0,m(18), CARD_WIDTH,m(18),.15f,.15f);
    }

    // renders card to target
    private void renderText(AdvancedGraphics p){
//        p.strokeWeight(2);
//        p.noStroke();
//        p.fill(0);
//
//        p.rect(0, 0, CARD_WIDTH, CARD_HEIGHT, CARD_SCALE /2f);

        // the font we use isn't quite base-aligned right, so tweak it
        final int TBA = 4; // text base alignment

        p.fill(255);

        p.textAlign(LEFT);
        Style.getFont(Style.F_STANDARD,Style.FONT_33).apply(p);
        p.text(CC_BOLD+name,m(1),m(2));

        p.textAlign(LEFT, CENTER);
        Style.getFont(Style.F_STANDARD,Style.FONT_27).apply(p);
        p.text(CC_BOLD+type,m(1),m(17));

        if(isBeing) {
            p.textAlign(RIGHT, CENTER);
            p.text(CC_BOLD +(attackDefaultValue+"/"+healthDefaultValue), m(19), m(17));
        }
        p.textAlign(LEFT, CENTER);
        Style.getFont(Style.F_STANDARD,Style.FONT_24).apply(p);
        p.text(desc,m(1),m(21.5f));

        p.textAlign(LEFT);
        Style.getFont(Style.F_FLAVOR,Style.FONT_24).apply(p);
        p.text(flavor, m(1), m(27));
    }

    private float m(float v){
        return v * CARD_SCALE;
    }

    private static void fadeLine(AdvancedGraphics p, float x1, float y1, float x2, float y2, float alphamult, float alphabase){
        p.beginShape();
        for(float i=0;i<1.05;i+=.1){
            float alpha = PApplet.sin(i*PI)*alphamult+alphabase;
            p.stroke(255,alpha*255);
            p.vertex(Util.lerp(i,x1,x2),Util.lerp(i,y1,y2));
        }
        p.endShape(LINE);
    }

    public CardDefinition setBeingValues(boolean isBeing, int attackDefaultValue, int healthDefaultValue){
        this.isBeing = isBeing;
        this.attackDefaultValue = attackDefaultValue;
        this.healthDefaultValue = healthDefaultValue;
        return this;
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
