package UI;

import Gamestate.CardDefinition;
import Gamestate.CardStack;
import Gamestate.CardStackListener;
import Globals.Style;
import core.AdvancedApplet;

import java.util.ArrayList;

public class UICardStackView extends UIBase implements CardStackListener {
    protected ArrayList<UICardView> cardViews;
    protected float cardRenderScale;
    CardStack stack;

    public UICardStackView(int x, int y, int w, int h, float scale) {
        super(x, y, w, h, UILayer.FIELD);
        cardRenderScale = scale;
        cardViews = new ArrayList<>();
    }

//    public UICardStackView(int x, int y, int w, int h, UILayer layer) {
//        super(x, y, w, h, layer);
//        cardRenderScale = scale;
//
//    }

    public UICardStackView setStack(CardStack newStack){
        if(stack!=null)
            stack.removeListener(this);
        stack=newStack;
        stack.addListener(this);
        rebuild();
        return this;
    }

    protected void rebuild() {
        // discard
        for(UICardView cv: cardViews){
            removeChild(cv);
        }
        cardViews.clear();

        if(stack==null)
            return;

        // build
        for (int i = 0; i < stack.getCardCount(); i++) {
            if(stack.getCardByIdx(i)!=null)
                addedCard(stack, i);
        }
    }


    protected UICardView addCardView(UICardView cv){
        cardViews.add(cv);
        return addChild(cv);
    }

    @Override
    protected void _draw(AdvancedApplet p) {
        p.stroke(Style.borderColor);
        p.fill(Style.fillColorPanel);
        p.rect(cx, cy, cw, ch, Style.borderRadius);
    }

    @Override
    public void addedCard(CardStack stack, int idx) {
        addCardView(new UICardView(getCardHorizPos(idx),10,cardRenderScale)).setCardView(stack.getCardByIdx(idx), !stack.isOwner());
    }

    @Override
    public void onOwnershipChange(CardStack stack, boolean isOwner) {
        for (UICardView cardView: cardViews){
            cardView.setReadonly(!isOwner);
        }
    }

    protected int getCardHorizPos(int idx){
        return (int)(10+idx*cardRenderScale*(CardDefinition.CARD_WIDTH+10));
    }
}
