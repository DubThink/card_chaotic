package UI;

import Gamestate.Card;
import Gamestate.CardDefinition;
import Globals.Style;
import core.AdvancedApplet;
import core.AdvancedGraphics;

public class UICardView extends UIBase {
    public Card card;
    public boolean readonly;

    float scale;

    public UICardView(int x, int y, float scale, UILayer layer) {
        super(x, y, (int)(CardDefinition.CARD_WIDTH* scale), (int)(CardDefinition.CARD_HEIGHT* scale), layer);
        this.scale = scale;

    }

    @Override
    protected void _draw(AdvancedApplet p) {
        //long nt = System.nanoTime();
        //p.image(imageLoader.getCardImage(card.definition.imageFileName), cx, cy,cw/2f,ch/2f);
        p.smooth();
        p.image(card.definition.getRenderedImage(p), cx, cy,cw,ch);

//        AdvancedGraphics ps = (AdvancedGraphics) p.createGraphics(cw, ch, "core.AdvancedGraphics");
//        ps.initializeInjector();
//        ps.beginDraw();
//        card.definition.render((AdvancedGraphics) ps);
//        ps.endDraw();
//        p.image(ps.textureImage, cx+20, cy+20,cw/2f,ch/2f);
//        //p.image(ps, cx+40, cy+40,cw/2f,ch/2f);
//        PImage img = p.createImage(cw,ch,ARGB);// = ps.copy();
//        //ps.updateTexture();
//        //ps.loadPixels();
//        img.set(0,0,ps);
//        p.image(ps, cx+100, cy+100);

        //long et = System.nanoTime();
        //System.out.println("MS render (1): "+(et-nt)/1000000f);
    }

    @Override
    protected void _debugDraw(AdvancedApplet p) {
        super._debugDraw(p);
        p.fill(0,0,255);
        p.noStroke();
        Style.getFont(Style.F_CODE, Style.FONT_SMALL).apply(p);
        if(card==null)
            p.text("no card", cx+20,cy+20);
        else
            p.text("id:"+card.definition.uid, cx+20,cy+20);
        for (int j=0;j<29;j++){
            if(j%5==0)
                p.stroke(255,127,255,200);
            else
                p.stroke(127,255,255,200);
            p.line(cx,cy+m(j),cx+cw,cy+m(j));
        }
        for(int i=0;i<21;i++){
            if(i%5==0)
                p.stroke(255,127,255,200);
            else
                p.stroke(127,255,255,200);
            p.line(cx+m(i),cy,cx+m(i),cy+ch);

        }
    }

    private float m(float v){
        return v * CardDefinition.CARD_SCALE* scale;
    }

    public UICardView setCardView(Card card, boolean readonly){
        this.card = card;
        this.readonly = readonly;
        return this;
    }

    public UICardView setCardDefinitionView(CardDefinition definition){
        card = new Card(definition);
        readonly = true;
        return this;
    }
}
