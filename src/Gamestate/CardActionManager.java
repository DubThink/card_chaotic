package Gamestate;

import Globals.GlobalEnvironment;

public class CardActionManager {
    private static Card selection;

    // used for pulsing the selected card
    private static int lastUpdateMS;

    public static void selectCard(Card card) {
        if(card!=selection)
            lastUpdateMS = GlobalEnvironment.simTimeMS();
        selection = card;
    }

    public static Card getSelection() {
        return selection;
    }

    public static int getLastUpdateMS() {
        return lastUpdateMS;
    }
}
