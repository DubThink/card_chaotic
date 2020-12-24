import processing.core.PApplet;
public class Emulsion extends PApplet{

    public void settings(){
        fullScreen(P3D,-2);
    }

    float ax,ay,az;
    public void draw(){
        background(10,10,30);
        //fill(10,10,30,10);
        //rect(0,0,width,height);
        noFill();
        pushMatrix();
        translate(width/2,height/2);
        rotateY(frameCount/255f);
        stroke(255);
        box(200);
        if(frameCount%63==0){
            ax=round(random(-1,1));
            ay=round(random(-1,1));
            az=round(random(-1,1));
        }
        pushMatrix();

        stroke(255,255-5*(frameCount%63));
        for(int i=1;i<10;i++){
            rotate((i/10f)*0.005f*(frameCount%63),ax,ay,az);
            stroke(255,(i%10f)+(i/10f)*(255-5*(frameCount%63)));
            box(200+(i/1f)*(frameCount%63));
        }

        popMatrix();
        popMatrix();
    }

    public static void main(String... args){
        PApplet.main("Emulsion");
    }
}