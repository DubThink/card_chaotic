package Server;

import UI.Action;
import UI.UIButton;
import network.NetEvent;

import static Globals.GlobalEnvironment.*;

public class PregamePhase implements ServerGamePhase {
    UIButton startButton;
    boolean shouldStart;

    public PregamePhase() {
        startButton = uiRoot.addChild(new UIButton(10, 10, 100, 25, "Start Game", new Action() {
            @Override
            public void action() {
                shouldStart=true;
            }
        }));
    }

    @Override
    public void updateStep(int dt) {

    }

    @Override
    public boolean processNetEvent(NetEvent event) {
        return false;
    }

    @Override
    public boolean shouldEnd() {
        return shouldStart;
    }

    @Override
    public ServerGamePhase createNextPhase() {
        return new SvBiddingIntroPhase();
    }

    @Override
    public void cleanup() {
        uiRoot.removeChild(startButton);
    }

    @Override
    public String getPhaseName() {
        return "Pregame";
    }
}
