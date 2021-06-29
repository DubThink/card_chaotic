package UI;

import Gamestate.Card;
import Gamestate.CardActionManager;
import Gamestate.CardDefinition;
import Globals.GlobalEnvironment;
import Globals.Style;
import core.AdvancedApplet;
import core.AdvancedGraphics;
import processing.core.PConstants;

import static Globals.GlobalEnvironment.modifierCtrl;
import static Globals.GlobalEnvironment.modifierShift;

public class UICardView extends UIBase {
    public Card card;
    public boolean readonly;
    /** renders the card in a slower way that shows text updates in realtime */
    public boolean previewMode;

    UICounterView healthCV,counter1CV;

    float scale;
    public UICardView(int x, int y, float scale) {
        this(x, y, scale, UILayer.INTERFACE);
    }
    public UICardView(int x, int y, float scale, UILayer layer) {
        super(x, y, (int)(CardDefinition.CARD_WIDTH* scale), (int)(CardDefinition.CARD_HEIGHT* scale), layer);
        this.scale = scale;
//        healthCV=addChild(new UICounterView(0,0,su(1),null));
        healthCV=addChild(new UICounterView(su(1),su(4),su(2),null, Card.HEALTH_COLOR));
        counter1CV=addChild(new UICounterView(su(1),su(7),su(2),null, Card.COUNTER_COLOR));
    }

    private int su(float u){
        return (int)(this.scale*CardDefinition.CARD_SCALE*u);
    }

    public void centerAt(int x, int y){
        setPos(x-w/2,y-h/2);
    }

    @Override
    protected void _draw(AdvancedApplet p) {
        if(card==null)return;
        healthCV.setEnabled(!card.flipped);
        counter1CV.setEnabled(!card.flipped);
        if(card!=null) {
            if(card.flipped){
                p.image(CardDefinition.getCardBack(), cx, cy, cw, ch);
            } else {
                if (previewMode)
                    card.definition.drawPreview(p, cx, cy, scale);
                else
                    p.image(card.definition.getRenderedImage(p), cx, cy, cw, ch);
            }
        }
        if (card == CardActionManager.getSelection()) {
            p.getAdvGraphics().expertStrokeWeight(4);
            p.stroke(Style.selectionColor);
            p.noFill();
            CardDefinition.renderShapeRect(p.getAdvGraphics(),cx,cy,scale);
        }

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
        setReadonly(readonly);
        healthCV.setCounter(card.health);
        counter1CV.setCounter(card.counter1);
        return this;
    }

    public UICardView setCardDefinitionView(CardDefinition definition){
        setCardView(new Card(definition),true);
        return this;
    }

    public UICardView setCardBackView(){
        setCardDefinitionView(new CardDefinition(-1, -1));
        card.flipped=true;
        return this;
    }

    public void setReadonly(boolean val){
        readonly = val;
        healthCV.setInteractable(!readonly);
        counter1CV.setInteractable(!readonly);
    }

    @Override
    protected boolean _handleMouseInput(boolean down, int button, int x, int y) {
        if (down && button == PConstants.CENTER && isPointOver(x,y) && GlobalEnvironment.DEV_MODE) {
            GlobalEnvironment.openSchema(card,modifierCtrl?false:readonly);
        }
        if (down && button == PConstants.LEFT && isPointOver(x,y)) {
            CardActionManager.selectCard(card);
        }
        return super._handleMouseInput(down, button, x, y);
    }
}
