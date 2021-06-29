package Gamestate;

public interface CardStackListener {
    void addedCard(CardStack stack, int idx);
    void onOwnershipChange(CardStack stack, boolean isOwner);

}
