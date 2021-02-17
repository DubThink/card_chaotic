package UI;

import core.AdvancedApplet;
import processing.core.PImage;

public class UIImage extends UIBase {
    public PImage image;
    int ix1, iy1, ix2, iy2; // crop
    boolean isCropping;
    boolean isScaling=true;


    public UIImage(int x, int y, PImage image){
        super(x,y, image.width, image.height);
        this.image = image;
        ix1=0;
        iy1=0;
        ix2=1;
        iy2=1;
        isScaling = false;
    }

    public UIImage(int x, int y, int w, int h, PImage image) {
        super(x, y, w, h);
        this.image = image;
        ix1=0;
        iy1=0;
        ix2=1;
        iy2=1;
    }

    /**
     * Sets UV coords for cropping in pixel coords (as ints)
     */
    public UIImage setCrop(int x1, int y1, int x2, int y2) {
        ix1 = x1;
        iy1 = y1;
        ix2 = x2;
        iy2 = y2;
        isCropping=true;
        return this;
    }

    @Override
    protected void _draw(AdvancedApplet p) {
        super._draw(p);
        if (isCropping){
            p.image(image, cx, cy, cw, ch, ix1,iy1,ix2,iy2);
        } else {
            if(isScaling)
                p.image(image, cx, cy, cw, ch);
            else
                p.image(image,cx,cy);
        }
    }

    public UIImage setCropping(boolean cropping) {
        isCropping = cropping;
        return this;
    }

    public UIImage setScaling(boolean scaling) {
        isScaling = scaling;
        return this;
    }

    @Override
    public String debugName() {
        return "UIImage["+image+"]";
    }
}
