package bpw;

import core.AdvancedGraphics;

import java.awt.*;

import static bpw.Util.lerp;


public class PUtil {
    public static int mixColor(AdvancedGraphics ag, int a, int b, float v){
        return ag.color(lerp(ag.red(a),ag.red(b),v),
                lerp(ag.green(a),ag.green(b),v),
                lerp(ag.blue(a),ag.blue(b),v),
                lerp(ag.alpha(a),ag.alpha(b),v));

    }
}
