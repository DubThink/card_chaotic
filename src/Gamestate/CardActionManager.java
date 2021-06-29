package Gamestate;

public class CardActionManager {
    private static Card selection;

    public static void selectCard(Card card) {
        selection = card;
    }

    public static Card getSelection() {
        return selection;
    }
}
