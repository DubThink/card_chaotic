package Server;

import UI.Action;
import UI.UIButton;
import network.NetEvent;

import static Server.ServerEnvironment.phasePanel;

public class PregamePhase implements ServerGamePhase {
    UIButton startButton;
    boolean shouldStart;

    public PregamePhase() {
        startButton = phasePanel.addChild(new UIButton(10, 10, 150, 30, "Start Game", new Action() {
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
    public boolean processNetEvent(SvPlayer player, NetEvent event) {
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
        phasePanel.removeChild(startButton);
    }

    @Override
    public String getPhaseName() {
        return "Pregame";
    }
}
