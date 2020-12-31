package UI;

public class UICounter extends  UIBase {
    int value;

    public UICounter(int x, int y, int w, int h, UILayer layer) {
        super(x, y, w, h, layer);
        value=0;
    }
}
