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
    int imageDisplayMode = IMAGE_SHOW_FULL;
    float u1,v1,u2,v2;

    // LOCAL ONLY
    public PImage renderedBase;
    public PImage renderedImage;

    // STATIC GEN
    private static AdvancedGraphics renderTarget;

    public static final int CARD_SCALE = 24;
    public static final int CARD_WIDTH = CARD_SCALE *20;
    public static final int CARD_HEIGHT = CARD_SCALE *28;

    private static final int IMAGE_SHOW_FULL = 0;
    private static final int IMAGE_SHOW_SMALL = 1;

    public CardDefinition(int uid, String name, String type, String desc, String flavor, String imageFileName) {
        this.uid = uid;
        this.name = name;
        this.type = type;
        this.desc = desc;
        this.flavor = flavor;
        this.imageFileName = imageFileName;

        u1=0;
        v1=0;
        u2=1;
        v2=1;
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

    public CardDefinition refreshDisplay(AdvancedApplet a){
        refreshBase(a);
        return refreshImage(a);
    }

    PImage getRenderedBase(AdvancedApplet a){
        if(renderedBase==null){
            refreshBase(a);
        }
        return renderedBase;
    }

    public CardDefinition refreshBase(AdvancedApplet a){
        float startTime = perfTimeMS();
        prepareRenderTargets(a);
        if(renderedBase==null || renderedBase.width != CARD_WIDTH || renderedBase.height != CARD_HEIGHT)
            renderedBase = a.createImage(CARD_WIDTH, CARD_HEIGHT, ARGB);

        renderTarget.beginDraw();
        renderTarget.clear();
        renderBase(renderTarget);
        renderTarget.loadPixels();
        renderedBase.set(0,0,renderTarget);
        renderedBase.mask(getCardMask(a)); // this has to be here and I'm very unclear why
        renderTarget.endDraw();
        Debug.perfView.cardRendersGraph.addVal(Debug.perfTimeMS() - startTime);
        return this;
    }

    public PImage getRenderedImage(AdvancedApplet a) {
        if(renderedImage==null) {
            refreshImage(a);
        }
        return renderedImage;
    }

    public CardDefinition refreshImage(AdvancedApplet a){
        float startTime = perfTimeMS();
        prepareRenderTargets(a);
        if (renderedImage == null || renderedImage.width != CARD_WIDTH || renderedImage.height != CARD_HEIGHT)
            renderedImage = a.createImage(CARD_WIDTH, CARD_HEIGHT, ARGB);

        // make sure the base is rendered
        getRenderedBase(a);

        // render text layer, and store in renderedImage
        renderTarget.beginDraw();
        renderTarget.clear();
        renderText(renderTarget);
        textEnhanceFilter(a, renderTarget);
        renderTarget.loadPixels();
        renderedImage.set(0, 0, renderTarget);
        renderTarget.endDraw();

        // render full boi, drawing renderedImage over top
        renderTarget.beginDraw();
        renderTarget.clear();
        renderTarget.image(renderedBase, 0, 0);
        renderTarget.image(renderedImage, 0, 0);
        renderTarget.loadPixels();
        renderedImage.set(0, 0, renderTarget);
        renderTarget.endDraw();
        Debug.perfView.lastCardRenderMS = Debug.perfTimeMS() - startTime;
        Debug.perfView.cardRendersGraph.addVal(Debug.perfTimeMS() - startTime);
        return this;
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
        if (imageDisplayMode == IMAGE_SHOW_FULL) {
            p.image(cardImage, 0, 0, CARD_WIDTH, CARD_HEIGHT,
                    (int) (u1 * cardImage.width), (int) (v1 * cardImage.height),
                    (int) (u2 * cardImage.width), (int) (v2 * cardImage.height));
        } else {
            p.image(cardImage, 0, m(3), CARD_WIDTH, m(13),
                    (int) (u1 * cardImage.width), (int) (v1 * cardImage.height),
                    (int) (u2 * cardImage.width), (int) (v2 * cardImage.height));
        }
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

    public CardDefinition setCropCenteredFull(){
        setCropForHeight(CARD_HEIGHT);
        imageDisplayMode = IMAGE_SHOW_FULL;
        return this;
    }
    public CardDefinition setCropCenteredSmall(){
        setCropForHeight(13*CARD_SCALE);
        imageDisplayMode = IMAGE_SHOW_SMALL;
        return this;
    }

    private void setCropForHeight(int height){
        PImage cardImage = GlobalEnvironment.imageLoader.getCardImage(imageFileName);
        final float cardAspect = CARD_WIDTH / (float)height;
        final float sourceAspect = cardImage.width/ (float)cardImage.height;
        //System.out.printf("Card aspect ratio %.3f%n", cardAspect);
        //System.out.printf("Source aspect ratio %.3f%n", sourceAspect);
        if (sourceAspect>cardAspect) {
            // vertically bound
            v1 = 0;
            v2 = 1;
            final float scale = cardImage.height / (float) height;
            float imageWidth = scale * CARD_WIDTH;
            imageWidth /= cardImage.width; // to [0,1]
            imageWidth*=.5f;

            assert imageWidth < cardImage.width;
            u1 = 0.5f - imageWidth;
            u2 = 0.5f + imageWidth;
        } else {
            // horizontally bound
            u1 = 0;
            u2 = 1;
            final float scale = cardImage.width / (float) CARD_WIDTH;
            float imageHeight = scale * height;
            imageHeight /= cardImage.height; // to [0,1]
            imageHeight*=.5f;

            assert imageHeight < cardImage.height;
            v1 = 0.5f - imageHeight;
            v2 = 0.5f + imageHeight;
        }

        //System.out.printf("Setting uv values to (%.3f, %.3f) (%.3f, %.3f)%n",u1,v1,u2,v2);
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
