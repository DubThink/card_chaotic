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
        //p.strokeWeight(2);
        p.ellipse(ccx, ccy, cr*2, cr*2);
        p.ellipse(ccx, ccy, cr*2+1, cr*2+1);
        Style.chooseFont(Style.F_STANDARD,cw*1.5f).apply(p);
        p.textAlign(PConstants.CENTER, PConstants.CENTER);
        p.fill(255);
        p.noStroke();
        if(grabbed) {
            int shift = -(int)(shiftAmount*ch);
            p.fill(255,255,255,Util.lerp(shiftAmount,-.5,.5,255,0));
            p.text(counter.value+1, ccx, ccy+shift-ch);
            p.fill(255,255,255,Util.lerp(1-Util.abs(shiftAmount),-.5,.5,0,255));
            p.text(counter.value, ccx, ccy+shift);
            p.fill(255,255,255,Util.lerp(shiftAmount,-.5,.5,0,255));
            p.text(counter.value-1, ccx, ccy+shift+ch);

        } else {
            p.text(counter.value, ccx, ccy);
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
            counter.value-=Math.round(shiftAmount);
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
            if(counter!=null)
                counter.value--;
        }

        if(shiftAmount<-.51){
            shiftAmount+=1;
            if(counter!=null)
                counter.value++;
        }

        lastMouseY = app.mouseY;
    }

    @Override
    protected boolean _handleMouseInput(boolean down, int button, int x, int y) {
        if(down && isPointOver(x,y) && !grabbed){
            MouseInputHandler.registerMouseUse(button,this::mouseUp);
            grabbed=true;
            lastMouseY=y;
            return true;
        } else return isPointOver(x,y);
    }
}
