package core;

import Globals.Debug;
import core.AdvancedApplet;
import network.event.CacheImageNetEvent;
import processing.core.PImage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class ImageLoader {
    private AdvancedApplet appletHandle;
    private HashMap<String, PImage> imageMap;
    private PImage nullimg;
    private Random random;
    private static final String userCardPath = "user/card/";
    private static final String cachePath = "cache/";
    private static final String cacheImagePrefix = "cachei_";

    public ImageLoader(AdvancedApplet appletHandle) {
        this.appletHandle = appletHandle;
        imageMap = new HashMap<>();
        nullimg = appletHandle.loadImage("null.jpg");
        if(nullimg==null)
            throw new RuntimeException("null.jpg not loaded");

        random = new Random(System.nanoTime());
    }

//    public PImage getImage(String name){
//        return null;
//    }

    public PImage getCardImage(String name){
        if (imageMap.containsKey(name))
            return imageMap.get(name);
        boolean cached = name.startsWith(cacheImagePrefix);
        PImage img = cached?
                appletHandle.loadImage(cachePath+name):
                appletHandle.loadImage(userCardPath+name);
        if(img==null){
            img=nullimg;
            if(cached)
                throw new RuntimeException("Card "+name+" is not cached, but has been requested.");
        }
        imageMap.put(name, img);
        return img;
    }

    public void handleEvent(CacheImageNetEvent event){
        // todo warning on already cached
        event.image.save(cachePath+event.name);
    }

    /**
     * Puts a new image in the cache. Server-only.
     * @param image the image to cache
     * @return the filename in the cache
     */
    public String cacheNewImage(PImage image){
        // todo server-side check
        long id = random.nextLong();
        String fname = cacheImagePrefix+Long.toHexString(id)+".jpg";
        image.save(cachePath+fname);
        imageMap.put(fname,image);
        return fname;
    }

    public boolean isCardImageValid(String name){
        return getCardImage(name)==nullimg;
    }

    public void launchCardFolder(){
        try {
            Desktop.getDesktop().open(appletHandle.dataFile(userCardPath));
        }
        catch (IOException ignored){};
    }

    public void uncacheCardImage(String name){
        imageMap.remove(name);
    }
}
