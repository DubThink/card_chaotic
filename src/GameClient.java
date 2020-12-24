import Globals.DebugConstants;
import UI.Action;
import UI.Style;
import UI.UIBase;
import UI.UIButton;
import core.AdvancedApplet;
import processing.core.PApplet;

import java.awt.*;

import static java.awt.event.KeyEvent.*;

public class GameClient extends AdvancedApplet {

    public void settings(){
        size(1600,900, "TestGraphics");

//        fullScreen(P3D,-2);
    }

    float ax,ay,az;
    UIBase root;

    @Override
    public void setup() {
        Style.loadFonts(this);
        root = new UIBase(10,10,width/2, height/2);
        Action test = new Action() {
            @Override
            public void action() {
                root.setPos(100,100);
            }
        };
        root.addChild(new UIButton(10,10,200,40, "abcde", test));
        Style.font32.font.initInjection();
    }

    public void draw(){
        background(10,10,30);
        //fill(10,10,30,10);
        //rect(0,0,width,height);
//        noFill();
//        pushMatrix();
//        translate(width/2,height/2);
//        rotateY(frameCount/255f);
//        stroke(255);
//        box(200);
//        if(frameCount%63==0){
//            ax=round(random(-1,1));
//            ay=round(random(-1,1));
//            az=round(random(-1,1));
//        }
//        pushMatrix();
//
//        stroke(255,255-5*(frameCount%63));
//        for(int i=1;i<10;i++){
//            rotate((i/10f)*0.005f*(frameCount%63),ax,ay,az);
//            stroke(255,(i%10f)+(i/10f)*(255-5*(frameCount%63)));
//            box(200+(i/1f)*(frameCount%63));
//        }
//
//        popMatrix();
//        popMatrix();
        noFill();
        noStroke();
        root.updateFocus(mouseX, mouseY);
        root.render(this);
        Style.font32.font.debuggy();
    }

    @Override
    public void keyPressed() {
        println(key, keyCode);
        if(keyCode == VK_F3)
            DebugConstants.renderUIDebug = !DebugConstants.renderUIDebug;
    }

    @Override
    public void mousePressed() {
        root.handleMouseInput(true, mouseButton, mouseX, mouseY);
    }

    @Override
    public void mouseReleased() {
        root.handleMouseInput(false, mouseButton, mouseX, mouseY);
    }

    public static void main(String... args){
        PApplet.main("GameClient");
    }
}