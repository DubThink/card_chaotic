package core;

import core.AdvancedApplet;
import processing.core.PImage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ImageLoader {
    AdvancedApplet appletHandle;
    HashMap<String, PImage> imageMap;
    private PImage nullimg;

    public ImageLoader(AdvancedApplet appletHandle) {
        this.appletHandle = appletHandle;
        imageMap = new HashMap<>();
        nullimg = appletHandle.loadImage("null.jpg");
        if(nullimg==null)
            throw new RuntimeException("null.jpg not loaded");
    }

//    public PImage getImage(String name){
//        return null;
//    }

    public PImage getUserImage(String name){
        if (imageMap.containsKey(name))
            return imageMap.get(name);
        PImage img = appletHandle.loadImage("user/"+name);
        imageMap.put(name, img);
        return img;

    }
    public PImage getCardImage(String name){
        if (imageMap.containsKey(name))
            return imageMap.get(name);
        PImage img = appletHandle.loadImage("user/card/"+name);
        if(img==null)
            img=nullimg;
        imageMap.put(name, img);
        return img;

    }

    public boolean isCardImageValid(String name){
        return getCardImage(name)==nullimg;
    }

    public void launchCardFolder(){
        try {
            Desktop.getDesktop().open(appletHandle.dataFile("user\\card\\"));
        }
        catch (IOException ignored){};
    }

    public void uncacheCardImage(String name){
        if (imageMap.containsKey(name))
            imageMap.remove(name);
    }
}
