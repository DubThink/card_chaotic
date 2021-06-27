package Render;

import core.AdvancedApplet;
import core.AdvancedGraphics;
import static Gamestate.CardDefinition.*;


public abstract class CardBackRenderer {
    public static final int CB_WIDTH_U = 15;
    public static final int CB_HEIGHT_U = 23;
    public static final int CB_WIDTH = CB_WIDTH_U*CARD_SCALE;
    public static final int CB_HEIGHT = CB_HEIGHT_U*CARD_SCALE;
    public static final float CB_OFFSET = 2.5f*CARD_SCALE;

//    public static final int CB_HALF_WIDTH = CB_WIDTH/2;
//    public static final int CB_HALF_HEIGHT = CB_HEIGHT/2;


    public abstract void initialize(AdvancedApplet app);

    public abstract void updateCardBack(AdvancedApplet baseApp, AdvancedGraphics p, float t, int dt);

    protected static void drawBackgroundRect(AdvancedGraphics p) {
        p.rect(0, 0, CARD_WIDTH, CARD_HEIGHT, CARD_SCALE /2f);
    }

    protected static void drawDefaultBackground(AdvancedGraphics p) {
        p.clear();
        p.noStroke();
        p.fill(0);
        drawBackgroundRect(p);
    }

    }
