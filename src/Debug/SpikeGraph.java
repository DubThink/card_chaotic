package Debug;

import bpw.Util;
import core.AdvancedGraphics;

import java.util.LinkedList;

public class SpikeGraph {
    private static final int MAX_SIZE = 300;

    private float history[];
    private int nextIndex;
    private float thisFrame;
    private int thisFrameCount;
    private float maxVal;
    private float minVal;

    public boolean sumMode;

    public SpikeGraph() {
        history = new float[MAX_SIZE];
    }

    public SpikeGraph(boolean sumMode) {
        this();
        this.sumMode = sumMode;
    }

    private float get(int i){
        final int idx = (i+nextIndex)%MAX_SIZE;
        return history[idx];
    }

    public void reMinMax(){
        maxVal = 0;
        minVal = 0;
        for(int i=0;i<MAX_SIZE;i++){
            maxVal = Util.max(history[i],maxVal);
            minVal = Util.min(history[i],minVal);
        }
    }

    void render(AdvancedGraphics p, float x, float y){


        p.pushMatrix();
        p.translate(x,y);
        p.noFill();
        p.stroke(127);
        p.strokeWeight(1);
        p.rect(0,0,MAX_SIZE,75);

        if(maxVal-minVal<=0) {
            p.popMatrix();
            return;
        }
        p.text(maxVal,MAX_SIZE,12);
        p.text(minVal,MAX_SIZE,75);

        final float scale = 75/(maxVal-minVal);
        final float zeroPoint= maxVal*scale;
        p.stroke(255);
        for(int i=0; i<MAX_SIZE; i++){
            p.line(i,zeroPoint,i,zeroPoint - scale*get(i));
        }
        p.popMatrix();
    }

    void advanceFrame(){
        if(thisFrameCount>0) {
            if(!sumMode)
                thisFrame /= thisFrameCount;
            history[nextIndex++]=thisFrame;
            nextIndex%=MAX_SIZE;

            maxVal = Util.max(maxVal, thisFrame);
            minVal = Util.min(minVal, thisFrame);

            thisFrame = 0;
            thisFrameCount = 0;
        } else {
            history[nextIndex++]=0;
            nextIndex%=MAX_SIZE;
        }
    }

    public void addVal(float val){
        thisFrameCount++;
        thisFrame += val;
    }
}
