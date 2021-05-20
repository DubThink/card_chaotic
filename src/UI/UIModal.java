package UI;

import Globals.Style;
import core.AdvancedApplet;
import processing.core.PApplet;

public class UIModal extends UIBase {

    UIPanel innerModal;
    UILabel message;
    UIButton btnPositive;
    UIButton btnNegative;

    public Action positiveAction;
    public Action negativeAction;

    public static final int MODAL_INFO_ONLY=0;
    public static final int MODAL_CONTINUE =1;
    public static final int MODAL_YES_NO=2;


    public UIModal(int modalType, String msg){
        this(modalType,msg,null,null);
        System.out.println("testistgawhng");
    }

    public UIModal(int modalType, String msg, Action positiveAction){
        this(modalType,msg,positiveAction,null);
    }

    public UIModal(int modalType, String msg, Action positiveAction, Action negativeAction) {
        super(0,0,0,0, UILayer.OVERLAY);

        this.positiveAction=positiveAction;
        this.negativeAction=negativeAction;

        if(modalType==MODAL_INFO_ONLY){
            innerModal = addChild(new UIPanel(10,10, 250, 110));
            message = innerModal.addChild(new UILabel(10,10,-10,-10,msg));
        }
        if(modalType== MODAL_CONTINUE){
            innerModal = addChild(new UIPanel(10,10, 250, 110));
            message = innerModal.addChild(new UILabel(10,10,-10,-45,msg));
            btnPositive = innerModal.addChild(new UIButton(80,-40,-80,-10,"Continue",this::closePositive));
        }

        if(modalType==MODAL_YES_NO){
            innerModal = addChild(new UIPanel(10,10, 250, 110));
            message = innerModal.addChild(new UILabel(10,10,-10,-45,msg));
            btnNegative = innerModal.addChild(new UIButton(30,-40,90,-10,"No",this::closeNegative));
            btnPositive = innerModal.addChild(new UIButton(130,-40,90,-10,"Yes",this::closePositive));
        }
        message.setJustify(PApplet.CENTER);
    }

    @Override
    protected void _updateCalculatedLayout() {
        super._updateCalculatedLayout();
        int newx=(this.cw-250)/2;
        int newy=(this.ch-110)/2;
        innerModal.setPos(newx, newy);
    }

    @Override
    protected void _draw(AdvancedApplet p) {
        p.noStroke();
        p.fill(Style.fillColorModalBG);
        p.rect(cx, cy, cw, ch, Style.borderRadius);
    }

    public void closePositive(){
        if(positiveAction!=null)
            positiveAction.action();
        this.parent.removeChild(this);
    }

    public void closeNegative(){
        if(negativeAction!=null)
            negativeAction.action();
        this.parent.removeChild(this);
    }
}
