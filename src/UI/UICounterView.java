package UI;

import Gamestate.Counter;
import Globals.Style;
import aew.Util;
import core.AdvancedApplet;
import core.MouseInputHandler;
import processing.core.PConstants;

public class UICounterView extends UICircle {
    protected Counter counter;
    protected int color;
    protected boolean grabbed;
    protected int lastMouseY;
    float shiftAmount;
    protected int tmpVal;

    public UICounterView(int x, int y, int w, Counter counter, int color) {
        super(x, y, w);
        this.counter = counter;
        this.color = color;
    }

    @Override
    protected void _draw(AdvancedApplet p) {
        if(counter==null)
            return;

        p.fill(0,150);
        p.stroke(color);
//        p.getAdvGraphics().expertStrokeWeight(2);
        p.ellipse(ccx, ccy, cr*2, cr*2);
        p.noFill();
        p.ellipse(ccx, ccy, cr*2+1, cr*2+1);
        p.ellipse(ccx, ccy, cr*2+2, cr*2+2);

        float pulseAmt = Style.getPulseAmt(counter.getLastUpdateTimestamp());
        if(pulseAmt > 0.01) {
            p.stroke(Style.pulseColor,Style.getPulseAlpha(pulseAmt));
            p.ellipse(ccx, ccy, cr*2+Style.getPulseSize(pulseAmt)*2, cr*2+Style.getPulseSize(pulseAmt)*2);
        }
        p.getAdvGraphics().strokeWeight(1);
        Style.chooseFont(Style.F_STANDARD,cw*1.5f).apply(p);
        p.textAlign(PConstants.CENTER, PConstants.CENTER);
        p.fill(255);
        p.noStroke();
        if(grabbed) {
            int shift = -(int)(shiftAmount*ch);
            p.fill(255,255,255,Util.lerp(shiftAmount,-.5,.5,255,0));
            dr.text(p,""+(tmpVal+1), ccx, ccy+shift-ch);
            p.fill(255,255,255,Util.lerp(1-Util.abs(shiftAmount),-.5,.5,0,255));
            dr.text(p,""+tmpVal, ccx, ccy+shift);
            p.fill(255,255,255,Util.lerp(shiftAmount,-.5,.5,0,255));
            dr.text(p,""+(tmpVal-1), ccx, ccy+shift+ch);

        } else {
            p.text(counter.getValue(), ccx, ccy);
        }
    }

    public UICounterView setCounter(Counter c){
        setEnabled(c!=null);
        counter=c;
        return this;
    }

    private void mouseUp(){
        grabbed=false;
        if(counter!=null){
            tmpVal-=Math.round(shiftAmount);
            counter.localSetValue(tmpVal);
        }
        shiftAmount=0;
    }

    @Override
    protected void _logicStep(int dt) {
        if(!grabbed)return;
        int dy = app.mouseY-lastMouseY;
        shiftAmount -= dy*0.02;
        if(shiftAmount>.51){
            shiftAmount-=1;
            tmpVal--;
        }

        if(shiftAmount<-.51){
            shiftAmount+=1;
            tmpVal++;
        }

        lastMouseY = app.mouseY;
    }

    @Override
    protected boolean _handleMouseInput(boolean down, int button, int x, int y) {
        if(down && isPointOver(x,y) && !grabbed){
            MouseInputHandler.registerMouseUse(button,this::mouseUp);
            grabbed=true;
            lastMouseY=y;
            if(counter!=null)
                tmpVal = counter.getValue();
            return true;
        } else return isPointOver(x,y);
    }
}
