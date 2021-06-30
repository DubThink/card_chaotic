package Gamestate;

import Render.CardBackRenderer;
import Schema.SchemaTypeID;
import Schema.VersionMismatchException;
import Schema.VersionedSerializable;
import Globals.Debug;
import Globals.GlobalEnvironment;
import Globals.Style;
import aew.Util;
import core.AdvancedApplet;
import core.AdvancedGraphics;
import network.NetSerializerUtils;
import processing.core.PApplet;
import processing.core.PImage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static Globals.Debug.perfTimeMS;
import static core.AdvancedApplet.CC_BOLD;
import static core.AdvancedApplet.CC_ITALIC;
import static processing.core.PApplet.*;

/**
 * Defines the invariable portions of a card (used to generate cards)
 */
public class CardDefinition extends VersionedSerializable {
    private static final int SCHEMA_VERSION_NUMBER = 3;

    public static final int ARCHETYPE_OBJECT=0;
    public static final int ARCHETYPE_BEING=1;
    public static final int ARCHETYPE_GEAR=2;
    public static final int ARCHETYPE_ACTION=3;
    public static final int _ARCHETYPE_COUNT=4;

    public final int uid;

//    public int creatorid;

    public String name;
    public String type;
    public String desc;
    public String flavor;

    public int attackDefaultValue;
    public int healthDefaultValue;
    public int archetype;

    boolean hasCounter;
    int counterDefaultValue;

    private PImage sourceImage;
    int imageDisplayMode = IMAGE_SHOW_FULL;
    float u1,v1,u2,v2;

    public int authorAccountUID;

    // LOCAL ONLY
    public String localSourceImageFilename;
    private boolean localSourceLoaded;

    private PImage renderedBase;
    private boolean baseInvalidated;
    private PImage renderedImage;
    private boolean imageInvalidated;

    // STATIC GEN
    private static AdvancedGraphics renderTarget;

    // CARD BACK
    private static AdvancedGraphics cardBackTarget;
    private static CardBackRenderer cardBackRenderer;

    // CONSTANTS
    public static final int CARD_SCALE = 22;
    public static final int CARD_WIDTH = CARD_SCALE *20;
    public static final int CARD_HEIGHT = CARD_SCALE *28;
    public static final float CARD_H_W = CARD_WIDTH/2f;
    public static final float CARD_H_H = CARD_HEIGHT/2f;

    private static final int IMAGE_SHOW_FULL = 0;
    private static final int IMAGE_SHOW_SMALL = 1;
    private static final int IMAGE_SHOW_SQUARE = 2;

    public CardDefinition(int uid, int authorAccountUID) {
        this(uid,authorAccountUID,"","","","","");
    }

    public CardDefinition(int uid, int authorAccountUID, String name, String type, String desc, String flavor, String imageFileName) {
        this.uid = uid;
        this.authorAccountUID = authorAccountUID;
        this.name = name;
        this.type = type;
        this.desc = desc;
        this.flavor = flavor;
        this.localSourceImageFilename = imageFileName;

        u1=0;
        v1=0;
        u2=1;
        v2=1;
    }

    public CardDefinition(DataInputStream dis) throws IOException {
        super(dis);
        deserializeVersioned(dis);
        uid = dis.readInt();
    }


    // ------ SCHEMA ------ //


    @Override
    public int getVersionNumber() {
        return SCHEMA_VERSION_NUMBER;
    }

    @Override
    public int getSchemaType() {
        return SchemaTypeID.CARD_DEFINITION;
    }


    private static void prepareRenderTargets(AdvancedApplet a){
        if(renderTarget == null){
            renderTarget = (AdvancedGraphics) a.createGraphics(CARD_WIDTH, CARD_HEIGHT, "core.AdvancedGraphics");
            renderTarget.initializeInjector();
            //renderTarget.noSmooth();
        }
        if(cardBackTarget == null){
            cardBackTarget = (AdvancedGraphics) a.createGraphics(CARD_WIDTH, CARD_HEIGHT, "core.AdvancedGraphics");
            cardBackTarget.initializeInjector();
        }
    }


    // ------ PUBLIC RENDERING ACCESS ------ //


    PImage getRenderedBase(AdvancedApplet a){
        if(renderedBase==null||baseInvalidated){
            refreshBase(a);
        }
        return renderedBase;
    }

    private CardDefinition refreshBase(AdvancedApplet a){
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
        baseInvalidated=false;
        return this;
    }

    public PImage getRenderedImage(AdvancedApplet a) {
        if(renderedImage==null|| imageInvalidated) {
            refreshImage(a);
        }
        return renderedImage;
    }

    private CardDefinition refreshImage(AdvancedApplet a){
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
        imageInvalidated =false;
        return this;
    }

    public void drawPreview(AdvancedApplet a, float x, float y, float scale) {
        float startTime = perfTimeMS();
        a.pushMatrix();
        a.translate(x,y);
        a.scale(scale);
        a.image(getRenderedBase(a),0,0);
        renderText(a.getAdvGraphics());
        a.popMatrix();
        Debug.perfView.cardRendersGraph.addVal(Debug.perfTimeMS() - startTime);
    }

    public CardDefinition invalidateBase(){
        baseInvalidated=true;
        imageInvalidated=true;
        return this;
    }

    public CardDefinition invalidateImage(){
        imageInvalidated=true;
        return this;
    }


    // ------ CARD BACK ------ //


    public static void updateCardBack(AdvancedApplet a, int dt){
        float startTime = perfTimeMS();
        prepareRenderTargets(a);

        float t= a.millis()/1000f;

        cardBackTarget.smooth(8);
        cardBackTarget.beginDraw();

        //cardBackTarget.pushMatrix();
        //cardBackTarget.translate(-CARD_SCALE*2.5f,-CARD_SCALE*2.5f);
        cardBackTarget.pushStyle();
        cardBackRenderer.updateCardBack(a,cardBackTarget,t,dt);
        cardBackTarget.popStyle();
        //cardBackTarget.popMatrix();

        AdvancedGraphics p = cardBackTarget;

        // black blocks
        p.fill(0);
        p.noStroke();
        int U=CARD_SCALE;
        p.rect(U,U,U*1.5f,CARD_HEIGHT-U);
        p.rect(CARD_WIDTH-U*2.5f,U,U*1.5f,CARD_HEIGHT-U);
        p.rect(U,U,CARD_WIDTH-U,U*1.5f);
        p.rect(U,CARD_HEIGHT-U*2.5f,CARD_WIDTH-U,U*1.5f);

        // borders
        p.noFill();
        p.stroke(255);
//        p.expertStrokeWeight(7);
//        p.rect(CARD_SCALE, CARD_SCALE, CARD_WIDTH-CARD_SCALE*2, CARD_HEIGHT-CARD_SCALE*2, CARD_SCALE /5f);
//        p.rect(CARD_SCALE, CARD_SCALE, CARD_WIDTH-CARD_SCALE*2, CARD_HEIGHT-CARD_SCALE*2, CARD_SCALE /5f);
        // bootleg strokeWeight
        for (int i=0;i<7;i+=1)
            p.rect(CARD_SCALE-i, CARD_SCALE-i, 2*i+CARD_WIDTH-CARD_SCALE*2, 2*i+CARD_HEIGHT-CARD_SCALE*2, CARD_SCALE /5f);
//        p.strokeWeight(2);
        p.rect(CARD_SCALE*2, CARD_SCALE*2, CARD_WIDTH-CARD_SCALE*4, CARD_HEIGHT-CARD_SCALE*4, 5 );


        // center logo thing
        p.noStroke();
        p.fill(20);
        p.ellipse(CARD_H_W,CARD_H_H, 120,120);
        p.noFill();
        p.stroke(255);
        //p.strokeWeight(2);
        p.ellipse(CARD_H_W,CARD_H_H, 100,100);
        p.ellipse(CARD_H_W,CARD_H_H, 101,101);
        p.ellipse(CARD_H_W,CARD_H_H, 102,102);

        cardBackTarget.endDraw();

        Debug.perfView.cardBackRenderMS = (Debug.perfTimeMS() - startTime);
    }

    public static PImage getCardBack(){
        return cardBackTarget;
    }

    public static void setCardBackRenderer(CardBackRenderer renderer)  {
        cardBackRenderer = renderer;
    }


    // ------ BASE RENDERING ------ //


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
        //p.strokeWeight(2);
        p.noStroke();
        p.fill(0);

        // background
        p.rect(0, 0, CARD_WIDTH, CARD_HEIGHT, CARD_SCALE /2f);


        getSourceImage();
        // image
        p.texture(sourceImage);
        if (imageDisplayMode == IMAGE_SHOW_FULL) {
            p.image(sourceImage, 0, 0, CARD_WIDTH, CARD_HEIGHT,
                    (int) (u1 * sourceImage.width), (int) (v1 * sourceImage.height),
                    (int) (u2 * sourceImage.width), (int) (v2 * sourceImage.height));
        } else if (imageDisplayMode == IMAGE_SHOW_SQUARE) {
            p.image(sourceImage, 0, 0, CARD_WIDTH, m(18),
                    (int) (u1 * sourceImage.width), (int) (v1 * sourceImage.height),
                    (int) (u2 * sourceImage.width), (int) (v2 * sourceImage.height));
        } else {
            p.image(sourceImage, 0, m(3), CARD_WIDTH, m(13),
                    (int) (u1 * sourceImage.width), (int) (v1 * sourceImage.height),
                    (int) (u2 * sourceImage.width), (int) (v2 * sourceImage.height));
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

    public static void renderShapeRect(AdvancedGraphics p, int x, int y, float scale) {
        p.rect(x,y, CARD_WIDTH*scale, CARD_HEIGHT*scale, scale*CARD_SCALE / 2f);
    }

    public static void renderShapeRectExpand(AdvancedGraphics p, int x, int y, float scale, float expand) {
        p.rect(x-expand,y-expand, CARD_WIDTH*scale + expand*2, CARD_HEIGHT*scale + expand*2, scale*CARD_SCALE / 2f);
    }


    // ------ TEXT RENDERING ------ //


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

        p.textAlign(RIGHT, CENTER);
        switch (archetype){
            case ARCHETYPE_OBJECT:
                break;
            case ARCHETYPE_BEING:
                p.text(CC_BOLD +(attackDefaultValue+"/"+healthDefaultValue), m(19), m(17));
                break;
            case ARCHETYPE_GEAR:
                p.text(CC_ITALIC +"Gear", m(19), m(17));
                break;
            case ARCHETYPE_ACTION:
                p.text(CC_ITALIC +"Action", m(19), m(17));
                break;
            default:
                throw new RuntimeException("Not all archetypes implemented in render");
        }

        p.textAlign(LEFT, CENTER);
        Style.getFont(Style.F_STANDARD,Style.FONT_24).apply(p);
        String hyperDesc = AdvancedApplet.hyperText(desc);
        p.textClipped(hyperDesc, 0, m(1), m(21.5f), CARD_WIDTH-m(2), m(7));

        p.textAlign(LEFT);
        Style.getFont(Style.F_FLAVOR,Style.FONT_24).apply(p);
        p.text(flavor, m(1), m(27));
    }

    private float m(float v){
        return v * CARD_SCALE;
    }


    // ------ RENDER HELPERS ------ //


    private static void fadeLine(AdvancedGraphics p, float x1, float y1, float x2, float y2, float alphamult, float alphabase){
        p.beginShape();
        for(float i=0;i<1.05;i+=.1){
            float alpha = PApplet.sin(i*PI)*alphamult+alphabase;
            p.stroke(255,alpha*255);
            p.vertex(Util.lerp(i,x1,x2),Util.lerp(i,y1,y2));
        }
        p.endShape(LINE);
    }


    // ------ CROP ------ //


    public CardDefinition refreshCrop(){
        if(imageDisplayMode==IMAGE_SHOW_FULL)
            setCropCenteredFull();
        else if(imageDisplayMode==IMAGE_SHOW_SMALL)
            setCropCenteredSmall();
        else
            setCropCenteredSquare();
        return this;
    }

    public CardDefinition setCropCenteredFull(){
        setCropForHeight(CARD_HEIGHT);
        if(imageDisplayMode!=IMAGE_SHOW_FULL)
            invalidateBase();
        imageDisplayMode = IMAGE_SHOW_FULL;
        return this;
    }

    public CardDefinition setCropCenteredSmall(){
        setCropForHeight(13*CARD_SCALE);
        if(imageDisplayMode!=IMAGE_SHOW_SMALL)
            invalidateBase();
        imageDisplayMode = IMAGE_SHOW_SMALL;
        return this;
    }

    public CardDefinition setCropCenteredSquare(){
        setCropForHeight(18*CARD_SCALE);
        if(imageDisplayMode!=IMAGE_SHOW_SQUARE)
            invalidateBase();
        imageDisplayMode = IMAGE_SHOW_SQUARE;
        return this;
    }

    private void setCropForHeight(int height){
        getSourceImage();
        final float cardAspect = CARD_WIDTH / (float)height;
        final float sourceAspect = sourceImage.width/ (float)sourceImage.height;
        //System.out.printf("Card aspect ratio %.3f%n", cardAspect);
        //System.out.printf("Source aspect ratio %.3f%n", sourceAspect);
        if (sourceAspect>cardAspect) {
            // vertically bound
            v1 = 0;
            v2 = 1;
            final float scale = sourceImage.height / (float) height;
            float imageWidth = scale * CARD_WIDTH;
            imageWidth /= sourceImage.width; // to [0,1]
            imageWidth*=.5f;

            assert imageWidth < sourceImage.width;
            u1 = 0.5f - imageWidth;
            u2 = 0.5f + imageWidth;
        } else {
            // horizontally bound
            u1 = 0;
            u2 = 1;
            final float scale = sourceImage.width / (float) CARD_WIDTH;
            float imageHeight = scale * height;
            imageHeight /= sourceImage.height; // to [0,1]
            imageHeight*=.5f;

            assert imageHeight < sourceImage.height;
            v1 = 0.5f - imageHeight;
            v2 = 0.5f + imageHeight;
        }

        //System.out.printf("Setting uv values to (%.3f, %.3f) (%.3f, %.3f)%n",u1,v1,u2,v2);
    }


    // ------ SET/GET ------ //


    public PImage getSourceImage(){
        if(!localSourceLoaded && GlobalEnvironment.imageLoader.isCardImageValid(localSourceImageFilename)){
            sourceImage = GlobalEnvironment.imageLoader.getCardImage(localSourceImageFilename);
            localSourceLoaded=true;
            invalidateBase();
            refreshCrop();
        } else if(sourceImage==null) {
            sourceImage = GlobalEnvironment.imageLoader.nullimg;
            refreshCrop();
        }
        return sourceImage;
    }

    public CardDefinition setLocalImageSource(String s){
        localSourceImageFilename=s;
        localSourceLoaded=false;
        return this;
    }

    public CardDefinition setBeingValues(int attackDefaultValue, int healthDefaultValue){
        this.attackDefaultValue = attackDefaultValue;
        this.healthDefaultValue = healthDefaultValue;
        return this;
    }

    public String validateDefinition(){
        if(sourceImage==null)
            return "Source image not found";
        if(sourceImage.width>3000 ||sourceImage.height>3000)
            return "Source image too big (max 3000x3000)";
        if(name.length()==0)
            return "Name must be filled out";
        if(type.length()==0)
            return "Type must be filled out";
        if(flavor.length()==0)
            return "Flavor must be filled out";
        return null;
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        dos.writeUTF(name);
        dos.writeUTF(type);
        dos.writeUTF(desc);
        dos.writeUTF(flavor);

        dos.writeInt(attackDefaultValue);
        dos.writeInt(healthDefaultValue);
        dos.writeInt(archetype);

        dos.writeBoolean(hasCounter);
        dos.writeInt(counterDefaultValue);

        NetSerializerUtils.serializeImage(getSourceImage(), dos);
        dos.writeInt(imageDisplayMode);
        dos.writeFloat(u1);
        dos.writeFloat(v1);
        dos.writeFloat(u2);
        dos.writeFloat(v2);

        dos.writeInt(authorAccountUID);

        // FINAL VALUES BELOW
        dos.writeInt(uid);
    }

    @Override
    protected void deserializeFromVersion(DataInputStream dis, int dataVersion) throws IOException {
        if(dataVersion<2)
            throw new VersionMismatchException(dataVersion, getVersionNumber(), getSchemaType());

        name = dis.readUTF();
        type = dis.readUTF();
        desc = dis.readUTF();
        flavor = dis.readUTF();

        attackDefaultValue = dis.readInt();
        healthDefaultValue = dis.readInt();
        archetype = dis.readInt();

        hasCounter = dis.readBoolean();
        counterDefaultValue = dis.readInt();

        sourceImage = NetSerializerUtils.deserializeImage(dis);
        imageDisplayMode = dis.readInt();
        u1 = dis.readFloat();
        v1 = dis.readFloat();
        u2 = dis.readFloat();
        v2 = dis.readFloat();

        if(dataVersion>2)
            authorAccountUID = dis.readInt();
        else {
            System.out.println("Versioning for V2");
            authorAccountUID = 0;
        }
    }

    @Override
    public String toString() {
        return "CardDefinition{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", desc='" + desc + '\'' +
                ", flavor='" + flavor + '\'' +
                ", attackDefaultValue=" + attackDefaultValue +
                ", healthDefaultValue=" + healthDefaultValue +
                ", archetype=" + archetype +
                ", hasCounter=" + hasCounter +
                ", counterDefaultValue=" + counterDefaultValue +
                ", sourceImage=" + sourceImage +
                ", imageDisplayMode=" + imageDisplayMode +
                ", u1=" + u1 +
                ", v1=" + v1 +
                ", u2=" + u2 +
                ", v2=" + v2 +
                ", localSourceImageFilename='" + localSourceImageFilename + '\'' +
                '}';
    }

    public static String getArchetypeName(int archetype){
        return switch (archetype) {
            case ARCHETYPE_OBJECT -> "Object";
            case ARCHETYPE_BEING -> "Being";
            case ARCHETYPE_GEAR -> "Gear";
            case ARCHETYPE_ACTION -> "Action";
            default -> throw new RuntimeException("Not all archetypes covered");
        };
    }
}
