package UI;

public class UIButton extends UIBase{
    Action onAction;
    Action offAction;
    boolean toggle;

    public UIButton(int x, int y, int w, int h, Action action) {
        super(x, y, w, h);
        this.onAction = action;
    }

    @Override
    public boolean handleMouseInput(boolean down, int button, int x, int y) {
        return super.handleMouseInput(down, button, x, y);
    }
}
