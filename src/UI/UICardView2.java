package UI;

import Gamestate.Card;
import Gamestate.CardDefinition;
import Globals.Style;
import core.AdvancedApplet;
import processing.opengl.PGraphicsOpenGL;

import static core.AdvancedApplet.CC_BOLD;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;

public class UICardView2 extends UIBase {
    public Card card;
    public boolean readonly;
    int scale;

    float scaler;
    public static final int CARD_VIEW_TABLE_SCALE = 12;
    public static final int CARD_VIEW_PREVIEW_SCALE = 18;
    public static final int CARD_VIEW_LARGE_SCALE = 12;

    public UICardView2(int x, int y, float scaler, UILayer layer) {
        super(x, y, CARD_VIEW_LARGE_SCALE*20,CARD_VIEW_LARGE_SCALE*28 , layer);
        this.scale = CARD_VIEW_LARGE_SCALE;
        this.scaler=scaler;

    }

    @Override
    protected void _draw(AdvancedApplet p) {
        p.strokeWeight(2);
        p.noStroke();
        p.fill(0);
        p.pushMatrix();
        p.translate(cx, cy);
        p.scale(scaler);

        p.rect(0, 0, cw, ch, scale/2f);
        p.fill(255);
        if(card!=null){
            p.textAlign(LEFT,CENTER);
            Style.getFont(Style.F_STANDARD,Style.FONT_16).apply(p);
            p.text(CC_BOLD+card.definition.name,m(1),m(1.5f));
            Style.getFont(Style.F_STANDARD,Style.FONT_14).apply(p);
            p.text(CC_BOLD+card.definition.type,m(1),m(17));
            Style.getFont(Style.F_STANDARD,Style.FONT_12).apply(p);
            p.text(card.definition.desc,m(1),m(22));
            Style.getFont(Style.F_FLAVOR,Style.FONT_12).apply(p);
            p.textAlign(LEFT);
            p.text(card.definition.flavor, m(1), m(27));
        }
        p.popMatrix();
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
        p.stroke(127,255,255,100);
        for (int j=0;j<29;j++){
            p.line(cx,cy+m(j),cx+cw,cy+m(j));
        }
        for(int i=0;i<21;i++){
            p.line(cx+m(i),cy,cx+m(i),cy+ch);

        }
    }

    private float m(float v){
        return v * scale;
    }

    public UICardView2 setCardView(Card card, boolean readonly){
        this.card = card;
        this.readonly = readonly;
        return this;
    }

    public UICardView2 setCardDefinitionView(CardDefinition definition){
        card = new Card(definition);
        readonly = true;
        return this;
    }
}
