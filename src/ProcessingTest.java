/**
 * Created by benja on 2/27/2017.
 */
import processing.core.PApplet;

public class ProcessingTest extends PApplet{
    int scoreA=301;
    int scoreB=301;
    int scoreC=301;
    int totalRound=0;
    int active=0;
    public void changeActive(int amt){
        totalRound-=amt;
        if(active==0)scoreA+=amt;
        else if(active==1)scoreB+=amt;

        else scoreC+=amt;
    }
    public void settings(){
        fullScreen(P2D,-2);//size(1600, 900);
    }

    public void  setup(){
        background(0);
    }
    public void draw() {
        background(0);
        textAlign(CENTER);
        noStroke();

        fill(10,180,50);
        rect(0,height-(active==0?80:20),width/3,80);
        fill(50,80,180);
        rect(width/3,height-(active==1?80:20),width/3,80);
        fill(170,80,60);
        rect(2*width/3,height-(active==2?80:20),width/3,80);
        textSize(200);
        if(scoreA<=0){
            fill(200,70,70);
        }else
            fill(200);
        text(scoreA,width/6,2*height/5);

        if(scoreB<=0){
            fill(200,70,70);
        }else
            fill(200);
        text(scoreB,3*width/6,2*height/5);

        if(scoreC<=0){
            fill(200,70,70);
        }else
            fill(200);
        text(scoreC,5*width/6,2*height/5);

        fill(150);
        textSize(150);
        text(totalRound,(1+2*active)*width/6,4*height/5);

    }

    public void keyPressed(){
        int mod=-1;
        if(key==' '){
            active=(active+1)%3;
            totalRound=0;
        }
        if(key==ENTER){
            scoreA=301;
            scoreB=301;
            scoreC=301;
            totalRound=0;
        }
        if(key=='1')changeActive(1*mod);
        if(key=='2')changeActive(2*mod);
        if(key=='3')changeActive(3*mod);
        if(key=='4')changeActive(4*mod);
        if(key=='5')changeActive(5*mod);
        if(key=='6')changeActive(6*mod);
        if(key=='7')changeActive(7*mod);
        if(key=='8')changeActive(8*mod);
        if(key=='9')changeActive(9*mod);
        if(key=='0')changeActive(10*mod);
        if(key=='q')changeActive(11*mod);
        if(key=='w')changeActive(12*mod);
        if(key=='e')changeActive(13*mod);
        if(key=='r')changeActive(14*mod);
        if(key=='t')changeActive(15*mod);
        if(key=='y')changeActive(16*mod);
        if(key=='u')changeActive(17*mod);
        if(key=='i')changeActive(18*mod);
        if(key=='o')changeActive(19*mod);
        if(key=='p')changeActive(20*mod);
    }

    public static void main(String... args){
        PApplet.main("ProcessingTest");
    }
}
