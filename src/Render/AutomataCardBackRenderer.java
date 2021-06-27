package Render;

import aew.Util;
import core.AdvancedApplet;
import core.AdvancedGraphics;

import static Gamestate.CardDefinition.*;

public class AutomataCardBackRenderer extends CardBackRenderer {
    protected boolean[][] data;
    protected boolean[][] dataOld;
    protected int timeSinceLastRun=0;
    protected final int stepInMS=1000;
    protected final int fadeTimeInMS=500;


    @Override
    public void initialize(AdvancedApplet app) {
        data = new boolean[CB_WIDTH_U][CB_HEIGHT_U];
        dataOld = new boolean[CB_WIDTH_U][CB_HEIGHT_U];
        for (int x = 0; x < data.length; x++) {
            for (int y = 0; y < data[0].length; y++) {
                data[x][y] = app.random(0,1)>.75f;
            }
        }
    }

    @Override
    public void updateCardBack(AdvancedApplet baseApp, AdvancedGraphics p, float t, int dt) {
        drawDefaultBackground(p);
        timeSinceLastRun+=dt;
        if(timeSinceLastRun>stepInMS){
            timeSinceLastRun-=stepInMS;
            for (int x = 0; x < data.length; x++) {
                for (int y = 0; y < data[0].length; y++) {
                    dataOld[x][y] = rule(x,y);
                }
            }

            // swap
            boolean[][] tmp=data;
            data= dataOld;
            dataOld =tmp;
        }

        float offset = CB_OFFSET + CARD_SCALE*0.5f;
        p.noStroke();
        for (int x = 0; x < data.length; x++) {
            for (int y = 0; y < data[0].length; y++) {
                if(timeSinceLastRun>fadeTimeInMS || data[x][y]== dataOld[x][y]) {
                    p.fill(data[x][y] ? 200 : 30);
                } else {
                    // LERP LERP
                    int startVal = dataOld[x][y] ? 200 : 30;
                    int endVal = data[x][y] ? 200 : 30;
                    p.fill(Util.lerp(timeSinceLastRun/(float)fadeTimeInMS, 0,1,startVal,endVal));
                }
                p.ellipse(offset + x*CARD_SCALE, offset + y*CARD_SCALE, 10,10);
            }
        }
    }

    protected boolean rule(int x, int y) {
        return thirty(x, y);
    }

    protected boolean thirty(int x, int y) {
        return rval(x-1,y-1) ^ (rval(x-1,y)||rval(x-1,y+1));
    }


        protected boolean conway(int x, int y) {
        int ct = countNeighbors(x,y);
        if(ct<2 || ct>3)
            return false;
        if(ct==3)
            return true;
        return rval(x,y);
    }

    protected int countNeighbors(int x, int y){
        int ct = 0;
        if(rval(x-1,y-1))ct++;
        if(rval(x-1,y))ct++;
        if(rval(x-1,y+1))ct++;
        if(rval(x,y-1))ct++;
        // me
        if(rval(x,y+1))ct++;
        if(rval(x+1,y-1))ct++;
        if(rval(x+1,y))ct++;
        if(rval(x+1,y+1))ct++;
        return ct;
    }

    protected boolean rval(int x, int y){
        return ri(ri(data,x),y);
    }

    private static boolean ri(boolean[] array, int idx){
        idx = idx%array.length;
        if(idx<0)
            idx+=array.length;
        return array[idx];
    }

    private static <T> T ri(T[] array, int idx){
        idx = idx%array.length;
        if(idx<0)
            idx+=array.length;
        return array[idx];
    }
}
