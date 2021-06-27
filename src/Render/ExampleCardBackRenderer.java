package Render;

import Globals.Style;
import Schema.SchemaTypeID;
import core.AdvancedApplet;
import core.AdvancedGraphics;
import processing.core.PConstants;

import static Gamestate.CardDefinition.*;

public class ExampleCardBackRenderer extends CardBackRenderer{
    private float x,y;
    private int vx,vy;
    private float velocity=200;

    int bounce=0;

    private static final int ICON_WIDTH=90;
    private static final int ICON_HEIGHT=44;

    /**
     * Called once on create
     * @param app the base processing application (used for random() and noise() mostly)
     */
    @Override
    public void initialize(AdvancedApplet app) {
        x = app.random(CARD_H_W-20, CARD_H_W+20);
        y = app.random(CARD_H_H-20, CARD_H_H+20);
        vx=1;
        vy=1;
    }

    /**
     * Called once per frame
     * @param baseApp the base processing application (used for random() and noise() mostly)
     * @param p the target for rendering. Will already be active, but not cleared.
     * @param t (float) process time in seconds
     * @param dt (int) delta time in milliseconds
     */
    @Override
    public void updateCardBack(AdvancedApplet baseApp, AdvancedGraphics p, float t, int dt) {
        // unless you're doing fancy fade stuff, just call this
        drawDefaultBackground(p);

        // update stuff
        x+=velocity*vx*dt/1000f;
        y+=velocity*vy*dt/1000f;

        // CB_OFFSET is the distance in pixels to the corner of the renderable area on the card back
        float minPos = CB_OFFSET;
        float maxPosHorizontal = CB_OFFSET + CB_WIDTH - ICON_WIDTH;
        float maxPosVertical = CB_OFFSET + CB_HEIGHT - ICON_HEIGHT;

        if(x<minPos) {
            vx = 1;
            bounce++;
        }
        if(x>maxPosHorizontal) {
            vx = -1;
            bounce++;
        }
        if(y<minPos) {
            vy = 1;
            bounce++;
        }
        if(y>maxPosVertical) {
            vy = -1;
            bounce++;
        }

        p.fill(100);
        p.rect(x,y,ICON_WIDTH,ICON_HEIGHT);
        p.textAlign(PConstants.CENTER,PConstants.CENTER);
        // activate a font
        Style.getFont(Style.F_IMPACT, Style.FONT_36).apply(p);
        // changing color from the bits of the bounce counter
        p.fill((bounce&1)!=0?0:255, (bounce&2)!=0?0:255, (bounce&4)!=0?0:255);
        p.text("DVD", x+ICON_WIDTH/2f, y+ICON_HEIGHT/2f);
    }

}
