package core;

import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PJOGL;

public class AdvancedPJOGL extends PJOGL {

    public AdvancedPJOGL(PGraphicsOpenGL pg) {
        super(pg);
    }

    @Override
    protected int getTextWidth(Object font, char[] buffer, int start, int stop) {
        return super.getTextWidth(font, buffer, start, stop);
    }
}
