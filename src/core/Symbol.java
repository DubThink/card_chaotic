package core;

import processing.core.PImage;

public class Symbol {
    public static float wPad = 0.08f; // in font size units
    public static float vOffset = 0.08f; // in font size units
    public PImage image;
    public char c;
    // in font size units
    public float mWidth, mHeight;
    public void setMSize(float fontSize){
        mWidth = image.width/fontSize;
        mHeight = image.height/fontSize;
    }
}
