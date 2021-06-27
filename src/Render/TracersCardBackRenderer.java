package Render;

import aew.Util;
import core.AdvancedApplet;
import core.AdvancedGraphics;
import processing.core.PVector;

import static processing.core.PApplet.cos;
import static processing.core.PApplet.sin;
import static processing.core.PConstants.TWO_PI;
import static Gamestate.CardDefinition.*;

public class TracersCardBackRenderer extends CardBackRenderer{
    private int _blanked;
    private PVector[] tracers;

    @Override
    public void initialize(AdvancedApplet app) {
        tracers = new PVector[400];
        for (int i=0;i<tracers.length;i++) {
            float dir = app.random(0,TWO_PI);
            tracers[i]=new PVector(
                    0,0);
            tracers[i].set(CARD_H_W+cos(dir)*40,CARD_H_H+sin(dir)*40);

        }
    }

    @Override
    public void updateCardBack(AdvancedApplet baseApp, AdvancedGraphics p, float t, int dt) {

        //p.clear();

        // blanker
        // TODO figure out why the heck the blanker doesn't work on the first try
        if(_blanked<2){
            p.noStroke();
            p.fill(0);
            drawBackgroundRect(p);
            _blanked++;
        }

        p.noStroke();
        p.fill(0,3);
        // background
        p.rect(0, 0, CARD_WIDTH, CARD_HEIGHT, CARD_SCALE /2f);

        p.noStroke();
        p.fill(255);
        p.strokeWeight(1);
        int bob=0;


        for (PVector tracer :
                tracers) {
//            if(tracer==tracers[0]){
//                p.fill(255,255,0);
//            }else {
//                p.fill(255);
//            }
            // update tracer pos
            if(isPointOutsideCard(tracer)){
                float dir = baseApp.random(0,TWO_PI);
                tracer.set(CARD_H_W+cos(dir)*40,CARD_H_H+sin(dir)*40);
            }
            //dt+=sin(t*5)*.02;
            // step tracer
            PVector currentDelta = new PVector(tracer.x-CARD_H_W,tracer.y-CARD_H_H);
            float d=currentDelta.mag();
            currentDelta.rotate(1f);
            currentDelta.setMag(0.4f);
            currentDelta.add(sampleNoise(tracer, baseApp,t));
            currentDelta.mult(0.126f*dt); //min(dt,20));
            //currentDelta.mult(sin(t*2 + bob/10.f)+0.25f);
            tracer.add(currentDelta);
            p.fill(127+100*currentDelta.x,127+100*currentDelta.y,255);
            // render
            float sz=2+8*d/CARD_H_H;
            p.ellipse(tracer.x, tracer.y, sz, sz);
            bob++;
        }


        p.stroke(255,200,50);
    }

    private static boolean isPointOutsideCard(PVector p){
        return !Util.in(p.x,CARD_SCALE*2.5f,CARD_WIDTH-CARD_SCALE*2.5f) ||
                !Util.in(p.y,CARD_SCALE*2.5f,CARD_HEIGHT-CARD_SCALE*2.5f);
    }

    private static PVector sampleNoise(PVector point, AdvancedApplet a, float t){
        float ns=.02f;
        t*=0.1;
        return new PVector(a.noise(point.x*ns,point.y*ns,t)-0.5f,a.noise(point.x*ns+1000,point.y*ns+1000,t)-0.5f);
    }
}
